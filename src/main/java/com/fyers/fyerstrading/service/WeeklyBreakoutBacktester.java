package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.model.BacktestResult;
import com.fyers.fyerstrading.model.BreakoutSetupResult;
import com.fyers.fyerstrading.model.WeeklyCandle;
import com.fyers.fyerstrading.utility.CandleConverter;

@Service
public class WeeklyBreakoutBacktester {

	public List<BacktestResult> runBacktestFromDaily(String symbol, List<StockDailyPrice> dailyCandles) {
		List<WeeklyCandle> weeklyCandles = CandleConverter.convertToWeekly(dailyCandles);
		return runBacktest(symbol, weeklyCandles, dailyCandles);
	}

	public List<BacktestResult> runBacktest(String symbol, List<WeeklyCandle> weeklyCandles,
			List<StockDailyPrice> dailyCandles) {
		List<BacktestResult> results = new ArrayList<>();
		AtomicReference<LocalDate> lastExitDateRef = new AtomicReference<>(LocalDate.MIN);

		for (int i = 4; i < weeklyCandles.size(); i++) {
			WeeklyCandle w1 = weeklyCandles.get(i - 4);
			WeeklyCandle w2 = weeklyCandles.get(i - 3);
			WeeklyCandle w3 = weeklyCandles.get(i - 2);
			WeeklyCandle w4 = weeklyCandles.get(i - 1);

			double high4w = Stream.of(w1, w2, w3, w4).mapToDouble(WeeklyCandle::getHigh).max().orElse(0);
			double avgVol4w = Stream.of(w1, w2, w3, w4).mapToDouble(WeeklyCandle::getVolume).average().orElse(1);

			boolean allBelowW1 = w2.getHigh() < w1.getHigh() && w3.getHigh() < w1.getHigh()
					&& w4.getHigh() < w1.getHigh();
			boolean tightRange = (w2.getHigh() - w2.getLow()) < 0.05 * w2.getLow()
					&& (w3.getHigh() - w3.getLow()) < 0.05 * w3.getLow();

			int score = calculateScore(allBelowW1, tightRange);

			List<StockDailyPrice> dailySubset = dailyCandles.stream()
					.filter(d -> !d.getTradeDate().isBefore(w4.getWeekStarting())).collect(Collectors.toList());

			Optional<BacktestResult> breakout = detectBreakout(symbol, dailySubset, high4w, avgVol4w, score,
					lastExitDateRef.get());

			if (breakout.isPresent()) {
				BacktestResult result = breakout.get();
				Optional<BacktestResult> fullTrade = handleEntry(result, dailyCandles, 20000); // capital per trade

				if (fullTrade.isPresent()) {
					simulateExit(fullTrade.get(), dailyCandles);
					lastExitDateRef.set(result.getExitDate());
					results.add(result);
				}
			}
		}
		return results;
	}

	private int calculateScore(boolean allBelowW1, boolean tightRange) {
		int score = 0;
		if (allBelowW1)
			score += 2;
		if (tightRange)
			score += 1;
		return score;
	}

	private Optional<BacktestResult> detectBreakout(String symbol, List<StockDailyPrice> daily, double high4w,
			double avgVol4w, int baseScore, LocalDate lastExitDate) {

		for (StockDailyPrice candle : daily) {
			if (candle.getTradeDate().isBefore(lastExitDate))
				continue;

			boolean breakout = candle.getHighPrice() > high4w && candle.getClosePrice() > high4w;
			boolean volumeSpike = candle.getVolume() > 1.5 * avgVol4w;

			if (breakout && volumeSpike) {
				int score = baseScore + volumeScore(volumeSpike);

				/*
				 * if (score < 5) continue;
				 */

				BacktestResult res = new BacktestResult();
				res.setSymbol(symbol);
				res.setBreakoutCandleDate(candle.getTradeDate());
				res.setBreakoutVolume(candle.getVolume());
				res.setScore(score);
				res.setAggressive(false);
				// res.setAggressive(score >= 7);
				return Optional.of(res);
			}
		}
		return Optional.empty();
	}

	private int volumeScore(boolean volumeSpike) {
		return volumeSpike ? 2 : 0;
	}

