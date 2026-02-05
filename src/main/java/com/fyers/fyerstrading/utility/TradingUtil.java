package com.fyers.fyerstrading.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fyers.fyerstrading.entity.StockDailyPrice;

public class TradingUtil {

    
  
	public static double calculateEMA(List<Double> prices, int period, Double prevEMA) {
	    if (prices.size() < period) return 0; // Not enough data

	    double multiplier = 2.0 / (period + 1);

	    // If no previous EMA exists, calculate the first EMA using SMA
	    if (prevEMA == null) {
	        double sma = prices.subList(0, period).stream().mapToDouble(Double::doubleValue).sum() / period;
	        return sma;
	    }

	    // Use the previous EMA for a smoother transition
	    double ema= ((prices.get(prices.size() - 1) - prevEMA) * multiplier) + prevEMA;
	    return roundToTwoDecimalPlaces(ema);
	}

	 // Helper method to round to 2 decimal places
	public static double roundToTwoDecimalPlaces(double value) {
	    BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
	    return bd.doubleValue(); // Ensures proper rounding
	}
    
    
	public static Map<String, Double> calculateRSI(List<Double> prices, int period, Double prevAvgGain, Double prevAvgLoss) {
	    if (prices.size() < period + 1) return Collections.emptyMap();

	    double gain = Math.max(prices.get(prices.size() - 1) - prices.get(prices.size() - 2), 0);
	    double loss = Math.abs(Math.min(prices.get(prices.size() - 1) - prices.get(prices.size() - 2), 0));

	    double avgGain, avgLoss;

	    if (prevAvgGain == null || prevAvgLoss == null) { // First RSI Calculation
	        double sumGain = 0, sumLoss = 0;
	        for (int i = 1; i <= period; i++) {
	            double diff = prices.get(i) - prices.get(i - 1);
	            sumGain += Math.max(diff, 0);
	            sumLoss += Math.abs(Math.min(diff, 0));
	        }
	        avgGain = sumGain / period;
	        avgLoss = sumLoss / period;
	    } else { // Use Wilder’s Smoothing for Subsequent RSI Values
	        avgGain = ((prevAvgGain * (period - 1)) + gain) / period;
	        avgLoss = ((prevAvgLoss * (period - 1)) + loss) / period;
	    }

	    // Ensure three decimal places in intermediate calculations
	 

	    double rs = avgLoss == 0 ? 100 : avgGain / avgLoss;
	    rs=roundToThreeDecimalPlaces(rs);
	    double rsi = 100 - (100 / (1 + rs));
	    rsi=roundToTwoDecimalPlaces(rsi);

	    // Ensure three decimal places before storing in map
	    Map<String, Double> result = new HashMap<>();
	    result.put("avgGain", roundToThreeDecimalPlaces(avgGain));
	    result.put("avgLoss", roundToThreeDecimalPlaces(avgLoss));
	    result.put("RSI", roundToThreeDecimalPlaces(rsi));
	    return result;
	}

	// Utility method for rounding to 3 decimal places
	private static double roundToThreeDecimalPlaces(double value) {
	    return Math.round(value * 1000.0) / 1000.0;
	}


	public static double calculateATRWilder(List<StockDailyPrice> stockDataList, int period, Double prevATR) {
	    if (stockDataList.size() < period) return 0;

	    // First ATR = Simple Average of True Ranges
	    if (prevATR == null) {
	        double trSum = 0;
	        for (int i = 1; i < stockDataList.size(); i++) {
	            StockDailyPrice current = stockDataList.get(i);
	            StockDailyPrice previous = stockDataList.get(i - 1);
	            trSum += calculateTrueRange(current, previous);
	        }
	        return roundToThreeDecimalPlaces(trSum / (stockDataList.size() - 1)); // Since it's TRs of N-1 days
	    }

	    // Wilder’s Smoothing for subsequent ATR
	    StockDailyPrice last = stockDataList.get(stockDataList.size() - 1);
	    StockDailyPrice previous = stockDataList.get(stockDataList.size() - 2);
	    double tr = calculateTrueRange(last, previous);
	    return roundToThreeDecimalPlaces(((prevATR * (period - 1)) + tr) / period);
	}

	public static double calculateTrueRange(StockDailyPrice current, StockDailyPrice previous) {
	    double highLow = current.getHighPrice() - current.getLowPrice();
	    double highPrevClose = Math.abs(current.getHighPrice() - previous.getClosePrice());
	    double lowPrevClose = Math.abs(current.getLowPrice() - previous.getClosePrice());
	    return Math.max(highLow, Math.max(highPrevClose, lowPrevClose));
	}
	
	public static double calculateInitialATR(List<StockDailyPrice> stockDataList) {
	    if (stockDataList.size() < 14) return 0;

	    double trSum = 0;
	    for (int i = 1; i < stockDataList.size(); i++) {
	        StockDailyPrice current = stockDataList.get(i);
	        StockDailyPrice previous = stockDataList.get(i - 1);
	        trSum += calculateTrueRange(current, previous);
	    }

	    return roundToThreeDecimalPlaces(trSum / (stockDataList.size() - 1));
	}

	
	
	
	public static double calculateVWAP(List<StockDailyPrice> data, int period) {
	    int size = data.size();
	    if (size < period) return 0.0;

	    double cumulativeTPV = 0.0; // Typical Price * Volume
	    double cumulativeVolume = 0.0;

	    for (int i = size - period; i < size; i++) {
	        StockDailyPrice candle = data.get(i);
	        double typicalPrice = (candle.getHighPrice() + candle.getLowPrice() + candle.getClosePrice()) / 3;
	        cumulativeTPV += typicalPrice * candle.getVolume();
	        cumulativeVolume += candle.getVolume();
	    }

	    return cumulativeVolume == 0 ? 0.0 : cumulativeTPV / cumulativeVolume;
	}
	
	
	
	public static int calculateSetupRank(List<StockDailyPrice> historicalData, StockDailyPrice current) {
		int score = 0;
		int lookback = 20;

		// Volume Surge Only
		double avgVolume = historicalData.stream().skip(Math.max(0, historicalData.size() - lookback - 1))
				.limit(lookback).mapToDouble(StockDailyPrice::getVolume).average().orElse(0);

		if (avgVolume > 0) {
			double volumeRatio = current.getVolume() / avgVolume;

			if (volumeRatio > 6)
				score += 50;
			else if (volumeRatio > 5)
				score += 40;
			else if (volumeRatio > 4)
				score += 30;
			else if (volumeRatio > 3)
				score += 20;
			else
				score += 10;

		}

		return score;
	}

}
