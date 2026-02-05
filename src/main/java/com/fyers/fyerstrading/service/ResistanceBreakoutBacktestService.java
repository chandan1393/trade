package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.model.BacktestResultForResistenceBreakout;
import com.fyers.fyerstrading.model.ResistanceSetup;

@Service
public class ResistanceBreakoutBacktestService {

	private static final int MAX_TRADES_PER_STOCK = 200;

	public List<BacktestResultForResistenceBreakout> backtest(String symbol, List<StockDailyPrice> candles) {

		List<BacktestResultForResistenceBreakout> results = new ArrayList<>();

		if (candles == null || candles.size() < 100)
			return results;

		int i = 60;

		while (i < candles.size() - 50) { // ⛔ stop early so forward look works

			if (results.size() >= MAX_TRADES_PER_STOCK)
				break; // avoid memory explosion

			ResistancePoint res = findDynamic35DayResistance(candles, i);

			if (res == null) {
				i++;
				continue;
			}

			// ⛔ PROTECTION: breakout index must be > resistance index
			if (res.breakoutIndex <= res.resistanceIndex) {
				i++;
				continue;
			}

			// ⛔ PROTECTION: breakout must be ahead of current position
			if (res.breakoutIndex <= i) {
				i++;
				continue;
			}

			BacktestResultForResistenceBreakout trade = executeTrade(candles, res.breakoutIndex, symbol,
					res.resistance);

			if (trade != null)
				results.add(trade);

			// Move index forward safely
			i = res.breakoutIndex + 1;
		}

		return results;
	}

	// --------------------------------------------------------------------
	// -------------------- DYNAMIC 35-DAY RESISTANCE -------------------
	// --------------------------------------------------------------------

	/**
	 * Follows exact logic: 1. Find swing high (20–60 lookback) 2. If broken <35
	 * days → new resistance from that candle 3. If broken >35 days → valid breakout
	 */
	private ResistancePoint findDynamic35DayResistance(List<StockDailyPrice> candles, int startIndex) {

		int lookbackStart = Math.max(0, startIndex - 60);
		int lookbackEnd = startIndex - 20;

		if (lookbackEnd <= lookbackStart)
			return null;

		double res = 0;
		int resIndex = -1;

		for (int j = lookbackStart; j < lookbackEnd; j++) {
			if (candles.get(j).getHighPrice() > res) {
				res = candles.get(j).getHighPrice();
				resIndex = j;
			}
		}
		if (resIndex == -1)
			return null;

		double current = res;
		int currentIndex = resIndex;

		for (int k = resIndex + 1; k < candles.size(); k++) {

			double high = candles.get(k).getHighPrice();

			if (high > current) {
				int diff = k - currentIndex;

				if (diff < 35) {
					// update resistance
					current = high;
					currentIndex = k;
					continue;
				}

				// valid breakout
				return new ResistancePoint(current, currentIndex, k);
			}
		}

		return null;
	}

	// --------------------------------------------------------------------
	// --------------------------- TRADE LOGIC ----------------------------
	// --------------------------------------------------------------------

	private BacktestResultForResistenceBreakout executeTrade(List<StockDailyPrice> candles, int breakoutIndex,
			String symbol, double resistance) {

		if (breakoutIndex + 1 >= candles.size())
			return null;

		StockDailyPrice breakoutCandle = candles.get(breakoutIndex);

		// -------------------------------
		// ENTRY & STOPLOSS
		// -------------------------------
		double entry = resistance; // Buy at breakout of resistance
		double sl = breakoutCandle.getLowPrice(); // Initial SL = breakout candle low

		// RISK CALCULATION
		double risk = entry - sl;

		// TARGETS
		double target = entry + (risk * 5); // 1:5 target
		double target2 = entry + (risk * 2); // 1:2 level (SL shift trigger)

		boolean slMovedToEntry = false; // To track SL update

		// -------------------------------
		// SIMULATE THE TRADE
		// -------------------------------
		for (int i = breakoutIndex + 1; i < candles.size(); i++) {

			double high = candles.get(i).getHighPrice();
			double low = candles.get(i).getLowPrice();
			double close = candles.get(i).getClosePrice();
			LocalDate exitDate = candles.get(i).getTradeDate();

			// 🔹 If price reaches 1:2, move SL to Entry
			if (!slMovedToEntry && high >= target2) {
				sl = entry; // move SL to breakeven
				slMovedToEntry = true;
			}

			// 🔹 Stoploss hit
			if (low <= sl) {
				return new BacktestResultForResistenceBreakout(symbol, breakoutCandle.getTradeDate(), exitDate,
						sl - entry // could be negative or 0 after SL shift
				);
			}

			// 🔹 Target hit (1:5)
			if (high >= target) {
				return new BacktestResultForResistenceBreakout(symbol, breakoutCandle.getTradeDate(), exitDate,
						target - entry);
			}

			// 🔹 If last candle → exit at close
			if (i == candles.size() - 1) {
				return new BacktestResultForResistenceBreakout(symbol, breakoutCandle.getTradeDate(), exitDate,
						close - entry);
			}
		}

		return null;
	}

	private static class ResistancePoint {
		double resistance;
		int resistanceIndex;
		int breakoutIndex;

		ResistancePoint(double r, int rIndex, int bIndex) {
			this.resistance = r;
			this.resistanceIndex = rIndex;
			this.breakoutIndex = bIndex;
		}
	}

}