	private Optional<BacktestResult> handleEntry(BacktestResult breakout, List<StockDailyPrice> allDaily,
			double capitalPerTrade) {
		List<StockDailyPrice> afterBreakout = allDaily.stream()
				.filter(d -> d.getTradeDate().isAfter(breakout.getBreakoutCandleDate())).collect(Collectors.toList());

		if (afterBreakout.isEmpty())
			return Optional.empty();

		StockDailyPrice entryCandle = afterBreakout.get(0);
		StockDailyPrice breakoutCandle = allDaily.stream()
				.filter(d -> d.getTradeDate().equals(breakout.getBreakoutCandleDate())).findFirst().orElse(null);

		if (breakoutCandle == null)
			return Optional.empty();

		// === TECHNICAL FILTERS ===
		// if (breakoutCandle.getTechnicalIndicator().getRsi() < 55 ) return
		// Optional.empty(); // RSI + VWAP
		if (breakoutCandle.getTechnicalIndicator().getPercentChange() < 1.5)
			return Optional.empty(); // candle body / wick size check
		// if (!breakout.isConfirmedByHigherTimeframe()) return Optional.empty(); //
		// Multi-timeframe confirmation

		double entry = entryCandle.getOpenPrice();
		double sl = breakoutCandle.getLowPrice();

		double quantity = Math.floor(capitalPerTrade / entry);
		double capitalUsed = quantity * entry;

		// === CONSOLIDATION & TARGET LOGIC ===
		// long consolidationDays =
		// ChronoUnit.DAYS.between(breakout.getStartConsolidation(),
		// breakout.getBreakoutCandleDate());
		double range = breakoutCandle.getHighPrice() - breakoutCandle.getLowPrice();

		double target1 = entry + 1.5 * range;
		double target2 = entry + 2.5 * range;
		// int holdingDays = consolidationDays >= 20 ? 10 : 5;

		// Set data
		breakout.setEntryDate(entryCandle.getTradeDate());
		breakout.setEntry(entry);
		breakout.setStopLoss(sl);
		breakout.setTarget1(target1);
		breakout.setTarget2(target2);
		breakout.setQuantity(quantity);
		breakout.setCapitalUsed(capitalUsed);

		simulateExit(breakout, allDaily);
		return Optional.of(breakout);
	}

	private void simulateExit(BacktestResult res, List<StockDailyPrice> dailyCandles) {
		double entry = res.getEntry();
		double target1 = res.getTarget1();
		double target2 = res.getTarget2();
		double sl = res.getStopLoss();
		LocalDate entryDate = res.getEntryDate();
		double quantity = res.getQuantity();
		int maxHoldingDays = 15;
		int daysHeld = 0;

		double qty1 = Math.floor(quantity / 2.0); // 50% exit at Target1
		double qty2 = quantity - qty1; // Remaining for Target2 or Time Exit

		boolean target1Hit = false;
		boolean finalExitDone = false;

		for (StockDailyPrice d : dailyCandles) {
			if (d.getTradeDate().isBefore(entryDate))
				continue;

			if (!target1Hit && d.getHighPrice() >= target1) {
				target1Hit = true;
				res.setExitDate(d.getTradeDate());
				res.setQuantitySoldAtTarget1(qty1);
			}

			// If after Target1, SL hits => exit remaining at SL
			if (target1Hit && d.getLowPrice() <= sl) {
				res.setExitReason("Partial T1 Hit, SL Hit");
				res.setQuantitySoldAtTarget2(qty2);
				res.setExitDate(d.getTradeDate());

				double totalPnl = (target1 - entry) * qty1 + (sl - entry) * qty2;
				double avgExit = (target1 * qty1 + sl * qty2) / quantity;

				res.setAverageExitPrice(avgExit);
				res.setExitPrice(avgExit);
				res.setProfitLoss(totalPnl);
				res.setPnlPercent(100.0 * totalPnl / (entry * quantity));
				return;
			}

			if (res.isAggressive() && target1Hit && d.getHighPrice() >= target2) {
				res.setExitReason("Target1 + Target2 Hit");
				res.setExitDate(d.getTradeDate());
				res.setQuantitySoldAtTarget2(qty2);

				double totalPnl = (target1 - entry) * qty1 + (target2 - entry) * qty2;
				double avgExit = (target1 * qty1 + target2 * qty2) / quantity;

				res.setAverageExitPrice(avgExit);
				res.setExitPrice(avgExit);
				res.setProfitLoss(totalPnl);
				res.setPnlPercent(100.0 * totalPnl / (entry * quantity));
				return;
			}

			// If SL hits before Target1
			if (!target1Hit && d.getLowPrice() <= sl) {
				res.setExitReason("Full StopLoss");
				res.setExitDate(d.getTradeDate());
				res.setExitPrice(sl);
				res.setProfitLoss((sl - entry) * quantity);
				res.setPnlPercent(-100.0 * (entry - sl) / entry);
				return;
			}

			daysHeld++;
			if (daysHeld >= maxHoldingDays)
				break;
		}

		// Fallback Time Exit (based on remaining qty)
		List<StockDailyPrice> holdingWindow = dailyCandles.stream().filter(d -> !d.getTradeDate().isBefore(entryDate))
				.limit(maxHoldingDays).collect(Collectors.toList());

		if (!holdingWindow.isEmpty()) {
			StockDailyPrice lastDay = holdingWindow.get(holdingWindow.size() - 1);
			double timeExitPrice = lastDay.getClosePrice();
			res.setExitDate(lastDay.getTradeDate());

			if (target1Hit) {
				res.setExitReason("Partial T1 Hit, Time Exit");
				res.setQuantitySoldAtTarget2(qty2);

				double totalPnl = (target1 - entry) * qty1 + (timeExitPrice - entry) * qty2;
				double avgExit = (target1 * qty1 + timeExitPrice * qty2) / quantity;

				res.setAverageExitPrice(avgExit);
				res.setExitPrice(avgExit);
				res.setProfitLoss(totalPnl);
				res.setPnlPercent(100.0 * totalPnl / (entry * quantity));
			} else {
				// No Target hit, full exit at time
				res.setExitReason("Time Exit - Full");
				res.setExitPrice(timeExitPrice);
				double pnl = (timeExitPrice - entry) * quantity;
				res.setProfitLoss(pnl);
				res.setPnlPercent(100.0 * pnl / (entry * quantity));
			}
		} else {
			res.setExitDate(entryDate.plusDays(maxHoldingDays));
			res.setExitPrice(entry);
			res.setExitReason("Time Exit - No Data");
			res.setPnlPercent(0);
			res.setProfitLoss(0);
		}
	}

