package com.fyers.fyerstrading.strategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.enu.Side;
import com.fyers.fyerstrading.model.MasterCandle;
import com.fyers.fyerstrading.model.TradeResult;
import com.fyers.fyerstrading.model.TradeState;

@Service
public class IntradayVWAPBacktest {

	// ================= CONFIG =================
	static final double MIN_ATR_PCT = 0.002; // 0.2%
	static final long MIN_VWAP_VOLUME = 200_000;
	static final int MAX_QTY = 5000;

	enum Bias {
		BULLISH, BEARISH, NONE
	}

	// ================= MAIN BACKTEST =================
	public static List<TradeResult> backtest(String symbol, List<MasterCandle> candles, double capital,
			double riskPct) {

		Map<LocalDate, List<MasterCandle>> byDay = splitByDay(candles);
		List<TradeResult> results = new ArrayList<>();

		List<LocalDate> days = new ArrayList<>(byDay.keySet());
		Collections.sort(days);

		for (int i = 1; i < days.size(); i++) {

			List<MasterCandle> prev = byDay.get(days.get(i - 1));
			List<MasterCandle> cur = byDay.get(days.get(i));

			if (!isValidTradingDay(prev, cur))
				continue;

			Bias bias = detectBias(prev);
			if (bias == Bias.NONE)
				continue;

			TradeState s = new TradeState(capital, riskPct);

			for (int j = 1; j < cur.size(); j++) {

				MasterCandle c = cur.get(j);
				buildVWAP(s, c);

				if (!isValidSession(c, s, j))
					continue;

				double atr = calcATR(cur, j, 14);
					double avgVol = avgVolume(cur.subList(0, j));

				if (atr <= 0 || atr < c.getClose() * MIN_ATR_PCT)
					continue;

				if (!s.tradeTaken) {
					tryEntry(cur, c, j, s, atr, avgVol, bias);
				} else {
					if (checkExitFirst(c, s, days.get(i), results))
						break; // ONE TRADE PER DAY
				}
			}

			handleTimeExit(cur, s, days.get(i), results);
		}

		return results;
	}

	// ================= DAY FILTER =================
	private static boolean isValidTradingDay(List<MasterCandle> prev, List<MasterCandle> cur) {
		if (prev == null || cur == null || cur.size() < 20)
			return false;

		double high = prev.stream().mapToDouble(MasterCandle::getHigh).max().orElse(0);
		double low = prev.stream().mapToDouble(MasterCandle::getLow).min().orElse(0);

		return ((high - low) / low) * 100 <= 1.5;
	}

	// ================= DAILY BIAS =================
	private static Bias detectBias(List<MasterCandle> prev) {

		MasterCandle first = prev.get(0);
		MasterCandle last = prev.get(prev.size() - 1);

		double body = Math.abs(last.getClose() - first.getOpen());
		double range = prev.stream().mapToDouble(c -> c.getHigh() - c.getLow()).sum();

		if (body / range < 0.4)
			return Bias.NONE;

		return last.getClose() > first.getOpen() ? Bias.BULLISH : Bias.BEARISH;
	}

	// ================= VWAP =================
	private static void buildVWAP(TradeState s, MasterCandle c) {
		double tp = (c.getHigh() + c.getLow() + c.getClose()) / 3;
		s.cumPV += tp * c.getVolume();
		s.cumVol += c.getVolume();
		if (s.cumVol > 0)
			s.vwap = s.cumPV / s.cumVol;
	}

	// ================= SESSION FILTER =================
	private static boolean isValidSession(MasterCandle c, TradeState s, int idx) {

		LocalTime t = c.getTime().toLocalTime();

		if (t.isBefore(LocalTime.of(9, 20)))
			return false;
		if (t.isAfter(LocalTime.of(15, 0)))
			return false;
		if (idx < 10)
			return false;

		// lunch chop filter
		if (t.isAfter(LocalTime.of(12, 0)) && t.isBefore(LocalTime.of(13, 30)))
			return false;

		return s.cumVol >= MIN_VWAP_VOLUME;
	}

	// ================= ENTRY =================
	private static void tryEntry(List<MasterCandle> cur, MasterCandle c, int j, TradeState s, double atr, double avgVol,
			Bias bias) {

		if (avgVol <= 0 || c.getVolume() < avgVol * 2)
			return;

		double riskCapital = s.capital * s.riskPct;

		// BUY
		if (bias == Bias.BULLISH && vwapReclaim(cur, j, s.vwap, true)) {

			s.side = Side.BUY;
			s.entry = c.getClose();
			s.riskPerShare = atr * 1.2;
			s.sl = s.entry - s.riskPerShare;

			initializeTrade(s, c, riskCapital, c.getHigh());
		}

		// SELL
		if (bias == Bias.BEARISH && vwapReclaim(cur, j, s.vwap, false)) {

			s.side = Side.SELL;
			s.entry = c.getClose();
			s.riskPerShare = atr * 1.2;
			s.sl = s.entry + s.riskPerShare;

			initializeTrade(s, c, riskCapital, c.getLow());
		}
	}

