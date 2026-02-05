package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.Nifty5MinCandle;
import com.fyers.fyerstrading.entity.Nifty5MinCandle;

@Service
public class NiftyORBHelper {

	public static Nifty5MinCandle getRangeCandle(List<Nifty5MinCandle> candles, int startIndex, int endIndex) {
		double high = Double.MIN_VALUE;
		double low = Double.MAX_VALUE;
		for (int i = startIndex; i <= endIndex && i < candles.size(); i++) {
			Nifty5MinCandle c = candles.get(i);
			high = Math.max(high, c.getHigh());
			low = Math.min(low, c.getLow());
		}
		return new Nifty5MinCandle(candles.get(endIndex).getOpen(), high, low, candles.get(endIndex).getClose(),
				candles.get(endIndex).getTimestamp());
	}

	public static boolean isInsideBar(Nifty5MinCandle current, Nifty5MinCandle reference) {
		return current.getHigh() <= reference.getHigh() && current.getLow() >= reference.getLow();
	}

	public static boolean isRiskRewardFavorable(double entry, double stopLoss, double target) {
		double risk = Math.abs(entry - stopLoss);
		double reward = Math.abs(target - entry);
		return reward >= 2 * risk;
	}

	public static boolean isNearResistanceOrSupport(Nifty5MinCandle candle, double pdh, double pdl) {
	    double buffer = 0.3; // Example: 0.3% buffer
	    double price = candle.getClose();
	    return isNearLevel(price, pdh, buffer) || isNearLevel(price, pdl, buffer);
	}

	private static boolean isNearLevel(double price, double level, double bufferPercent) {
		double buffer = level * bufferPercent / 100;
		return price >= level - buffer && price <= level + buffer;
	}

	public static boolean isChoppyPreMarket(List<Nifty5MinCandle> candles) {
		// Logic: pre-market range is wide and overlapping (ex: first 3 candles range
		// overlaps heavily)
		if (candles.size() < 3)
			return false;
		Nifty5MinCandle c1 = candles.get(0);
		Nifty5MinCandle c2 = candles.get(1);
		Nifty5MinCandle c3 = candles.get(2);
		return overlaps(c1, c2) && overlaps(c2, c3);
	}

	private static boolean overlaps(Nifty5MinCandle a, Nifty5MinCandle b) {
		return a.getHigh() >= b.getLow() && b.getHigh() >= a.getLow();
	}

	public static boolean isGapDay(List<Nifty5MinCandle> candles, double prevClose) {
		if (candles.isEmpty())
			return false;
		double open = candles.get(0).getOpen();
		double gapPercent = Math.abs(open - prevClose) / prevClose * 100;
		return gapPercent > 1.5; // example gap threshold
	}

	public Map<LocalDate, Nifty5MinCandle> convertToDailyCandles(List<Nifty5MinCandle> fiveMinCandles) {
		return fiveMinCandles.stream().sorted(Comparator.comparing(Nifty5MinCandle::getTimestamp))
				.collect(Collectors.groupingBy(candle -> candle.getTimestamp().toLocalDate(), LinkedHashMap::new,
						Collectors.collectingAndThen(Collectors.toList(), this::aggregateToDaily)));
	}

	private Nifty5MinCandle aggregateToDaily(List<Nifty5MinCandle> dailyCandles) {
		double open = dailyCandles.get(0).getOpen();
		double close = dailyCandles.get(dailyCandles.size() - 1).getClose();
		double high = dailyCandles.stream().mapToDouble(Nifty5MinCandle::getHigh).max().orElse(open);
		double low = dailyCandles.stream().mapToDouble(Nifty5MinCandle::getLow).min().orElse(open);

		Nifty5MinCandle daily = new Nifty5MinCandle();
		daily.setOpen(open);
		daily.setHigh(high);
		daily.setLow(low);
		daily.setClose(close);
		return daily;
	}

