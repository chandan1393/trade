package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fyers.fyerstrading.entity.IndexOIData;
import com.fyers.fyerstrading.entity.StockOIData;
import com.fyers.fyerstrading.model.FyersOrderResponse;
import com.fyers.fyerstrading.model.FyersSessionContext;
import com.fyers.fyerstrading.model.GTTModifiedOrderResponse;
import com.fyers.fyerstrading.model.HoldingsResponse;
import com.fyers.fyerstrading.model.OrderResponse;
import com.fyers.fyerstrading.model.PositionResponse;
import com.fyers.fyerstrading.model.StockQuote;
import com.fyers.fyerstrading.model.UserProfile;
import com.fyers.fyerstrading.utility.ExpiryUtil;
import com.tts.in.model.FyersClass;
import com.tts.in.model.GTTModel;
import com.tts.in.model.PlaceOrderModel;
import com.tts.in.model.StockHistoryModel;
import com.tts.in.utilities.Tuple;

@Service
public class FyersApiService {

	@Autowired
	private FyersClass fyersClass;

	public void setFyersToken(FyersSessionContext fyersSession) {
		fyersClass.accessToken = fyersSession.getAccessToken();
		fyersClass.clientId = fyersSession.getClientId();
	}

	public JSONObject getStockHistory(StockHistoryModel stockHistoryModel) {

		Tuple<JSONObject, JSONObject> stockTuple = fyersClass.GetStockHistory(stockHistoryModel);
		if (stockTuple.Item2() == null) {
			return stockTuple.Item1();
		}
		return null;

	}

	public StockQuote getStockQuotes(String symbols) {
		try {
			Tuple<JSONObject, JSONObject> stockTuple = fyersClass.GetStockQuotes(symbols);

			if (stockTuple.Item2() == null) {
				JSONObject jsonResponse = stockTuple.Item1();

				if (jsonResponse != null && jsonResponse.has("d")) {
					JSONArray dataArray = jsonResponse.getJSONArray("d");
					if (dataArray.length() > 0) {
						JSONObject v = dataArray.getJSONObject(0).getJSONObject("v");

						// Map JSON "v" object to StockQuote
						ObjectMapper mapper = new ObjectMapper();
						return mapper.readValue(v.toString(), StockQuote.class);
					}
				}
			} else {
				System.out.println("Error: " + stockTuple.Item2());
			}
		} catch (Exception e) {
			System.err.println("Error parsing stock quotes: " + e.getMessage());
		}
		return null;
	}

	public JSONObject getMarketDepth(String symbols) {
		Tuple<JSONObject, JSONObject> ResponseTuple = fyersClass.GetMarketDepth(symbols, 0);

		if (ResponseTuple.Item2() == null) {
			return ResponseTuple.Item1();
		} else {
			System.out.println("Market Depth Error:" + ResponseTuple.Item2());
		}
		return null;
	}

	public JSONObject getMarketStatus() {
		Tuple<JSONObject, JSONObject> stockTuple = fyersClass.GetMarketStatus();

		if (stockTuple.Item2() == null) {
			return stockTuple.Item1();
		} else {
			System.out.println("Market Status Error:" + stockTuple.Item2());
		}
		return null;
	}

	public UserProfile getProfile() {
		try {
			Tuple<JSONObject, JSONObject> profileResponseTuple = fyersClass.GetProfile();

			if (profileResponseTuple.Item2() == null) {
				JSONObject response = profileResponseTuple.Item1();
				ObjectMapper objectMapper = new ObjectMapper();
				return objectMapper.readValue(response.toString(), UserProfile.class);
			} else {
				System.out.println("Profile Error: " + profileResponseTuple.Item2());
			}
		} catch (Exception e) {
			System.err.println("Error parsing profile: " + e.getMessage());
		}
		return null;
	}

	public OrderResponse placeOrder(PlaceOrderModel model) {

		Tuple<JSONObject, JSONObject> responseTuple = fyersClass.PlaceOrder(model);

		JSONObject successJson = responseTuple.Item1();
		JSONObject errorJson = responseTuple.Item2();

		JSONObject responseJson;
		String message;

		// ❌ ERROR CASE
		if (errorJson != null) {

			int code = errorJson.optInt("code", -999);
			String status = errorJson.optString("s", "error");
			String id = errorJson.optString("id", "");

			message = "ERROR: " + errorJson.toString();

			return new OrderResponse(code, status, id, message);
		}

		// ✅ SUCCESS CASE
		responseJson = successJson;

		int code = responseJson.optInt("code", -999);
		String status = responseJson.optString("s", "ok");
		String id = responseJson.optString("id", "");
		message = responseJson.optString("message", "Order placed");

		return new OrderResponse(code, status, id, message);
	}

