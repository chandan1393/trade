package com.fyers.fyerstrading.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.entity.IndexOIData;
import com.fyers.fyerstrading.model.BiasResult;
import com.fyers.fyerstrading.model.DailyLevels;
import com.fyers.fyerstrading.model.DailyOiSummary;
import com.fyers.fyerstrading.model.WeeklyOiSummary;

@Component
public class DailyIndexOIAnalyzer {

	public DailyOiSummary analyzeDay(List<IndexOIData> dayData, WeeklyOiSummary weekly, double dayHigh, double dayLow,
			double dayClose) {

		DailyOiSummary d = new DailyOiSummary();

		// ---------- 1) Support & Resistance ----------
		DailyLevels levels = findDailyLevels(dayData);
		d.support = levels.support;
		d.resistance = levels.resistance;

		// ---------- 2) Daily Bias ----------
		BiasResult bias = calculateBias(dayData);
		d.bias = bias.value;
		d.biasReason = bias.reason;

		// ---------- 3) Weekly Context (Optional) ----------
		if (weekly != null) {
			d.weeklySupportBroken = dayLow < weekly.support;
			d.weeklyResistanceBroken = dayHigh > weekly.resistance;
		} else {
			d.weeklySupportBroken = false;
			d.weeklyResistanceBroken = false;
		}

		// ---------- 4) Trapped Players (Today) ----------
		d.trappedCalls = findTrappedCalls(dayData);
		d.trappedPuts = findTrappedPuts(dayData);

		// ---------- 5) Volatility Regime ----------
		d.volatility = calcVolatility(dayData);

		// ---------- 6) Build Tomorrow’s Trade Plan ----------
		d.tradePlan = buildTomorrowPlan(d, weekly, dayClose);

		return d;
	}

	public DailyLevels findDailyLevels(List<IndexOIData> dayData) {

		Map<LocalDateTime, List<IndexOIData>> byTime = dayData.stream().collect(Collectors.groupingBy(IndexOIData::getTimestamp));

		List<LocalDateTime> times = byTime.keySet().stream().sorted().toList();

		Map<Double, Long> putWriting = new HashMap<>();
		Map<Double, Long> callWriting = new HashMap<>();

		for (int i = 1; i < times.size(); i++) {
			List<IndexOIData> cur = byTime.get(times.get(i));
			List<IndexOIData> prev = byTime.get(times.get(i - 1));
			if (cur == null || prev == null)
				continue;

			Map<Double, IndexOIData> prevMap = prev.stream().collect(Collectors.toMap(IndexOIData::getStrikePrice, x -> x));

			for (IndexOIData c : cur) {
				IndexOIData p = prevMap.get(c.getStrikePrice());
				if (p == null)
					continue;

				// Put writing → support
				if (c.getPutChgOI() != null && c.getPutChgOI() > 0 && c.getPutLTP() != null && p.getPutLTP() != null
						&& c.getPutLTP() < p.getPutLTP()) {

					putWriting.merge(c.getStrikePrice(), c.getPutChgOI().longValue(), Long::sum);
				}

				// Call writing → resistance
				if (c.getCallChgOI() != null && c.getCallChgOI() > 0 && c.getCallLTP() != null && p.getCallLTP() != null
						&& c.getCallLTP() < p.getCallLTP()) {

					callWriting.merge(c.getStrikePrice(), c.getCallChgOI().longValue(), Long::sum);
				}
			}
		}

		DailyLevels d = new DailyLevels();
		d.support = putWriting.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(0.0);

		d.resistance = callWriting.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
				.orElse(0.0);

		return d;
	}

	public BiasResult calculateBias(List<IndexOIData> dayData) {

		long putWriting = 0;
		long callWriting = 0;

		Map<LocalDateTime, List<IndexOIData>> byTime = dayData.stream().collect(Collectors.groupingBy(IndexOIData::getTimestamp));

		List<LocalDateTime> times = byTime.keySet().stream().sorted().toList();

		for (int i = 1; i < times.size(); i++) {
			List<IndexOIData> cur = byTime.get(times.get(i));
			List<IndexOIData> prev = byTime.get(times.get(i - 1));
			if (cur == null || prev == null)
				continue;

			Map<Double, IndexOIData> prevMap = prev.stream().collect(Collectors.toMap(IndexOIData::getStrikePrice, x -> x));

			for (IndexOIData c : cur) {
				IndexOIData p = prevMap.get(c.getStrikePrice());
				if (p == null)
					continue;

				if (c.getPutChgOI() != null && c.getPutChgOI() > 0 && c.getPutLTP() < p.getPutLTP()) {
					putWriting += c.getPutChgOI();
				}

				if (c.getCallChgOI() != null && c.getCallChgOI() > 0 && c.getCallLTP() < p.getCallLTP()) {
					callWriting += c.getCallChgOI();
				}
			}
		}

		BiasResult r = new BiasResult();

		if (putWriting > callWriting * 1.2) {
			r.value = "BULLISH";
			r.reason = "Heavy put writing (" + putWriting + ") defending downside.";
		} else if (callWriting > putWriting * 1.2) {
			r.value = "BEARISH";
			r.reason = "Heavy call writing (" + callWriting + ") capping upside.";
		} else {
			r.value = "RANGE";
			r.reason = "Balanced call & put writing → range control.";
		}

		return r;
	}

	public List<Double> findTrappedCalls(List<IndexOIData> data) {
		Map<Double, Long> unwind = new HashMap<>();

		for (IndexOIData r : data) {
			if (r.getCallChgOI() != null && r.getCallChgOI() < 0) {
				unwind.merge(r.getStrikePrice(), (long) Math.abs(r.getCallChgOI()), Long::sum);
			}
		}

		return unwind.entrySet().stream().filter(e -> e.getValue() > 2_000_000).map(Map.Entry::getKey).toList();
	}

	public List<Double> findTrappedPuts(List<IndexOIData> data) {
		Map<Double, Long> unwind = new HashMap<>();

		for (IndexOIData r : data) {
			if (r.getPutChgOI() != null && r.getPutChgOI() < 0) {
				unwind.merge(r.getStrikePrice(), (long) Math.abs(r.getPutChgOI()), Long::sum);
			}
		}

		return unwind.entrySet().stream().filter(e -> e.getValue() > 2_000_000).map(Map.Entry::getKey).toList();
	}

	public String calcVolatility(List<IndexOIData> data) {

		double avgVix = data.stream().filter(x -> x.getIndiaVixLtp() != null).mapToDouble(IndexOIData::getIndiaVixLtp)
				.average().orElse(0);

		if (avgVix > 18)
			return "EXPANDING";
		if (avgVix < 12)
			return "CONTRACTING";
		return "NORMAL";
	}

	public String buildTomorrowPlan(DailyOiSummary d, WeeklyOiSummary w, double close) {

		StringBuilder plan = new StringBuilder();

		plan.append("Key levels: Support ").append(d.support).append(", Resistance ").append(d.resistance).append(". ");

		if ("BULLISH".equals(d.bias)) {
			plan.append("Bias bullish. ").append("Buy near support with SL below it. ")
					.append("Buy breakout above resistance.");
		}

		if ("BEARISH".equals(d.bias)) {
			plan.append("Bias bearish. ").append("Sell near resistance with SL above it. ")
					.append("Sell breakdown below support.");
		}

		if ("RANGE".equals(d.bias)) {
			plan.append("Bias range. ").append("Trade rejection at support/resistance only. ").append("Avoid middle.");
		}

		if (w != null && d.weeklySupportBroken) {
			plan.append(" Weekly support already broken → downside risk higher.");
		}

		return plan.toString();
	}

}
