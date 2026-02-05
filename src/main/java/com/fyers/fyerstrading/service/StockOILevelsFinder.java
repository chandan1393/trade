package com.fyers.fyerstrading.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.StockOIData;
import com.fyers.fyerstrading.model.StockOiLevels;

@Service
public class StockOILevelsFinder {

	public StockOiLevels findStockOiLevels(List<StockOIData> current, List<StockOIData> previous) {

		Map<Double, StockOIData> prevMap = previous.stream().collect(Collectors.toMap(StockOIData::getStrikePrice, x -> x));

		Map<Double, Long> putWriting = new HashMap<>();
		Map<Double, Long> callWriting = new HashMap<>();

		for (StockOIData c : current) {
			StockOIData p = prevMap.get(c.getStrikePrice());
			if (p == null)
				continue;

			// Put Writing = OI up + price down
			if (c.getPutChgOI() != null && c.getPutChgOI() > 0 && c.getPutLTP() != null && p.getPutLTP() != null
					&& c.getPutLTP() < p.getPutLTP()) {

				putWriting.merge(c.getStrikePrice(), c.getPutChgOI().longValue(), Long::sum);
			}

			// Call Writing = OI up + price down
			if (c.getCallChgOI() != null && c.getCallChgOI() > 0 && c.getCallLTP() != null && p.getCallLTP() != null
					&& c.getCallLTP() < p.getCallLTP()) {

				callWriting.merge(c.getStrikePrice(), c.getCallChgOI().longValue(), Long::sum);
			}
		}

		StockOiLevels lvl = new StockOiLevels();

		lvl.setSupport(
				putWriting.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(0.0));

		lvl.setResistance(callWriting.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
				.orElse(0.0));

		return lvl;
	}

}
