package com.fyers.fyerstrading.strategy;


import java.util.Optional;

import com.fyers.fyerstrading.model.TradeMasterSignal;

public class OiShiftOptionStrategy {

	/*
	 * private final CandleFetcher priceService; private final OptionChainService
	 * oiDataService;
	 * 
	 * public OiShiftOptionStrategy(CandleFetcher priceService, OptionChainService
	 * oiDataService) { this.priceService = priceService; this.oiDataService =
	 * oiDataService; }
	 * 
	 * @Override public Optional<TradeMasterSignal> run(String symbol) { LocalTime
	 * now = LocalTime.now(); if (now.isBefore(LocalTime.of(9, 20)) ||
	 * now.isAfter(LocalTime.of(15, 20))) return Optional.empty();
	 * 
	 * double ltp = priceService.getLTP(symbol); int atm = (int)
	 * oiDataService.getATMStrike(ltp, false);
	 * 
	 * // ATM ± 2 strikes (10/50 based on stock) List<Integer> strikes =
	 * Arrays.asList(atm - 100, atm - 50, atm, atm + 50, atm + 100); double
	 * totalCallOi = 0, totalPutOi = 0; int count = 0;
	 * 
	 * for (int s : strikes) { var oiCall = oiDataService.getOiForStrike(symbol, s,
	 * true).orElse(null); var oiPut = oiDataService.getOiForStrike(symbol, s,
	 * false).orElse(null); if (oiCall == null || oiPut == null) continue;
	 * 
	 * totalCallOi += oiCall.getCallOI(); totalPutOi += oiPut.getPutOI(); count++; }
	 * 
	 * if (count == 0) return Optional.empty();
	 * 
	 * double pcr = totalPutOi / Math.max(1.0, totalCallOi); // PCR ratio
	 * 
	 * // Interpret PCR: >1.05 bullish, <0.95 bearish if (pcr > 1.05) { var oi =
	 * oiDataService.getOiForStrike(symbol, atm, true).orElse(null); if (oi == null)
	 * return Optional.empty();
	 * 
	 * double entryPremium = oi.getCallLtp(); double delta = 0.5; double optionSL =
	 * Math.max(0.1, entryPremium * 0.4); double optionTarget = entryPremium * 1.8;
	 * 
	 * TradeMasterSignal signal = new TradeMasterSignal(); signal.symbol = symbol;
	 * signal.optionSymbol = "AUTO:" + atm + "CE"; signal.direction =
	 * TradeMasterSignal.Direction.LONG; signal.optionEntryPrice = entryPremium;
	 * signal.optionSL = optionSL; signal.optionTarget = optionTarget; signal.qty =
	 * 1; signal.underlyingAtSignal = ltp; signal.reason = "OI Shift bullish (PCR="
	 * + String.format("%.2f", pcr) + ")"; signal.timestamp = LocalDateTime.now();
	 * return Optional.of(signal); } else if (pcr < 0.95) { var oi =
	 * oiDataService.getOiForStrike(symbol, atm, false).orElse(null); if (oi ==
	 * null) return Optional.empty();
	 * 
	 * double entryPremium = oi.getPutLtp(); double optionSL = Math.max(0.1,
	 * entryPremium * 0.4); double optionTarget = entryPremium * 1.8;
	 * 
	 * TradeMasterSignal signal = new TradeMasterSignal(); signal.symbol = symbol;
	 * signal.optionSymbol = "AUTO:" + atm + "PE"; signal.direction =
	 * TradeMasterSignal.Direction.SHORT; signal.optionEntryPrice = entryPremium;
	 * signal.optionSL = optionSL; signal.optionTarget = optionTarget; signal.qty =
	 * 1; signal.underlyingAtSignal = ltp; signal.reason = "OI Shift bearish (PCR="
	 * + String.format("%.2f", pcr) + ")"; signal.timestamp = LocalDateTime.now();
	 * return Optional.of(signal); }
	 * 
	 * return Optional.empty(); }
	 */
}