	public OrderResponse PlaceGTTOrder(List<GTTModel> placeOrdersList) {
		Tuple<JSONObject, JSONObject> responseTuple = fyersClass.PlaceGTTOrder(placeOrdersList);

		JSONObject successJson = responseTuple.Item1();
		JSONObject errorJson = responseTuple.Item2();

		JSONObject responseJson;
		String message;

		if (errorJson != null) {
			responseJson = errorJson;

			message = "ERROR: " + errorJson.toString();

			int code = errorJson.optInt("code", -999);
			String s = errorJson.optString("s", "");
			String id = errorJson.optString("id", "");

			return new OrderResponse(code, s, id, message);
		}

		// Case 2: ✅ Success → use Item1()
		responseJson = successJson;

		int code = responseJson.optInt("code", -999);
		String s = responseJson.optString("s", "");
		String id = responseJson.optString("id", "");
		message = responseJson.optString("message", "");

		return new OrderResponse(code, s, id, message);
	}

	public GTTModifiedOrderResponse ModifyGTTOrder(List<GTTModel> placeOrdersList) {
		Tuple<JSONObject, JSONObject> responseTuple = fyersClass.ModifyGTTOrder(placeOrdersList);
		JSONObject responseJson = responseTuple.Item2() == null ? responseTuple.Item1() : responseTuple.Item2();

		// Safely extract fields from JSON
		int code = responseJson.optInt("code", -999); // default -999 if not present
		String responseBody = responseJson.optString("response_body", responseJson.toString());

		return new GTTModifiedOrderResponse(code, responseBody);
	}

	public void ExitPosition(List<String> positionIDs) {
		Tuple<JSONObject, JSONObject> jObject = fyersClass.ExitPositions(positionIDs);
		if (jObject.Item2() == null) {
			System.out.println("Position Message: " + jObject.Item1());
		} else {
			System.out.println("Position Error: " + jObject.Item2());
		}
	}

	public boolean cancelGTTOrder(String orderId) {

		Tuple<JSONObject, JSONObject> ResponseTuple = fyersClass.CancelGTTOrder(orderId);
		if (ResponseTuple.Item2() == null) {
			System.out.println("Orders ID: " + ResponseTuple.Item1());
			return true;
		} else {
			System.out.println("Place order Message : " + ResponseTuple.Item2());
		}
		return false;
	}

	public boolean cancelMultipleOrder(List<String> orderIdList) {

		Tuple<JSONObject, JSONObject> ResponseTuple = fyersClass.CancelMultipleOrders(orderIdList);
		if (ResponseTuple.Item2() == null) {
			System.out.println("Orders ID: " + ResponseTuple.Item1());
			return true;
		} else {
			System.out.println("Place order Message : " + ResponseTuple.Item2());
		}
		return false;
	}

	public FyersOrderResponse getOrders(String type, String orderId) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			Tuple<JSONObject, JSONObject> orderList;

			switch (type.toLowerCase()) {
			case "all":
				orderList = fyersClass.GetAllOrders();
				break;

			case "id":
				orderList = fyersClass.GetOrderById(orderId);
				break;

			case "tag":
				String orderTag = "2:Untagged";
				orderList = fyersClass.GetOrderByTag(orderTag);
				break;

			default:
				System.err.println("Invalid type: must be 'all', 'id', or 'tag'");
				return null;
			}

