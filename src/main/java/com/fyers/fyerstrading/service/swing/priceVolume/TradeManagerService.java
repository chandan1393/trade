package com.fyers.fyerstrading.service.swing.priceVolume;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fyers.fyerstrading.entity.TradeExecution;
import com.fyers.fyerstrading.entity.TradeSetup;
import com.fyers.fyerstrading.entity.TradeStatus;
import com.fyers.fyerstrading.model.GTTModifiedOrderResponse;
import com.fyers.fyerstrading.model.OrderResponse;
import com.fyers.fyerstrading.repo.TradeExecutionRepository;
import com.fyers.fyerstrading.repo.TradeSetupRepository;
import com.fyers.fyerstrading.service.FyersApiService;
import com.tts.in.model.GTTLeg;
import com.tts.in.model.GTTModel;

@Service
public class TradeManagerService {
	
	@Autowired
	private TradeExecutionRepository tradeExecutionRepo;
	@Autowired
	private TradeSetupRepository tradeSetupRepo;

	@Autowired
	private FyersApiService fyersApiService;
	

	@Transactional
	public void checkAndPlaceOrModifyEntryGTTOrder(TradeSetup setup) {

			String existingOrderId = setup.getGttEntryOrderId()!=null?setup.getGttEntryOrderId():"";

			String newOrderId = placeOrModifyEntryGTTOrder(setup, existingOrderId);

			if (!newOrderId.isEmpty()) {
				setup.setTradeStatus(TradeStatus.PENDING_ENTRY);
				setup.setSyncTime(LocalDateTime.now());
				setup.setGttEntryOrderId(newOrderId);
				tradeSetupRepo.save(setup);
			}
	}

	
	
	public String placeOrModifyEntryGTTOrder(TradeSetup setup, String gttEntryOrderId) {
		GTTModel model = new GTTModel();
		model.Side = 1;
		model.Symbol = setup.getStockSymbol();
		model.productType = "CNC";
		model.addGTTLeg("leg1", new GTTLeg(setup.getEntryPrice().intValue(), setup.getEntryPrice().intValue(),
				setup.getPositionSize()));

		if (setup.getGttEntryOrderId() == null) {
			OrderResponse newResponse = fyersApiService.PlaceGTTOrder(List.of(model));
			return newResponse.getId();

		} else {
			model.Id = gttEntryOrderId;

			GTTModifiedOrderResponse modifiedResponse = fyersApiService.ModifyGTTOrder(List.of(model));

			// Check if modification failed due to invalid order id (-51)
			if (modifiedResponse != null && modifiedResponse.getCode() == -51) {
				OrderResponse newResponse = fyersApiService.PlaceGTTOrder(List.of(model));
				return newResponse.getId();
			}

			return setup.getGttEntryOrderId();

		}

	}

	public void gttSellOrderAfterEntry(TradeExecution tradeExecution) {

		Map<String, String> gttOrderIds = placeThreeGTTOrders(tradeExecution);

	    if (!gttOrderIds.isEmpty()) {
	        // Save all three order IDs
	        tradeExecution.setGttExitOrderT1Id(gttOrderIds.get("T1"));
	        tradeExecution.setGttExitOrderT2Id(gttOrderIds.get("T2"));
	        tradeExecution.setGttExitOrderTSLId(gttOrderIds.get("SL"));

	        tradeExecution.setTradeStatus(TradeStatus.GTT_SELL_ORDERS_PLACED);
			tradeExecution.setRemarks(
					String.format("Placed 3 GTT Orders at %s | T1=%.2f, T2=%.2f, SL=%.2f", LocalDateTime.now(),
							tradeExecution.getTarget1(), tradeExecution.getTarget2(), tradeExecution.getStopLoss()));
			tradeExecution.setSyncTime(LocalDateTime.now());
	        tradeExecutionRepo.save(tradeExecution);
	    }

	}

