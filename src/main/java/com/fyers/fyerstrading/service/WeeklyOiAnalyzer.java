package com.fyers.fyerstrading.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.entity.IndexOIData;
import com.fyers.fyerstrading.model.WeeklyLevels;
import com.fyers.fyerstrading.model.WeeklyOiSummary;

@Component
public class WeeklyOiAnalyzer {

	public WeeklyOiSummary analyzeWeek(List<IndexOIData> weekData, double weekHigh, double weekLow,
			WeeklyOiSummary lastWeek) {

		WeeklyOiSummary s = new WeeklyOiSummary();

		// ---------- 1) Support & Resistance ----------
		WeeklyLevels levels = findWeeklyLevels(weekData);
		s.support = levels.supportStrike;
		s.resistance = levels.resistanceStrike;

		// ---------- Prepare 3-min sequence ----------
		Map<LocalDateTime, List<IndexOIData>> byTime = weekData.stream()
				.collect(Collectors.groupingBy(IndexOIData::getTimestamp));

		List<LocalDateTime> times = byTime.keySet().stream().sorted().toList();

		// ---------- 2) Weekly Bias ----------
		long bull = 0, bear = 0;

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

				// Put writing → bullish
				if (c.getPutChgOI() != null && c.getPutChgOI() > 0 && c.getPutLTP() != null && p.getPutLTP() != null
						&& c.getPutLTP() < p.getPutLTP()) {
					bull += c.getPutChgOI();
				}

				// Call writing → bearish
				if (c.getCallChgOI() != null && c.getCallChgOI() > 0 && c.getCallLTP() != null && p.getCallLTP() != null
						&& c.getCallLTP() < p.getCallLTP()) {
					bear += c.getCallChgOI();
				}
			}
		}

		if (bull > bear * 1.2)
			s.bias = "BULLISH";
		else if (bear > bull * 1.2)
			s.bias = "BEARISH";
		else
			s.bias = "RANGE";

		// ---------- 3) Break or Hold ----------
		if (lastWeek != null) {
			s.supportBroken = weekLow < lastWeek.support;
			s.resistanceBroken = weekHigh > lastWeek.resistance;
		}

		// ---------- 4) Trapped Sellers ----------
		Map<Double, Long> callUnwind = new HashMap<>();
		Map<Double, Long> putUnwind = new HashMap<>();

		for (IndexOIData r : weekData) {
			if (r.getCallChgOI() != null && r.getCallChgOI() < 0)
				callUnwind.merge(r.getStrikePrice(), (long) Math.abs(r.getCallChgOI()), Long::sum);

			if (r.getPutChgOI() != null && r.getPutChgOI() < 0)
				putUnwind.merge(r.getStrikePrice(), (long) Math.abs(r.getPutChgOI()), Long::sum);
		}

		long th = 5_000_000;
		callUnwind.forEach((k, v) -> {
			if (v > th)
				s.trappedCallStrikes.add(k);
		});
		putUnwind.forEach((k, v) -> {
			if (v > th)
				s.trappedPutStrikes.add(k);
		});

		// ---------- 5) Volatility ----------
		double avgVix = weekData.stream().filter(x -> x.getIndiaVixLtp() != null).mapToDouble(IndexOIData::getIndiaVixLtp)
				.average().orElse(0);

		if (avgVix > 18)
			s.volatility = "EXPANDING";
		else if (avgVix < 12)
			s.volatility = "CONTRACTING";
		else
			s.volatility = "NORMAL";

		return s;
	}

	// ---------- Weekly Support & Resistance ----------
	public WeeklyLevels findWeeklyLevels(List<IndexOIData> weekData) {

		Map<LocalDateTime, List<IndexOIData>> byTime = weekData.stream()
				.collect(Collectors.groupingBy(IndexOIData::getTimestamp));

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

				// Put writing = support
				if (c.getPutChgOI() != null && c.getPutChgOI() > 0 && c.getPutLTP() != null && p.getPutLTP() != null
						&& c.getPutLTP() < p.getPutLTP()) {
					putWriting.merge(c.getStrikePrice(), c.getPutChgOI().longValue(), Long::sum);
				}

				// Call writing = resistance
				if (c.getCallChgOI() != null && c.getCallChgOI() > 0 && c.getCallLTP() != null && p.getCallLTP() != null
						&& c.getCallLTP() < p.getCallLTP()) {
					callWriting.merge(c.getStrikePrice(), c.getCallChgOI().longValue(), Long::sum);
				}
			}
		}

		WeeklyLevels lvl = new WeeklyLevels();
		lvl.supportStrike = putWriting.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
				.orElse(0.0);

		lvl.resistanceStrike = callWriting.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
				.orElse(0.0);

		return lvl;
	}
}
