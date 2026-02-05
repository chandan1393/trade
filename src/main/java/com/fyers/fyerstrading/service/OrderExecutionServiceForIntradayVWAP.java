package com.fyers.fyerstrading.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.enu.Side;
import com.fyers.fyerstrading.model.OrderResponse;
import com.fyers.fyerstrading.model.StockQuote;
import com.tts.in.model.PlaceOrderModel;
import com.tts.in.utilities.OrderType;
import com.tts.in.utilities.OrderValidity;
import com.tts.in.utilities.ProductType;
import com.tts.in.utilities.TransactionType;

@Service("intradayVWAPOrderService")
public class OrderExecutionServiceForIntradayVWAP implements OrderExecutionService {

	@Autowired
	private FyersApiService fyersApiService;

	// ================= ENTRY ORDER =================
	@Override
	public OrderResponse placeEntryOrder(String symbol, Side side, int quantity, double price) {

		PlaceOrderModel model = new PlaceOrderModel();
		model.Symbol = symbol;
		model.Qty = quantity;
		model.OrderType = OrderType.MarketOrder.getDescription(); // MARKET
		model.Side = (side == Side.BUY) ? TransactionType.Buy.getValue() : TransactionType.Sell.getValue();

		model.ProductType = ProductType.INTRADAY;
		model.LimitPrice = 0;
		model.StopPrice = 0;
		model.OrderValidity = OrderValidity.DAY;
		model.DisclosedQty = 0;
		model.OffLineOrder = false;
		model.StopLoss = 0;
		model.TakeProfit = 0;
		model.OrderTag = "VWAP_INTRADAY";

		return fyersApiService.placeOrder(model);

	}

	// ================= EXIT ORDER =================
	@Override
	public OrderResponse placeExitOrder(String symbol, Side side, int quantity) {

		// Exit = reverse market order
		PlaceOrderModel model = new PlaceOrderModel();
		model.Symbol = symbol;
		model.Qty = quantity;
		model.OrderType = OrderType.MarketOrder.getDescription();
		model.Side = (side == Side.BUY) ? TransactionType.Sell.getValue() : TransactionType.Buy.getValue();

		model.ProductType = ProductType.INTRADAY;
		model.LimitPrice = 0;
		model.StopPrice = 0;
		model.OrderValidity = OrderValidity.DAY;
		model.DisclosedQty = 0;
		model.OffLineOrder = false;
		model.OrderTag = "VWAP_EXIT";

		return  fyersApiService.placeOrder(model);

		
	}

	// ================= LTP =================
	@Override
	public double getLtp(String symbol) {
		try {
			StockQuote quote = fyersApiService.getStockQuotes(symbol);
			return quote.getLp();
		} catch (Exception e) {
			System.err.println("Error fetching underlying LTP: " + e.getMessage());
		}
		return 0.0;
	}
}