	public Map<String, String> placeThreeGTTOrders(TradeExecution execution) {
	    Map<String, String> orderIds = new HashMap<>();

	    int halfQty, remainingQty, stopQty;
	    if (execution.getPositionSize() > 1) {
	        halfQty = execution.getPositionSize() / 2;
	        remainingQty = execution.getPositionSize() - halfQty;
	        stopQty = execution.getPositionSize(); // Full exit on SL
	    } else {
	        halfQty = 1;
	        remainingQty = 0; // no T2 if only 1 qty
	        stopQty = 1;
	    }

	    // Target 1
	    String t1OrderId = placeOrModifyTarget1GTTOrder(execution, halfQty);
	    orderIds.put("T1", t1OrderId);

	    // Target 2 (only if > 0 qty remains)
	    if (remainingQty > 0) {
	        String t2OrderId = placeOrModifyTarget2GTTOrder(execution, remainingQty);
	        orderIds.put("T2", t2OrderId);
	    }

	    // Stop Loss
	    String slOrderId = placeOrModifyStopLossGTTOrder(execution, stopQty);
	    orderIds.put("SL", slOrderId);

	    return orderIds;
	}
	
	
	public String placeOrModifyTarget1GTTOrder(TradeExecution execution, int qty) {
		GTTModel t1Model = new GTTModel();
		t1Model.Side = -1;
		t1Model.Symbol = execution.getStockSymbol();
		t1Model.productType = "CNC";

		t1Model.addGTTLeg("leg1",
				new GTTLeg(execution.getTarget1().intValue(), execution.getTarget1().intValue(), qty));

		if (execution.getGttExitOrderT1Id() == null) {
			// Place new order
			OrderResponse response = fyersApiService.PlaceGTTOrder(List.of(t1Model));
			return response.getId();
		} else {
			// Modify existing order
			t1Model.Id = execution.getGttExitOrderT1Id();

			GTTModifiedOrderResponse modifiedResponse = fyersApiService.ModifyGTTOrder(List.of(t1Model));
			// Check if modification failed due to invalid order id (-51)
			if (modifiedResponse != null && modifiedResponse.getCode() == -51) {
				OrderResponse newResponse = fyersApiService.PlaceGTTOrder(List.of(t1Model));
				return newResponse.getId();
			}

			return execution.getGttExitOrderT1Id();
		}

	}

	public String placeOrModifyTarget2GTTOrder(TradeExecution execution, int qty) {
		GTTModel t2Model = new GTTModel();
		t2Model.Side = -1;
		t2Model.Symbol = execution.getStockSymbol();
		t2Model.productType = "CNC";

		t2Model.addGTTLeg("leg1",
				new GTTLeg(execution.getTarget2().intValue(), execution.getTarget2().intValue(), qty));

		if (execution.getGttExitOrderT2Id() == null) {
			OrderResponse response = fyersApiService.PlaceGTTOrder(List.of(t2Model));
			return response.getId();
		} else {
			t2Model.Id = execution.getGttExitOrderT2Id();
			GTTModifiedOrderResponse modifiedResponse = fyersApiService.ModifyGTTOrder(List.of(t2Model));

			// Check if modification failed due to invalid order id (-51)
			if (modifiedResponse != null && modifiedResponse.getCode() == -51) {
				OrderResponse newResponse = fyersApiService.PlaceGTTOrder(List.of(t2Model));
				return newResponse.getId();
			}
			return execution.getGttExitOrderT2Id();
		}

	}

	public String placeOrModifyStopLossGTTOrder(TradeExecution execution, int qty) {
		GTTModel slModel = new GTTModel();
		slModel.Side = -1;
		slModel.Symbol = execution.getStockSymbol();
		slModel.productType = "CNC";

		slModel.addGTTLeg("leg1",
				new GTTLeg(execution.getStopLoss().intValue(), execution.getStopLoss().intValue(), qty));

		if (execution.getGttExitOrderTSLId() == null) {
			OrderResponse response = fyersApiService.PlaceGTTOrder(List.of(slModel));
			return response.getId();
		} else {
			slModel.Id = execution.getGttExitOrderTSLId();
			GTTModifiedOrderResponse modifiedResponse = fyersApiService.ModifyGTTOrder(List.of(slModel));

			if (modifiedResponse != null && modifiedResponse.getCode() == -51) {
				OrderResponse newResponse = fyersApiService.PlaceGTTOrder(List.of(slModel));
				return newResponse.getId();
			}
			return execution.getGttExitOrderTSLId();
		}

	}

	private boolean isValidGttId(String gttId) {
		return gttId != null && !gttId.trim().isEmpty();
	}



}
