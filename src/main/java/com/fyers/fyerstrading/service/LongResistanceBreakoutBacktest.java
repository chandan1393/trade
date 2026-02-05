package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.util.*;

import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.model.MasterCandle;

@Service
public class LongResistanceBreakoutBacktest {

	// ================= CONFIG =================
	private static final int RES_LOOKBACK = 60;
	private static final int ATR_PERIOD = 14;
	private static final int VOL_PERIOD = 20;

	private static final double SL_ATR_MULT = 1.5;
	private static final double TARGET_ATR_MULT = 3.0;
	private static final double GAP_ATR_FILTER = 0.8;
	private static final double VOL_MULT = 1.5;

	static class Trade {
		LocalDate resistanceDate;
		double resistance;

		LocalDate breakoutDate;
		double entryPrice;

		LocalDate exitDate;
		double exitPrice;

		double sl, target;
		double pnl;
		String exitReason;
	}

	// ================= BACKTEST =================
	public List<Trade> backtest(String symbol, List<MasterCandle> candles) {

		List<Trade> trades = new ArrayList<>();
		int i = RES_LOOKBACK;

		while (i < candles.size() - 1) {

			// STEP 1: Find resistance
			int resIndex = findResistance(candles, i);
			if (resIndex == -1) {
				i++;
				continue;
			}

			double resistance = candles.get(resIndex).getHigh();
			LocalDate resistanceDate = candles.get(resIndex).getTime().toLocalDate();

			// STEP 2–3: From next day check breakout
			int j = resIndex + 1;
			boolean tradeTaken = false;

			while (j < candles.size()) {

				MasterCandle c = candles.get(j);
				double atr = atr(candles, j);
				double avgVol = avgVolume(candles, j);

				// Volume confirmation
				if (c.getClose() > resistance && c.getVolume() > avgVol * VOL_MULT) {

					// Gap-up false breakout filter
					if (c.getOpen() > resistance + GAP_ATR_FILTER * atr) {
						j++;
						continue;
					}

					// STEP 4: BUY
					Trade trade = new Trade();
					trade.resistance = resistance;
					trade.resistanceDate = resistanceDate;
					trade.breakoutDate = c.getTime().toLocalDate();
					trade.entryPrice = c.getClose();

					trade.sl = trade.entryPrice - SL_ATR_MULT * atr;
					trade.target = trade.entryPrice + TARGET_ATR_MULT * atr;

					// STEP 5: Track SL / Target
					int k = j + 1;
					while (k < candles.size()) {
						MasterCandle e = candles.get(k);

						if (e.getLow() <= trade.sl) {
							trade.exitDate = e.getTime().toLocalDate();
							trade.exitPrice = trade.sl;
							trade.exitReason = "SL HIT";
							break;
						}

						if (e.getHigh() >= trade.target) {
							trade.exitDate = e.getTime().toLocalDate();
							trade.exitPrice = trade.target;
							trade.exitReason = "TARGET HIT";
							break;
						}
						k++;
					}

					if (trade.exitDate == null)
						break;

					trade.pnl = trade.exitPrice - trade.entryPrice;
					trades.add(trade);

					// STEP 6: Restart after exit
					i = k + 1;
					tradeTaken = true;
					break;
				}
				j++;
			}

			if (!tradeTaken)
				i++;
		}

		return trades;
	}

	// ================= HELPERS =================
	private int findResistance(List<MasterCandle> c, int end) {
		int start = end - RES_LOOKBACK;
		double maxHigh = 0;
		int idx = -1;

		for (int i = start; i < end; i++) {
			if (c.get(i).getHigh() > maxHigh) {
				maxHigh = c.get(i).getHigh();
				idx = i;
			}
		}

		// Unbroken check
		for (int i = idx + 1; i <= end; i++) {
			if (c.get(i).getClose() > maxHigh)
				return -1;
		}
		return idx;
	}

	private double atr(List<MasterCandle> c, int idx) {
		double sum = 0;
		for (int i = idx - ATR_PERIOD + 1; i <= idx; i++) {
			MasterCandle cur = c.get(i);
			MasterCandle prev = c.get(i - 1);
			double tr = Math.max(cur.getHigh() - cur.getLow(),
					Math.max(Math.abs(cur.getHigh() - prev.getClose()), Math.abs(cur.getLow() - prev.getClose())));
			sum += tr;
		}
		return sum / ATR_PERIOD;
	}

	private double avgVolume(List<MasterCandle> c, int idx) {
		double sum = 0;
		for (int i = idx - VOL_PERIOD + 1; i <= idx; i++)
			sum += c.get(i).getVolume();
		return sum / VOL_PERIOD;
	}
}