	public List<BreakoutSetupResult> findMonthlyBreakoutSetups(List<WeeklyCandle> weeklyCandles, String stockName) {
		List<BreakoutSetupResult> setups = new ArrayList<>();
		int lookbackVolume = 8;
		int forwardCandleCount = 8;
		int weeksToSkipAfterSetup = 4;

		int i = lookbackVolume;
		while (i < weeklyCandles.size() - forwardCandleCount) {
			WeeklyCandle current = weeklyCandles.get(i);

			// 1. Must be a strong green candle
			if (current.getClose() <= current.getOpen()) {
				i++;
				continue;
			}

			double candleRange = current.getHigh() - current.getLow();
			double bodySize = Math.abs(current.getClose() - current.getOpen());
			double bodyPercent = (bodySize / candleRange) * 100;

			// 2. Reject small-body or indecision candles
			if (bodyPercent < 40) { // You can tune this
				i++;
				continue;
			}

			// 3. No upper wick fakeout: upper wick should be small
			double upperWick = current.getHigh() - Math.max(current.getOpen(), current.getClose());
			double upperWickPercent = (upperWick / candleRange) * 100;
			if (upperWickPercent > 30) {
				i++;
				continue;
			}

			// 4. Volume > 4 * average of last N volumes
			double totalVolume = 0;
			for (int j = i - lookbackVolume; j < i; j++) {
				totalVolume += weeklyCandles.get(j).getVolume();
			}
			double avgVolume = totalVolume / lookbackVolume;
			if (current.getVolume() <= 4 * avgVolume) {
				i++;
				continue;
			}

			// 5. Volume-Price Confirmation: price + volume move together
			boolean volumePriceConfirmed = true;
			for (int j = i - 3; j < i; j++) {
				if (weeklyCandles.get(j).getClose() > weeklyCandles.get(j + 1).getClose()
						&& weeklyCandles.get(j).getVolume() < weeklyCandles.get(j + 1).getVolume()) {
					volumePriceConfirmed = false;
					break;
				}
			}
			if (!volumePriceConfirmed) {
				i++;
				continue;
			}

			// 6. Low Volatility Prior: previous candles should be narrow and low body
			boolean lowVolatility = true;
			for (int j = i - 3; j < i; j++) {
				WeeklyCandle wc = weeklyCandles.get(j);
				double range = wc.getHigh() - wc.getLow();
				double bodysize = Math.abs(wc.getClose() - wc.getOpen());
				if ((bodysize / range) * 100 > 40) { // if recent candles had large bodies, skip
					lowVolatility = false;
					break;
				}
			}
			if (!lowVolatility) {
				i++;
				continue;
			}

			// 7. High must not be broken in next M candles
			boolean highBroken = false;
			for (int j = i + 1; j <= i + forwardCandleCount; j++) {
				if (weeklyCandles.get(j).getClose() > current.getHigh()) {
					highBroken = true;
					break;
				}
			}

			if (!highBroken) {
				// Valid Setup
				/*
				 * System.out.println("Breakout Setup: " + stockName + " | Week: " +
				 * current.getWeekStarting() + " | High: " + current.getHigh() + " | Volume: " +
				 * current.getVolume() + " | AvgVol: " + avgVolume + " | Body%: " +
				 * String.format("%.1f", bodyPercent) + " | UpperWick%: " +
				 * String.format("%.1f", upperWickPercent));
				 */
				
				BreakoutSetupResult result = new BreakoutSetupResult(stockName, current.getWeekStarting(),
						current.getHigh(), current.getLow(), current.getOpen(), current.getClose(), current.getVolume(),
						avgVolume, bodyPercent, upperWickPercent, false);
				
				setups.add(result);
				i += weeksToSkipAfterSetup;
			} else {
				i++;
			}
		}

		return setups;
	}

}