			if (orderList.Item2() == null && orderList.Item1() != null) {
				return mapper.readValue(orderList.Item1().toString(), FyersOrderResponse.class);
			} else {
				System.err.println("Error response: " + orderList.Item2());
				return null;
			}

		} catch (Exception e) {
			System.err.println("Exception while fetching orders: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public FyersOrderResponse getGTTOrderBook() {
		ObjectMapper mapper = new ObjectMapper();

		try {
			Tuple<JSONObject, JSONObject> orderList = fyersClass.GetGTTOrderBook();

			if (orderList.Item2() == null && orderList.Item1() != null) {
				return mapper.readValue(orderList.Item1().toString(), FyersOrderResponse.class);
			} else {
				System.err.println("Error response: " + orderList.Item2());
				return null;
			}

		} catch (Exception e) {
			System.err.println("Exception while fetching orders: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public List<IndexOIData> getIndexOptionChain(String symbol, int strikeCount, String timestamp) {

		Tuple<JSONObject, JSONObject> tuple = fyersClass.GetOptionChain(symbol, strikeCount, "");

		JSONObject root = tuple.Item1();
		if (root == null)
			return List.of();

		JSONObject dataObj = root.optJSONObject("data");
		if (dataObj == null)
			return List.of();

		JSONArray chainArr = dataObj.optJSONArray("optionsChain");
		if (chainArr == null)
			return List.of();

		double indiaVixLtp = dataObj.has("indiavixData") ? dataObj.getJSONObject("indiavixData").optDouble("ltp", 0.0)
				: 0.0;

		Map<Double, IndexOIData> map = new HashMap<>();
		LocalDateTime now = LocalDateTime.now();

		for (int i = 0; i < chainArr.length(); i++) {

			JSONObject item = chainArr.getJSONObject(i);

			double strike = item.optDouble("strike_price", -1);
			if (strike <= 0)
				continue;

			String optionSymbol = item.optString("symbol");
			LocalDate expiry = ExpiryUtil.extractExpiryFromSymbol(optionSymbol);

			String type = item.optString("option_type");
			double ltp = item.optDouble("ltp", 0);
			long oi = item.optLong("oi", 0);
			long oich = item.optLong("oich", 0);
			long volume = item.optLong("volume", 0);

			IndexOIData row = map.computeIfAbsent(strike, s -> {
				IndexOIData d = new IndexOIData();
				d.setSymbol(symbol);
				d.setOptionSymbol(optionSymbol);
				d.setStrikePrice(s);
				d.setExpiry(expiry); // ✅ FIXED
				d.setTimestamp(now);
				d.setIntervalMin(1);
				d.setIndiaVixLtp(indiaVixLtp);
				return d;
			});

			if ("CE".equalsIgnoreCase(type)) {
				row.setCallLTP(ltp);
				row.setCallOI(oi);
				row.setCallChgOI(oich);
				row.setVolumeCall(volume);
			} else if ("PE".equalsIgnoreCase(type)) {
				row.setPutLTP(ltp);
				row.setPutOI(oi);
				row.setPutChgOI(oich);
				row.setVolumePut(volume);
			}
		}

		return new ArrayList<>(map.values());
	}

	public List<StockOIData> getStockOptionChain(String symbol, int strikeCount, String timestamp) {

		Tuple<JSONObject, JSONObject> tuple = fyersClass.GetOptionChain(symbol, strikeCount, "");

		JSONObject root = tuple.Item1();
		if (root == null)
			return List.of();

		JSONObject dataObj = root.optJSONObject("data");
		if (dataObj == null)
			return List.of();

		JSONArray chainArr = dataObj.optJSONArray("optionsChain");
		if (chainArr == null)
			return List.of();

		Map<Double, StockOIData> map = new HashMap<>();
		LocalDateTime now = LocalDateTime.now();

		for (int i = 0; i < chainArr.length(); i++) {

			JSONObject item = chainArr.getJSONObject(i);

			double strike = item.optDouble("strike_price", -1);
			if (strike <= 0)
				continue;

			String optionSymbol = item.optString("symbol");
			LocalDate expiry = ExpiryUtil.extractStockExpiry(optionSymbol);

			String type = item.optString("option_type");
			double ltp = item.optDouble("ltp", 0);
			long oi = item.optLong("oi", 0);
			long oich = item.optLong("oich", 0);
			long volume = item.optLong("volume", 0);

			StockOIData row = map.computeIfAbsent(strike, s -> {
				StockOIData d = new StockOIData();
				d.setSymbol(symbol);
				d.setOptionSymbol(optionSymbol);
				d.setExpiry(expiry);
				d.setStrikePrice(s);
				d.setTimestamp(now);
				d.setIntervalMin(5);
				return d;
			});

			if ("CE".equalsIgnoreCase(type)) {
				row.setCallLTP(ltp);
				row.setCallOI(oi);
				row.setCallChgOI(oich);
				row.setVolumeCall(volume);
			} else if ("PE".equalsIgnoreCase(type)) {
				row.setPutLTP(ltp);
				row.setPutOI(oi);
				row.setPutChgOI(oich);
				row.setVolumePut(volume);
			}
		}

		return new ArrayList<>(map.values());
	}

	public HoldingsResponse getHoldings() {
		try {
			Tuple<JSONObject, JSONObject> holdingTuple = fyersClass.GetHoldings();

			if (holdingTuple.Item2() == null) {
				ObjectMapper mapper = new ObjectMapper();
				HoldingsResponse response = mapper.readValue(holdingTuple.Item1().toString(), HoldingsResponse.class);
				return response;
			} else {
				System.err.println("Holdings Error: " + holdingTuple.Item2());
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public PositionResponse getPositions() {
		Tuple<JSONObject, JSONObject> positionTuple = fyersClass.GetPositions();

		if (positionTuple.Item2() != null) {
			System.err.println("Positions Error: " + positionTuple.Item2());
			return null;
		}

		try {
			JSONObject json = positionTuple.Item1();
			if (json == null) {
				System.err.println("Positions response missing.");
				return null;
			}

			ObjectMapper mapper = new ObjectMapper();
			System.out.println(json.toString());
			return mapper.readValue(json.toString(), PositionResponse.class);

		} catch (Exception e) {
			System.err.println("Error parsing positions: " + e.getMessage());
			return null;
		}
	}

	

}
