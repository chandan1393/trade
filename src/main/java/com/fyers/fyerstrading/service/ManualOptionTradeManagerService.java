package com.fyers.fyerstrading.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fyers.fyerstrading.entity.ManualOptionTrade;
import com.fyers.fyerstrading.model.OrderResponse;
import com.fyers.fyerstrading.model.ManualOrderRequest;
import com.fyers.fyerstrading.model.NetPosition;
import com.fyers.fyerstrading.repo.ManualOptionTradeRepository;
import com.tts.in.model.GTTLeg;
import com.tts.in.model.GTTModel;

@Service
public class ManualOptionTradeManagerService {

	private final ManualOptionTradeRepository tradeRepo;
	private final FyersApiService fyersApiService;

	// runtime local maps for TSL checks
	private final Map<String, Double> localHighMap = new ConcurrentHashMap<>();
	private final Map<String, Double> localLastTSLMap = new ConcurrentHashMap<>();

	public ManualOptionTradeManagerService(ManualOptionTradeRepository tradeRepo, FyersApiService fyersApiService) {
		this.tradeRepo = tradeRepo;
		this.fyersApiService = fyersApiService;
	}

	// ---------- place manual order from request ----------
	/*
	 * @Transactional public ManualOptionTrade placeManualOrder(ManualOrderRequest
	 * req) { // save DB record ManualOptionTrade trade = new ManualOptionTrade();
	 * trade.setSymbol(req.symbol); trade.setNetQty(req.qty);
	 * trade.setBuyAvg(req.entryPrice); trade.setStopLoss(req.stopLoss);
	 * trade.setTarget(req.target); trade.setEntryTime(LocalDateTime.now());
	 * trade.setOpen(true); tradeRepo.save(trade);
	 * 
	 * // place entry order String orderId =
	 * fyersApiService.placeBuyOrder(req.symbol, req.qty, req.entryPrice);
	 * trade.setFyBuyOrderId(orderId); tradeRepo.save(trade);
	 * 
	 * // place OCO for exit (single target + SL) placeOrUpdateSingleOco(trade,
	 * false);
	 * 
	 * return trade; }
	 */

	/*
	 * // ---------- place order using DB values ----------
	 * 
	 * @Transactional public ManualOptionTrade placeOrderFromDb(String symbol) {
	 * ManualOptionTrade trade = tradeRepo.findBySymbolAndOpenTrue(symbol); if
	 * (trade == null) throw new RuntimeException("No active trade found: " +
	 * symbol);
	 * 
	 * // place buy String orderId = fyersApiService.placeBuyOrder(symbol,
	 * trade.getNetQty(), trade.getBuyAvg()); trade.setFyBuyOrderId(orderId);
	 * tradeRepo.save(trade);
	 * 
	 * // place OCO placeOrUpdateSingleOco(trade, false); return trade; }
	 */

	// ---------- save target & sl + place OCO ----------
	@Transactional
	public void saveTargetAndSl(String symbol, Double sl, Double target) {
		ManualOptionTrade trade = tradeRepo.findBySymbolAndOpenTrue(symbol);
		if (trade == null)
			throw new RuntimeException("No active trade: " + symbol);

		trade.setStopLoss(sl);
		trade.setTarget(target);
		tradeRepo.save(trade);

		placeOrUpdateSingleOco(trade, false);
	}

	// ---------- place or update single OCO for (SL + target) ----------
	@Transactional
	public void placeOrUpdateSingleOco(ManualOptionTrade trade, boolean isTslUpdate) {
		if (trade == null)
			return;
		if (trade.getStopLoss() == null || trade.getTarget() == null)
			throw new IllegalArgumentException("SL or target missing for " + trade.getSymbol());

		String existingGttId = trade.getFyOcoOrderId();
		int qty = trade.getNetQty();

		// convert to int prices for GTTLeg (broker accepts ints)
		int tLimit = (int) Math.round(trade.getTarget());
		int tTrigger = Math.max(1, tLimit - Math.max(1, (int) Math.round(tLimit * 0.01))); // ~1% offset
		int slLimit = (int) Math.round(trade.getStopLoss());
		int slTrigger = slLimit + Math.max(1, (int) Math.round(slLimit * 0.01));

		GTTModel model = new GTTModel();
		if (existingGttId != null && !existingGttId.isBlank()) {
			model.Id = existingGttId; // modify
		}
		model.Side = -1; // Sell exit
		model.Symbol = trade.getSymbol();
		model.productType = "MARGIN";

		model.addGTTLeg("leg1", new GTTLeg(tTrigger, tLimit, qty));
		model.addGTTLeg("leg2", new GTTLeg(slTrigger, slLimit, qty));

		List<GTTModel> orders = Collections.singletonList(model);
		OrderResponse resp = fyersApiService.PlaceGTTOrder(orders);

		if (resp.getId() != null) {
			System.out.println("✅ OCO Order Placed: " + resp.getMessage());
		} else {
			System.err.println("❌ OCO Placement Error: " + resp.getMessage());
		}
		

		if (resp != null && resp.getCode() == 1101) {
			// if new, save id; if modify, keep existing id
			if (trade.getFyOcoOrderId() == null || trade.getFyOcoOrderId().isBlank()) {
				trade.setFyOcoOrderId(resp.getId());
			}
			trade.setLastUpdated(LocalDateTime.now());
			tradeRepo.save(trade);
		} else {
			String msg = (resp != null ? resp.getMessage() : "null response");
			throw new RuntimeException("GTT place/modify failed: " + msg);
		}
	}

