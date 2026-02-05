package com.fyers.fyerstrading.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ReversalDetector {

	public List<String> detectReversal(Map<LocalDateTime, String> tf3Signals, int confirmSlots) {

		List<Map.Entry<LocalDateTime, String>> list = tf3Signals.entrySet().stream().sorted(Map.Entry.comparingByKey())
				.toList();

		List<String> events = new ArrayList<>();

		String lastSide = null;
		int sameCount = 0;

		for (var e : list) {
			String side = mapToSide(e.getValue());

			if (side == null)
				continue;

			if (lastSide == null || side.equals(lastSide)) {
				sameCount++;
				lastSide = side;
			} else {
				// Opposite appeared
				sameCount = 1;
				lastSide = side;
			}

			if (sameCount == confirmSlots) {
				events.add(e.getKey() + " → REVERSAL TO " + side);
			}
		}
		return events;
	}

	private String mapToSide(String signal) {
		if (signal.equals("CALL_ACCUMULATION") || signal.equals("PUT_WRITING"))
			return "BULL";
		if (signal.equals("PUT_ACCUMULATION") || signal.equals("CALL_WRITING"))
			return "BEAR";
		return null;
	}
}
