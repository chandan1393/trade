package com.fyers.fyerstrading.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OptionTickRouter {

	@Autowired
	private StockOptionLogicEngine stockLogicEngine;

	@Autowired
	private IndexOptionLogicEngine indexLogicEngine;

	public void processLiveTick(String symbol, double ltp) {

		boolean processed = false;

		// 1️⃣ Stock trades
		List<Long> stockTradeIds = stockLogicEngine.getTradeIdsBySymbol(symbol);
		if (stockTradeIds != null && !stockTradeIds.isEmpty()) {
			stockLogicEngine.processLiveTick(symbol, ltp);
			processed = true;
		}

		// 2️⃣ Index trades
		List<Long> indexTradeIds = indexLogicEngine.getTradeIdsBySymbol(symbol);
		if (indexTradeIds != null && !indexTradeIds.isEmpty()) {
			indexLogicEngine.processLiveTick(symbol, ltp);
			processed = true;
		}

		// 3️⃣ Optional debug
		if (!processed) {
			// No active trade for this symbol — safe to ignore
			// System.out.println("No active trades for tick: " + symbol);
		}
	}
}
