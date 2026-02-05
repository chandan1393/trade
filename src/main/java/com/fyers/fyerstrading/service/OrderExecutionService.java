package com.fyers.fyerstrading.service;

import com.fyers.fyerstrading.enu.Side;
import com.fyers.fyerstrading.model.OrderResponse;

public interface OrderExecutionService {

	OrderResponse placeEntryOrder(String symbol, Side side, int quantity, double price);

	OrderResponse placeExitOrder(String symbol, Side side, int quantity);
	

	double getLtp(String symbol);
}
