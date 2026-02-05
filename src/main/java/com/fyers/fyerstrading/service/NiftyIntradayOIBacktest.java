package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.entity.IndexOIData;
import com.fyers.fyerstrading.model.IntradayReport;

@Component
public class NiftyIntradayOIBacktest {

	@Autowired
	private IndexOIAnalyzer analyzer;

	public List<IntradayReport> backtest3Min(Map<LocalDateTime, List<IndexOIData>> oiByTime,
			Map<LocalDateTime, Double> niftyMoveByTime) {

		Map<LocalDate, List<LocalDateTime>> byDate = new TreeMap<>();

		for (LocalDateTime t : oiByTime.keySet()) {
			byDate.computeIfAbsent(t.toLocalDate(), k -> new ArrayList<>()).add(t);
		}

		List<IntradayReport> reports = new ArrayList<>();

		for (var dayEntry : byDate.entrySet()) {
			LocalDate date = dayEntry.getKey();
			List<LocalDateTime> times = dayEntry.getValue();
			times.sort(Comparator.naturalOrder());

			if (times.size() < 2)
				continue;

			IntradayReport r = new IntradayReport();
			r.date = date;

			double open = niftyMoveByTime.getOrDefault(times.get(0), 0.0);
			r.openMove = open;
			r.maxUp = open;
			r.maxDown = open;

			String lastSide = null;

			for (int i = 1; i < times.size(); i++) {

				LocalDateTime t = times.get(i);
				LocalDateTime prevTime = times.get(i - 1);

				double move = niftyMoveByTime.getOrDefault(t, moveAt(open, niftyMoveByTime, prevTime));
				r.maxUp = Math.max(r.maxUp, move);
				r.maxDown = Math.min(r.maxDown, move);

				List<IndexOIData> current = oiByTime.get(t);
				List<IndexOIData> previous = oiByTime.get(prevTime);

				if (current == null || previous == null)
					continue;

				String signal = analyzer.analyze(current, previous);
				String side = mapSide(signal);

				r.timeline.add(t.toLocalTime() + " → " + move + " → " + signal);

				if ("BULL".equals(side))
					r.bullishSlots++;
				if ("BEAR".equals(side))
					r.bearishSlots++;

				if (side != null) {
					if (lastSide == null) {
						lastSide = side;
					} else if (!side.equals(lastSide)) {
						r.reversals++;
						lastSide = side;
					}
				}
			}

			r.closeMove = niftyMoveByTime.getOrDefault(times.get(times.size() - 1), r.maxDown);
			reports.add(r);
		}

		return reports;
	}

	private String mapSide(String s) {
		if (s == null)
			return null;
		if (s.equals("CALL_ACCUMULATION") || s.equals("PUT_WRITING"))
			return "BULL";
		if (s.equals("PUT_ACCUMULATION") || s.equals("CALL_WRITING"))
			return "BEAR";
		return null;
	}

	private double moveAt(double open, Map<LocalDateTime, Double> map, LocalDateTime fallback) {
		return map.getOrDefault(fallback, open);
	}
}
