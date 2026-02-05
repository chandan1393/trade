package com.fyers.fyerstrading.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.enu.Side;
import com.fyers.fyerstrading.model.OrderResponse;
import com.tts.in.model.PlaceOrderModel;
import com.tts.in.utilities.OrderType;
import com.tts.in.utilities.OrderValidity;
import com.tts.in.utilities.ProductType;
import com.tts.in.utilities.TransactionType;

@Service
public class OrderExecutionServiceForOption {

	@Autowired
	private FyersApiService fyersApiService;

	
	public OrderResponse placeEntryOrder(String symbol, Side side, int quantity, double price) {
		 PlaceOrderModel model = new PlaceOrderModel();
		return fyersApiService.placeOrder(model);
	}

	
	public OrderResponse placeExitOrder(String symbol, Side side, int quantity) {
	     PlaceOrderModel model = new PlaceOrderModel();
	        model.Symbol = symbol;
	        model.Qty = quantity;
	        model.OrderType = OrderType.MarketOrder.getDescription();
	        model.Side = TransactionType.Sell.getValue();
	        model.ProductType = ProductType.MARGIN;
	        model.LimitPrice = 0;
	        model.StopPrice = 0;
	        model.OrderValidity = OrderValidity.DAY;
	        model.DisclosedQty = 0;
	        model.OffLineOrder = false;
	        model.StopLoss = 0;
	        model.TakeProfit = 0;
	        model.OrderTag = "ManualOrderTag1";
		return fyersApiService.placeOrder(model);
	}

	
	public OrderResponse placeExitOrderLimitResponse(String symbol, Side side, int quantity) {
	     PlaceOrderModel model = new PlaceOrderModel();
	        model.Symbol = symbol;
	        model.Qty = quantity;
	        model.OrderType = OrderType.LimitOrder.getDescription();
	        model.Side = TransactionType.Sell.getValue();
	        model.ProductType = ProductType.MARGIN;
	        model.LimitPrice = 50;
	        model.StopPrice = 0;
	        model.OrderValidity = OrderValidity.DAY;
	        model.DisclosedQty = 0;
	        model.OffLineOrder = false;
	        model.StopLoss = 0;
	        model.TakeProfit = 0;
	        model.OrderTag = "ManualOrderTag1";
		return fyersApiService.placeOrder(model);
	}

	
}