	// ---------- cancel OCO ----------
	@Transactional
	public boolean cancelOco(String symbol) {
		ManualOptionTrade trade = tradeRepo.findBySymbolAndOpenTrue(symbol);
		if (trade == null)
			return false;
		String gttId = trade.getFyOcoOrderId();
		if (gttId == null)
			return false;
		boolean ok = fyersApiService.cancelGTTOrder(gttId);
		if (ok) {
			trade.setFyOcoOrderId(null);
			tradeRepo.save(trade);
		}
		return ok;
	}

	// ---------- handle live tick (called from websocket) ----------
	public void handleLiveTick(String symbol, double ltp) {
		localHighMap.merge(symbol, ltp, Math::max);
		double localHigh = localHighMap.get(symbol);
		if (shouldCheckTsl(symbol, localHigh)) {
			updateTslIfNeeded(symbol, localHigh);
		}
	}

	private boolean shouldCheckTsl(String symbol, double newHigh) {
		Double last = localLastTSLMap.get(symbol);
		if (last == null)
			return true;
		return newHigh > last * 1.005; // 0.5% above last tsl update
	}

	// ---------- TSL logic (update SL progressively and modify OCO) ----------
	@Transactional
	public void updateTslIfNeeded(String symbol, double highPrice) {
		ManualOptionTrade trade = tradeRepo.findBySymbolAndOpenTrue(symbol);
		if (trade == null)
			return;

		double entry = trade.getBuyAvg();
		Double slObj = trade.getStopLoss();
		Double targetObj = trade.getTarget();
		if (slObj == null || targetObj == null)
			return;

		double sl = slObj;
		double target = targetObj;

		double p50 = entry + (target - entry) * 0.5;
		double p75 = entry + (target - entry) * 0.75;

		double newSl = sl;
		if (highPrice >= p50 && sl < entry) {
			newSl = entry;
		} else if (highPrice >= p75 && sl < entry + (target - entry) * 0.5) {
			newSl = entry + (target - entry) * 0.5;
		} else if (highPrice >= target * 0.95 && sl < target - (target - entry) * 0.2) {
			newSl = target - (target - entry) * 0.2;
		}

		if (Double.compare(newSl, sl) != 0) {
			trade.setStopLoss(newSl);
			trade.setLastTslUpdateTime(LocalDateTime.now());
			tradeRepo.save(trade);
			localLastTSLMap.put(symbol, newSl);
			// modify OCO with new SL
			placeOrUpdateSingleOco(trade, true);
		} else {
			localLastTSLMap.put(symbol, sl); // update last seen
		}
	}

	// ---------- process fyers positions (sync) ----------
	@Transactional
	public void processFyersPositions(List<NetPosition> positions) {
		if (positions == null)
			return;

		List<ManualOptionTrade> openTrades = tradeRepo.findAllByOpenTrue();
		Map<String, ManualOptionTrade> openMap = openTrades.stream()
				.collect(Collectors.toMap(ManualOptionTrade::getSymbol, Function.identity(), (a, b) -> a));

		for (NetPosition p : positions) {
			try {
				if (p.getSegment() != 11)
					continue;
				String symbol = p.getSymbol();
				int netQty = p.getNetQty();

				if (netQty == 0 && openMap.containsKey(symbol)) {
					ManualOptionTrade t = openMap.get(symbol);
					t.setOpen(false);
					t.setExitPrice(p.getSellAvg() > 0 ? p.getSellAvg() : p.getLtp());
					t.setExitTime(LocalDateTime.now());
					t.setRealizedPnl(p.getPl());
					tradeRepo.save(t);
				} else if (netQty > 0) {
					saveOrUpdateBuyTrade(symbol, p.getBuyAvg(), netQty, p.getFyToken());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Transactional
	public ManualOptionTrade saveOrUpdateBuyTrade(String symbol, double buyPrice, int qty, String fyOrderId) {
		ManualOptionTrade existing = tradeRepo.findBySymbolAndOpenTrue(symbol);
		if (existing != null) {
			if (existing.getNetQty() != qty) {
				existing.setNetQty(qty);
				existing.setBuyAvg(buyPrice);
				existing.setFyBuyOrderId(fyOrderId);
				existing.setLastUpdated(LocalDateTime.now());
				return tradeRepo.save(existing);
			} else {
				existing.setLastUpdated(LocalDateTime.now());
				return tradeRepo.save(existing);
			}
		}

		ManualOptionTrade t = new ManualOptionTrade();
		t.setSymbol(symbol);
		t.setBuyAvg(buyPrice);
		t.setNetQty(qty);
		t.setFyBuyOrderId(fyOrderId);
		t.setEntryTime(LocalDateTime.now());
		t.setOpen(true);
		return tradeRepo.save(t);
	}

	public List<ManualOptionTrade> getAllActiveTrades() {
		return tradeRepo.findAllByOpenTrueOrderByEntryTimeDesc();
	}

	public void placeOptionOCO(String symbol, double targetPrice, double stopLossPrice, int qty) {

		try {
			List<GTTModel> orders = new ArrayList<>();

			// SELL OCO Order for Exit
			GTTModel model = new GTTModel();
			model.Side = -1; // SELL
			model.Symbol = symbol; // e.g. "NFO:NIFTY25JAN22500CE"
			model.productType = "INTRADAY";

			int tLimit = (int) Math.round(targetPrice);
			int tTrigger = tLimit - 2; // small offset

			model.addGTTLeg("target", new GTTLeg(tTrigger, tLimit, qty));

			int slLimit = (int) Math.round(stopLossPrice);
			int slTrigger = slLimit + 2; // small offset

			model.addGTTLeg("stopLoss", new GTTLeg(slTrigger, slLimit, qty));

			orders.add(model);

			// Send order
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("⚠️ Failed to place OCO for " + symbol);
		}
	}

}