	public Map<LocalDate, Double> calculateATR14(Map<LocalDate, Nifty5MinCandle> dailyCandleMap) {
		List<Map.Entry<LocalDate, Nifty5MinCandle>> entries = new ArrayList<>(dailyCandleMap.entrySet());
		entries.sort(Map.Entry.comparingByKey()); // Ensure chronological order

		Map<LocalDate, Double> atrMap = new LinkedHashMap<>();
		List<Double> trueRanges = new ArrayList<>();

		for (int i = 1; i < entries.size(); i++) {
			Nifty5MinCandle today = entries.get(i).getValue();
			Nifty5MinCandle prev = entries.get(i - 1).getValue();

			double highLow = today.getHigh() - today.getLow();
			double highClose = Math.abs(today.getHigh() - prev.getClose());
			double lowClose = Math.abs(today.getLow() - prev.getClose());

			double trueRange = Math.max(highLow, Math.max(highClose, lowClose));
			trueRanges.add(trueRange);

			// Once we have 14 TRs, start calculating ATR
			if (i == 14) {
				double firstATR = trueRanges.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
				atrMap.put(entries.get(i).getKey(), round(firstATR));
			} else if (i > 14) {
				double prevATR = atrMap.get(entries.get(i - 1).getKey());
				double currentATR = ((prevATR * 13) + trueRange) / 14;
				atrMap.put(entries.get(i).getKey(), round(currentATR));
			}
		}

		return atrMap;
	}

	private static double round(double value) {
		return Math.round(value * 100.0) / 100.0;
	}

	public static Map<LocalDate, Double> calculateEMA(Map<LocalDate, Nifty5MinCandle> dailyMap, int period) {
		List<Map.Entry<LocalDate, Nifty5MinCandle>> entries = new ArrayList<>(dailyMap.entrySet());
		entries.sort(Map.Entry.comparingByKey()); // Ensure chronological order

		Map<LocalDate, Double> emaMap = new LinkedHashMap<>();
		List<Double> closes = new ArrayList<>();

		double multiplier = 2.0 / (period + 1);
		Double prevEMA = null;

		for (int i = 0; i < entries.size(); i++) {
			LocalDate date = entries.get(i).getKey();
			double close = entries.get(i).getValue().getClose();
			closes.add(close);

			if (i == period - 1) {
				// First EMA = SMA
				double sma = closes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
				prevEMA = sma;
				emaMap.put(date, round(sma));
			} else if (i >= period) {
				// EMA = (Close - PrevEMA) * multiplier + PrevEMA
				double ema = ((close - prevEMA) * multiplier) + prevEMA;
				ema = round(ema);
				emaMap.put(date, ema);
				prevEMA = ema;
			}
		}

		return emaMap;
	}

	public static Double getPreviousDayHigh(Map<LocalDate, Nifty5MinCandle> dailyCandles, LocalDate date) {
	    // Get the previous day's data directly from the map
	    LocalDate previousDay = date.minusDays(1);
	    Nifty5MinCandle previousDayCandle = dailyCandles.get(previousDay);
	    return previousDayCandle != null ? previousDayCandle.getHigh() : null;  // Return high if available
	}

	public static Double getPreviousDayLow(Map<LocalDate, Nifty5MinCandle> dailyCandles, LocalDate date) {
	    // Get the previous day's data directly from the map
	    LocalDate previousDay = date.minusDays(1);
	    Nifty5MinCandle previousDayCandle = dailyCandles.get(previousDay);
	    return previousDayCandle != null ? previousDayCandle.getLow() : null;  // Return low if available
	}

	public static Double getPreviousClose(Map<LocalDate, Nifty5MinCandle> dailyCandles, LocalDate date) {
	    // Get the previous day's data directly from the map
	    LocalDate previousDay = date.minusDays(1);
	    Nifty5MinCandle previousDayCandle = dailyCandles.get(previousDay);
	    return previousDayCandle != null ? previousDayCandle.getClose() : null;  // Return close if available
	}

	public static boolean isTimeBetween(LocalTime time, String start, String end) {
	    LocalTime startTime = LocalTime.parse(start);
	    LocalTime endTime = LocalTime.parse(end);
	    return !time.isBefore(startTime) && !time.isAfter(endTime);
	}
	
	
	public static LocalDate getPreviousValidDate(LocalDate currentDate, Set<LocalDate> availableDates) {
	    List<LocalDate> sortedDates = new ArrayList<>(availableDates);
	    Collections.sort(sortedDates);
	    int idx = sortedDates.indexOf(currentDate);
	    return (idx > 0) ? sortedDates.get(idx - 1) : null;
	}
	
	

}