	private static boolean vwapReclaim(List<MasterCandle> cur, int j, double vwap, boolean buy) {

		MasterCandle prev = cur.get(j - 1);
		MasterCandle curr = cur.get(j);

		return buy ? prev.getClose() < vwap && curr.getClose() > vwap
				: prev.getClose() > vwap && curr.getClose() < vwap;
	}

	private static void initializeTrade(TradeState s, MasterCandle c, double riskCapital, double bestPrice) {

		s.qty = Math.min((int) (riskCapital / s.riskPerShare), MAX_QTY);
		if (s.qty <= 0)
			return;

		s.tsl = s.sl;
		s.bestPrice = bestPrice;
		s.entryTime = s.setupTime = c.getTime();
		s.tradeTaken = true;
	}

	// ================= EXIT =================
	private static boolean checkExitFirst(MasterCandle c, TradeState s, LocalDate day, List<TradeResult> results) {

		// HARD SL FIRST
		if (s.side == Side.BUY && c.getLow() <= s.tsl) {
			recordExit(day, s, s.tsl, c.getTime(), results, "SL");
			return true;
		}

		if (s.side == Side.SELL && c.getHigh() >= s.tsl) {
			recordExit(day, s, s.tsl, c.getTime(), results, "SL");
			return true;
		}

		// update best price
		if (s.side == Side.BUY)
			s.bestPrice = Math.max(s.bestPrice, c.getHigh());
		else
			s.bestPrice = Math.min(s.bestPrice, c.getLow());

		// trail only after 2R
		if (!s.trailActive) {
			if (s.side == Side.BUY && s.bestPrice >= s.entry + s.riskPerShare * 2)
				s.trailActive = true;
			if (s.side == Side.SELL && s.bestPrice <= s.entry - s.riskPerShare * 2)
				s.trailActive = true;
		}

		if (s.trailActive) {
			if (s.side == Side.BUY)
				s.tsl = Math.max(s.tsl, s.bestPrice - s.riskPerShare);
			else
				s.tsl = Math.min(s.tsl, s.bestPrice + s.riskPerShare);
		}

		return false;
	}

	private static void handleTimeExit(List<MasterCandle> cur, TradeState s, LocalDate day, List<TradeResult> results) {

		if (s.tradeTaken && !s.exited) {
			MasterCandle last = cur.get(cur.size() - 1);
			recordExit(day, s, last.getClose(), last.getTime(), results, "TIME_EXIT");
		}
	}

	private static void recordExit(LocalDate day, TradeState s, double exitPrice, LocalDateTime exitTime,
			List<TradeResult> results, String reason) {

		results.add(build(day, s.side, s.entry, exitPrice, s.qty, reason, s.setupTime, s.entryTime, exitTime));

		s.exited = true;
	}

	// ================= STATE =================
	static class TradeState {
		double capital, riskPct;
		double cumPV = 0, cumVol = 0, vwap = 0;

		Side side;
		double entry, sl, tsl, riskPerShare, bestPrice;
		int qty;

		boolean tradeTaken = false;
		boolean exited = false;
		boolean trailActive = false;

		LocalDateTime setupTime, entryTime;

		TradeState(double capital, double riskPct) {
			this.capital = capital;
			this.riskPct = riskPct;
		}
	}

// ================= HELPERS =================

	private static Map<LocalDate, List<MasterCandle>> splitByDay(List<MasterCandle> candles) {
		Map<LocalDate, List<MasterCandle>> map = new HashMap<>();
		for (MasterCandle c : candles) {
			map.computeIfAbsent(c.getTime().toLocalDate(), k -> new ArrayList<>()).add(c);
		}
		return map;
	}

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

	private static double avgVolume(List<MasterCandle> list) {
		return list.stream().mapToDouble(MasterCandle::getVolume).average().orElse(0);
	}

	private static TradeResult build(LocalDate tradeDate, Side side, double entry, double exit, int qty, String reason,
			LocalDateTime setupTime, LocalDateTime entryTime, LocalDateTime exitTime) {

		TradeResult r = new TradeResult();
		r.setTradeDate(tradeDate);
		r.setSide(side);
		r.setEntryPrice(entry);
		r.setExitPrice(exit);
		r.setQty(qty);
		r.setExitReason(reason);
		r.setSetupTime(setupTime);
		r.setEntryTime(entryTime);
		r.setExitTime(exitTime);

		r.setPnl(side == Side.BUY ? (exit - entry) * qty : (entry - exit) * qty);

		return r;
	}

}
