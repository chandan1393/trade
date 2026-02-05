package com.fyers.fyerstrading.restcontroller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.entity.FyersAuthDetails;
import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.entity.TradeExecution;
import com.fyers.fyerstrading.entity.TradeSetup;
import com.fyers.fyerstrading.entity.TradeStatus;
import com.fyers.fyerstrading.entity.UserEntity;
import com.fyers.fyerstrading.model.FyersOrderResponse;
import com.fyers.fyerstrading.model.Holding;
import com.fyers.fyerstrading.model.HoldingsResponse;
import com.fyers.fyerstrading.model.NetPosition;
import com.fyers.fyerstrading.model.Order;
import com.fyers.fyerstrading.model.PositionResponse;
import com.fyers.fyerstrading.model.UserProfile;
import com.fyers.fyerstrading.repo.FyersAuthRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.repo.TradeExecutionRepository;
import com.fyers.fyerstrading.repo.TradeSetupRepository;
import com.fyers.fyerstrading.service.AuthService;
import com.fyers.fyerstrading.service.BackTestHourlyStartegy;
import com.fyers.fyerstrading.service.FyersApiService;
import com.fyers.fyerstrading.service.FyersAuthService;
import com.fyers.fyerstrading.service.VolumePriceBreakOutService;
import com.fyers.fyerstrading.service.common.ProfileService;
import com.fyers.fyerstrading.strategy.PositionalNiftyStrategyUpdated;
import com.fyers.fyerstrading.strategy.RSIReversalStrategy;

@RestController
public class ProfileController {

	@Autowired
	AuthService authService;

	@Autowired
	FyersAuthRepository fyersAuthRepository;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private FyersApiService fyersApiService;
	@Autowired
	private FyersAuthService fyersAuthService;

	@Autowired
	private VolumePriceBreakOutService volumePriceBreakOutOrderService;

	@Autowired
	BackTestHourlyStartegy backTestHourlyStartegy;

	@Autowired
	StockMasterRepository masterRepository;

	@GetMapping("/get-positions")
	public ResponseEntity<List<NetPosition>> getPositions() {
		PositionResponse positionResponse = fyersApiService.getPositions();
		List<NetPosition> netPositionList = positionResponse.getNetPositions();
		return ResponseEntity.ok(netPositionList);
	}

	@GetMapping("/update-positions")
	public ResponseEntity<List<String>> updatePositions() {
		List<String> list = volumePriceBreakOutOrderService.updatePositions();
		return ResponseEntity.ok(list);
	}

	@GetMapping("/get-holdings")
	public ResponseEntity<HoldingsResponse> getHoldings() {
		HoldingsResponse holdings = profileService.getHoldings();
		return ResponseEntity.ok(holdings);
	}

	@GetMapping("/get-orderById")
	public ResponseEntity<List<Order>> getOrders(@RequestParam String orderId) {
		List<Order> orders = profileService.getOrderById(orderId);
		return ResponseEntity.ok(orders);
	}

	@GetMapping("/get-orders")
	public ResponseEntity<List<Order>> getOrders() {
		List<Order> orderList = profileService.getAllOrders();
		return ResponseEntity.ok(orderList);
	}

	@GetMapping("/getOrderBook")
	public ResponseEntity<List<Order>> getOrderBook() {
		FyersOrderResponse fyersOrderResponse = fyersApiService.getOrders("all", "");
		List<Order> orders = fyersOrderResponse.getOrderBook();

		if (orders == null || orders.isEmpty()) {
			return ResponseEntity.ok(Collections.emptyList());
		}
		return ResponseEntity.ok(orders);
	}

	@GetMapping("/getGTTOrderBook")
	public ResponseEntity<List<Order>> getGTTOrderBook(
			@RequestParam(value = "side", defaultValue = "all") String side) {

		FyersOrderResponse fyersOrderResponse = fyersApiService.getGTTOrderBook();
		List<Order> orders = fyersOrderResponse.getOrderBook();

		if (orders == null || orders.isEmpty()) {
			return ResponseEntity.ok(Collections.emptyList());
		}

		List<Order> filteredOrders;
		switch (side.toLowerCase()) {
		case "buy":
			filteredOrders = orders.stream().filter(o -> o.getTranSide() == 1).collect(Collectors.toList());
			break;
		case "sell":
			filteredOrders = orders.stream().filter(o -> o.getTranSide() == -1).collect(Collectors.toList());
			break;
		case "all":
		default:
			filteredOrders = orders;
			break;
		}

		return ResponseEntity.ok(filteredOrders);
	}

	@GetMapping("/getGTTOrderBookSymbols")
	public ResponseEntity<List<String>> getGTTOrderId(@RequestParam(value = "side", defaultValue = "all") String side) {

		FyersOrderResponse fyersOrderResponse = fyersApiService.getGTTOrderBook();
		List<Order> orders = fyersOrderResponse.getOrderBook();

		if (orders == null || orders.isEmpty()) {
			return ResponseEntity.ok(Collections.emptyList());
		}

		List<String> filteredOrderIds;
		switch (side.toLowerCase()) {
		case "buy":
			filteredOrderIds = orders.stream().filter(o -> o.getTranSide() == 1).map(Order::getSymbol)
					.collect(Collectors.toList());
			break;
		case "sell":
			filteredOrderIds = orders.stream().filter(o -> o.getTranSide() == -1).map(Order::getSymbol)
					.collect(Collectors.toList());
			break;
		case "all":
		default:
			filteredOrderIds = orders.stream().map(Order::getSymbol).collect(Collectors.toList());
			break;
		}

		return ResponseEntity.ok(filteredOrderIds);
	}

	@GetMapping("/cancelGTTOrders")
	public ResponseEntity<List<Order>> cancelGTTOrders(@RequestParam(defaultValue = "all") String side) {

		// Fetch all GTT orders
		FyersOrderResponse fyersOrderResponse = fyersApiService.getGTTOrderBook();
		List<Order> orders = fyersOrderResponse.getOrderBook();

		if (orders == null || orders.isEmpty()) {
			return ResponseEntity.ok(Collections.emptyList());
		}

		// Normalize input
		side = side.trim().toLowerCase();
		final String type = side;
		// Filter based on user input
		List<Order> filteredOrders = orders.stream().filter(o -> {
			if ("buy".equals(type)) {
				return o.getTranSide() == 1; // Buy
			} else if ("sell".equals(type)) {
				return o.getTranSide() == -1; // Sell
			} else {
				return true; // "all" or anything else
			}
		}).collect(Collectors.toList());

		// Cancel selected GTT orders
		for (Order order : filteredOrders) {
			fyersApiService.cancelGTTOrder(order.getId());
		}

		return ResponseEntity.ok(filteredOrders);
	}

	@GetMapping("/fetchHoldingsNotInDB")
	public ResponseEntity<List<Holding>> fetchHoldingsNotInDB() {
		List<Holding> unmatchedHoldings = volumePriceBreakOutOrderService.fetchHoldingTradesNotInDB();
		return ResponseEntity.ok(unmatchedHoldings);
	}

	public UserProfile getProfile() {
		UserProfile userProfile = fyersApiService.getProfile();
		return userProfile;

	}

}
