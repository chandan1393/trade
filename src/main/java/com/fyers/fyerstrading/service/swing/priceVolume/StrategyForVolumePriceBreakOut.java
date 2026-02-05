package com.fyers.fyerstrading.service.swing.priceVolume;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fyers.fyerstrading.entity.NiftyDailyCandle;
import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.StockTechnicalIndicator;

@Service
public class StrategyForVolumePriceBreakOut {

	public boolean isValidSetup(List<StockDailyPrice> data, List<NiftyDailyCandle> niftyData) {
		if (data.size() < 63)
			return false;

		StockDailyPrice latest = data.get(data.size() - 1);

		double close = latest.getClosePrice();
		double open = latest.getOpenPrice();
		double high = latest.getHighPrice();
		double low = latest.getLowPrice();
		double volume = latest.getVolume();
		double range = high - low;
		double body = Math.abs(close - open);
		double rsi = latest.getTechnicalIndicator().getRsi();
		double pctChange = latest.getTechnicalIndicator().getPercentChange();

		if (!isLiquidStock(data))
			return false;

		double avgVolume = data.subList(data.size() - 14, data.size()).stream().mapToDouble(StockDailyPrice::getVolume)
				.average().orElse(0);
		double avgChange = data.subList(data.size() - 14, data.size()).stream()
				.mapToDouble(p -> p.getTechnicalIndicator().getPercentChange()).average().orElse(0);

		double prevHigh = data.get(data.size() - 2).getHighPrice();

		// --- Mandatory smart volume condition ---
		boolean isSmartVolume = close > open && volume > 5 * avgVolume;
		if (!isSmartVolume)
			return false;

		// --- Candle Strength Filters ---
		if (body < 0.5 * range)
			return false; // Body must be >50% of range
		if ((close - open) / open * 100 < 3.0)
			return false; // At least 3% move
		if ((close - low) / (high - low) < 0.6)
			return false; // Close must be in top 40% of range

		// --- Nifty confirmation ---
		// if (!niftyConfirmation(niftyData, tradeDate)) return false;
		// --- Scoring logic ---
		boolean isBreakout = close > prevHigh;
		boolean bullishCandle = close > open && body > 0.6 * range;
		boolean strongChange = pctChange > avgChange * 1.5;
		boolean highVolume = volume > 2.5 * avgVolume;
		boolean priceAboveEMA = close > latest.getTechnicalIndicator().getEma21()
				&& latest.getTechnicalIndicator().getEma21() > latest.getTechnicalIndicator().getEma50();
		boolean rangeVolatile = range > 0.8 * latest.getTechnicalIndicator().getAtr();
		boolean rsiOk = rsi > 55 && rsi < 75;

		int score = 0;
		if (rsiOk)
			score++;
		if (highVolume)
			score++;
		if (isBreakout)
			score++;
		if (priceAboveEMA)
			score++;
		if (rangeVolatile)
			score++;
		if (bullishCandle)
			score++;
		if (strongChange)
			score++;
		return score >= 4;
	}

	public boolean isValidSetupForFNO(List<StockDailyPrice> data) {

		if (data.size() < 63)
			return false;

		StockDailyPrice latest = data.get(data.size() - 1);
		StockDailyPrice prev = data.get(data.size() - 2);

		double close = latest.getClosePrice();
		double open = latest.getOpenPrice();
		double high = latest.getHighPrice();
		double low = latest.getLowPrice();
		double volume = latest.getVolume();

		double range = high - low;
		double body = Math.abs(close - open);

		double rsi = latest.getTechnicalIndicator().getRsi();
		double pctChange = latest.getTechnicalIndicator().getPercentChange();
		double atr = latest.getTechnicalIndicator().getAtr();

		// 1️⃣ Liquidity filter (mandatory for options)
		if (!isLiquidStock(data))
			return false;

		// 2️⃣ Relative volume (F&O tuned)
		double avgVolume = data.subList(data.size() - 14, data.size()).stream().mapToDouble(StockDailyPrice::getVolume)
				.average().orElse(0);

		boolean smartVolume = close > open && volume > 4.0 * avgVolume && pctChange > 1.3;

		if (!smartVolume)
			return false;

		// 3️⃣ Candle strength (tightened)
		if (body < 0.6 * range)
			return false;

		if ((close - open) / open * 100 < 1.3)
			return false;

		if ((close - low) / range < 0.65)
			return false;

		// 4️⃣ Breakout with ATR buffer
		boolean breakout = close > prev.getHighPrice() + 0.15 * atr;

		if (!breakout)
			return false;

		// 5️⃣ Trend alignment
		boolean trendOk = close > latest.getTechnicalIndicator().getEma21()
				&& latest.getTechnicalIndicator().getEma21() > latest.getTechnicalIndicator().getEma50();

		if (!trendOk)
			return false;

		// 6️⃣ RSI for trending F&O
		boolean rsiOk = rsi > 55 && rsi < 85;
		if (!rsiOk)
			return false;

		// 7️⃣ Volatility expansion
		boolean rangeExpansion = range > 0.9 * atr;
		if (!rangeExpansion)
			return false;

		return true;
	}

