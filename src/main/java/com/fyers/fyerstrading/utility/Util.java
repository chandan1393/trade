package com.fyers.fyerstrading.utility;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fyers.fyerstrading.model.StockCandle;
import com.fyers.fyerstrading.model.StockQuote;

public class Util {
    
	public static String computeAppIdHash(String appId, String appSecret) {
		String input = appId + ":" + appSecret;
		return DigestUtils.sha256Hex(input);
	}

	
	public static String convertTimeframeToResolution(String timeframe) {
		switch (timeframe) {
		case "1MIN":
			return "1";
		case "5MIN":
			return "5";
		case "15MIN":
			return "15";
		case "30MIN":
			return "30";
		case "1HOUR":
			return "60";
		case "1DAY":
			return "D";
		default:
			throw new IllegalArgumentException("Invalid timeframe: " + timeframe);
		}
	}
	

    public static List<StockCandle> parseStockData(JSONObject stockHistory) {
        List<StockCandle> candles = new ArrayList<>();

        if (stockHistory!=null && stockHistory.has("candles")) {
            JSONArray candlesArray = stockHistory.getJSONArray("candles");

            for (int i = 0; i < candlesArray.length(); i++) {
                JSONArray candleData = candlesArray.getJSONArray(i);

                // Convert timestamp to LocalDateTime
                Instant timestamp = Instant.ofEpochSecond(candleData.getLong(0));
                LocalDate dateTime = LocalDate.ofInstant(timestamp, ZoneId.of("Asia/Kolkata"));

                    StockCandle candle = new StockCandle(timestamp, // Timestamp
                            candleData.getDouble(1), // Open
                            candleData.getDouble(2), // High
                            candleData.getDouble(3), // Low
                            candleData.getDouble(4), // Close
                            candleData.getLong(5), // Volume
                            dateTime
                    );
                    candles.add(candle);
                }
            
        }
        return candles;
    }
    
    public static StockQuote convertJsonToStockQuote(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            
            // Directly access "d" without "Stock Quotes"
            JsonNode dataArray = rootNode.path("d");

            if (dataArray.isArray() && dataArray.size() > 0) {
                JsonNode vNode = dataArray.get(0).path("v"); // Extract "v" inside first object
                return objectMapper.treeToValue(vNode, StockQuote.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	// Get current date
	public static String getCurrentDate() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	

}
