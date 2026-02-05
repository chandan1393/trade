package com.fyers.fyerstrading.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.entity.IndexOIData;

@Component
public class IndexOIAnalyzer {

	public String analyze(List<IndexOIData> current, List<IndexOIData> previous) {

		Map<Double, IndexOIData> prevMap = previous.stream().collect(Collectors.toMap(IndexOIData::getStrikePrice, x -> x));

		long callBuy = 0, callWrite = 0;
		long putBuy = 0, putWrite = 0;

		for (IndexOIData cur : current) {
			IndexOIData prev = prevMap.get(cur.getStrikePrice());
			if (prev == null)
				continue;

			// CALL side
			if (cur.getCallChgOI() != null) {
				if (cur.getCallChgOI() > 0) {
					if (cur.getCallLTP() > prev.getCallLTP())
						callBuy += cur.getCallChgOI(); // Price ↑ + OI ↑
					else
						callWrite += cur.getCallChgOI(); // Price ↓ + OI ↑
				}
			}

			// PUT side
			if (cur.getPutChgOI() != null) {
				if (cur.getPutChgOI() > 0) {
					if (cur.getPutLTP() > prev.getPutLTP())
						putBuy += cur.getPutChgOI();
					else
						putWrite += cur.getPutChgOI();
				}
			}
		}

		if (putBuy > callBuy && putBuy > putWrite)
			return "PUT_ACCUMULATION";
		if (callBuy > putBuy && callBuy > callWrite)
			return "CALL_ACCUMULATION";
		if (putWrite > callWrite)
			return "PUT_WRITING";
		if (callWrite > putWrite)
			return "CALL_WRITING";

		return "NEUTRAL";
	}
}