	public boolean isLiquidStock(List<StockDailyPrice> data) {
		if (data.size() < 14)
			return false;

		double avgVolume = data.subList(data.size() - 14, data.size()).stream().mapToDouble(StockDailyPrice::getVolume)
				.average().orElse(0.0);

		/*
		 * double avgValue = data.subList(data.size() - 14, data.size()).stream()
		 * .mapToDouble(c -> c.getVolume() * c.getClosePrice()).average().orElse(0.0);
		 */

		if (avgVolume < 90000) {
			// System.out.println("Illiquid stock due to low volume: " + avgVolume);
			return false;
		}

		/*
		 * if (avgValue < 1_00_00_000) { // ₹1 Cr
		 * System.out.println("Illiquid stock due to low traded value: ₹" + avgValue);
		 * return false; }
		 */

		return true;
	}

	private boolean niftyConfirmation(List<NiftyDailyCandle> allNiftyCandles, LocalDate stockDate) {
		List<NiftyDailyCandle> last21 = getNiftySublistEndingOnDate(allNiftyCandles, stockDate, 21);
		List<NiftyDailyCandle> last50 = getNiftySublistEndingOnDate(allNiftyCandles, stockDate, 50);
		List<NiftyDailyCandle> last63 = getNiftySublistEndingOnDate(allNiftyCandles, stockDate, 63);
		List<NiftyDailyCandle> last200 = getNiftySublistEndingOnDate(allNiftyCandles, stockDate, 200);
		List<NiftyDailyCandle> last15 = getNiftySublistEndingOnDate(allNiftyCandles, stockDate, 15); // 14-period
																										// ATR/RSI needs
																										// 15 candles

		if (last21.isEmpty() || last63.isEmpty() || last200.isEmpty())
			return false;

		double latestClose = last21.get(last21.size() - 1).getClose();

		double ema21 = calculateEMA(last21, 21);
		double ema50 = calculateEMA(last50, 50);

		if (ema50 > latestClose)
			return false;
		double ema63 = calculateEMA(last63, 63);
		double ema200 = calculateEMA(last200, 200);

		double atr = calculateATR(last15, 14);
		double rsi = calculateRSI(last15, 14);

		int score = 0;
		if (latestClose > ema21)
			score++;
		if (ema21 > ema63)
			score++;
		if (ema63 > ema200)
			score++;
		if (rsi > 55)
			score++;

		return score >= 3;
	}

	private List<NiftyDailyCandle> getNiftySublistEndingOnDate(List<NiftyDailyCandle> allNiftyCandles, LocalDate date,
			int period) {
		if (allNiftyCandles == null || allNiftyCandles.isEmpty() || period <= 0) {
			return Collections.emptyList();
		}

		int endIndex = IntStream.range(0, allNiftyCandles.size())
				.filter(i -> allNiftyCandles.get(i).getTradeDate().equals(date)).findFirst().orElse(-1);

		int startIndex = endIndex - (period - 1);
		if (endIndex == -1 || startIndex < 0) {
			return Collections.emptyList();
		}

		return new ArrayList<>(allNiftyCandles.subList(startIndex, endIndex + 1));
	}

	private double calculateEMA(List<NiftyDailyCandle> candles, int period) {
		if (candles.size() < period)
			return -1;

		// Calculate initial SMA for first 'period' candles
		double sma = 0;
		for (int i = 0; i < period; i++) {
			sma += candles.get(i).getClose();
		}
		sma /= period;

		double multiplier = 2.0 / (period + 1);
		double ema = sma;

		// Apply EMA formula to remaining candles
		for (int i = period; i < candles.size(); i++) {
			double close = candles.get(i).getClose();
			ema = (close - ema) * multiplier + ema;
		}

		return ema;
	}

	private double calculateATR(List<NiftyDailyCandle> candles, int period) {
		if (candles.size() < period + 1)
			return -1;

		List<Double> trueRanges = new ArrayList<>();

		for (int i = 1; i < candles.size(); i++) {
			double high = candles.get(i).getHigh();
			double low = candles.get(i).getLow();
			double prevClose = candles.get(i - 1).getClose();

			double tr = Math.max(high - low, Math.max(Math.abs(high - prevClose), Math.abs(low - prevClose)));
			trueRanges.add(tr);
		}

		// Step 1: Initial ATR is SMA of first `period` TR values
		double atr = 0;
		for (int i = 0; i < period; i++) {
			atr += trueRanges.get(i);
		}
		atr /= period;

		// Step 2: Apply smoothing formula
		for (int i = period; i < trueRanges.size(); i++) {
			atr = ((atr * (period - 1)) + trueRanges.get(i)) / period;
		}

		return atr;
	}

	private double calculateRSI(List<NiftyDailyCandle> candles, int period) {
		if (candles.size() <= period)
			return -1;

		double gain = 0, loss = 0;

		for (int i = candles.size() - period; i < candles.size(); i++) {
			double change = candles.get(i).getClose() - candles.get(i - 1).getClose();
			if (change > 0)
				gain += change;
			else
				loss += Math.abs(change);
		}

		if (gain == 0)
			return 0;
		if (loss == 0)
			return 100;

		double rs = gain / loss;
		return 100 - (100 / (1 + rs));
	}

}
