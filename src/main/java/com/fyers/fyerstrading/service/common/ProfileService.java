package com.fyers.fyerstrading.service.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.model.FyersOrderResponse;
import com.fyers.fyerstrading.model.HoldingsResponse;
import com.fyers.fyerstrading.model.NetPosition;
import com.fyers.fyerstrading.model.Order;
import com.fyers.fyerstrading.model.PositionResponse;
import com.fyers.fyerstrading.service.FyersApiService;

@Service
public class ProfileService {

	@Autowired
	private FyersApiService fyersApiService;

	public List<Order> getOrderById(String orderId) {
		try {
			// Step 1: Fetch order from Fyers using ID
			FyersOrderResponse fyersOrderResponse = fyersApiService.getOrders("id", orderId);
			return fyersOrderResponse.getOrderBook();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Order> getAllOrders() {
		try {
			// Step 1: Fetch order from Fyers using ID
			FyersOrderResponse fyersOrderResponse = fyersApiService.getOrders("all", "");
			return fyersOrderResponse.getOrderBook();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Order> getAllGTTOrders() {
		try {
			// Step 1: Fetch order from Fyers using ID
			FyersOrderResponse fyersOrderResponse = fyersApiService.getGTTOrderBook();
			return fyersOrderResponse.getOrderBook();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<NetPosition> getPositions() {
		try {
			PositionResponse positionResponse = fyersApiService.getPositions();

			return positionResponse.getNetPositions();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public HoldingsResponse getHoldings() {
		try {

			HoldingsResponse holdingResponse = fyersApiService.getHoldings();
			return holdingResponse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
