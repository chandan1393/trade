package com.fyers.fyerstrading.strategy;

import java.time.LocalTime;
import java.util.List;

import com.fyers.fyerstrading.entity.IntradayTrade;
import com.fyers.fyerstrading.enu.Side;
import com.fyers.fyerstrading.model.MasterCandle;

public class IntradayVWAPSignalEngine {

	private static final double MIN_ATR_PCT = 0.003; // 0.3%
	private static final double MIN_SL_PCT = 0.002; // 0.2%
	private static final int MAX_QTY = 2000;


	public static IntradayTrade evaluate(String symbol, List<MasterCandle> todayCandles,
			List<MasterCandle> prevDayCandles, double capital, double riskPct) {

		// ===== BASIC SAFETY =====
		if (todayCandles == null || prevDayCandles == null)
			return null;
		if (todayCandles.size() < 20)
			return null;

		int idx = todayCandles.size() - 1;
		MasterCandle c = todayCandles.get(idx);

		LocalTime t = c.getTime().toLocalTime();
		if (t.isBefore(LocalTime.of(9, 20)))
			return null;
		if (t.isAfter(LocalTime.of(14, 30)))
			return null;

		// ===== PREVIOUS DAY LEVELS =====
		double prevHigh = prevDayCandles.stream().mapToDouble(MasterCandle::getHigh).max().orElse(0);

		double prevLow = prevDayCandles.stream().mapToDouble(MasterCandle::getLow).min().orElse(0);

		if (prevHigh == 0 || prevLow == 0)
			return null;

		double prevRangePct = (prevHigh - prevLow) / prevLow * 100;
		if (prevRangePct > 1.2)
			return null; // compression filter

		// ===== VWAP (TODAY ONLY) =====
		double cumPV = 0, cumVol = 0;
		for (MasterCandle x : todayCandles) {
			double tp = (x.getHigh() + x.getLow() + x.getClose()) / 3;
			cumPV += tp * x.getVolume();
			cumVol += x.getVolume();
		}
		if (cumVol == 0)
			return null;
		double vwap = cumPV / cumVol;

		// ===== ATR (14) =====
		double atr = calcATR(todayCandles, idx, 14);
		if (atr <= 0)
			return null;
		if (atr < c.getClose() * MIN_ATR_PCT)
			return null;

		double slDistance = 0.5 * atr;
		if (slDistance < c.getClose() * MIN_SL_PCT)
			return null;

		// ===== VOLUME (NO LOOKAHEAD) =====
		double avgVol = todayCandles.subList(0, idx).stream().mapToDouble(MasterCandle::getVolume).average().orElse(0);

		if (avgVol <= 0)
			return null;

		// ===== POSITION SIZING =====
		double riskCapital = capital * riskPct;
		int qty = (int) (riskCapital / slDistance);
		qty = Math.min(qty, MAX_QTY);
		if (qty <= 0)
			return null;

		// ================= BUY =================
		if (c.getClose() > prevHigh && c.getClose() > vwap && c.getVolume() > avgVol * 2) {

			IntradayTrade s = new IntradayTrade();
			s.setSide(Side.BUY);
			s.setEntryPrice(c.getClose());
			s.setInitialSl(c.getClose() - slDistance);
			s.setQuantity(qty);
			return s;
		}

		// ================= SELL =================
		if (c.getClose() < prevLow && c.getClose() < vwap && c.getVolume() > avgVol * 2) {

			IntradayTrade s = new IntradayTrade();
			s.setSide(Side.SELL);
			s.setEntryPrice(c.getClose());
			s.setInitialSl(c.getClose() + slDistance);
			s.setQuantity(qty);
			return s;
		}

		return null;
	}

	// ===== ATR CALC =====
	private static double calcATR(List<MasterCandle> list, int idx, int period) {
		if (idx < period)
			return 0;
		double sum = 0;
		for (int i = idx - period + 1; i <= idx; i++) {
			MasterCandle c = list.get(i);
			sum += (c.getHigh() - c.getLow());
		}
		return sum / period;
	}
}
