package com.fyers.fyerstrading.strategy;

import java.util.Optional;

import com.fyers.fyerstrading.model.TradeMasterSignal;

public class IntradayNiftyStrategy {

	/*
	 * private final CandleFetcher priceService; private final IndexOIService
	 * oiDataService;
	 * 
	 * public IntradayNiftyStrategy(CandleFetcher priceService, IndexOIService
	 * oiDataService) { this.priceService = priceService; this.oiDataService =
	 * oiDataService; }
	 * 
	 * @Override public Optional<TradeMasterSignal> run(String symbol) { LocalTime
	 * now = LocalTime.now(ZoneId.of("Asia/Kolkata")); if
	 * (now.isBefore(LocalTime.of(9, 20)) || now.isAfter(LocalTime.of(15, 15)))
	 * return Optional.empty();
	 * 
	 * // Last 30 candles List<Nifty5MinCandle> candles =
	 * priceService.getLastNCandlesNifty5Min(30); if (candles == null ||
	 * candles.size() < 10) return Optional.empty();
	 * 
	 * Nifty5MinCandle last = candles.get(0); Nifty5MinCandle prev = candles.get(1);
	 * 
	 * // VWAP replacement using HLC3 average double vwap =
	 * computePseudoVWAP(candles);
	 * 
	 * // Opening Range High/Low (first 3 candles = 15 mins) double orHigh =
	 * candles.subList(candles.size() - 30, candles.size() - 27)
	 * .stream().mapToDouble(Nifty5MinCandle::getHigh).max().orElse(0); double orLow
	 * = candles.subList(candles.size() - 30, candles.size() - 27)
	 * .stream().mapToDouble(Nifty5MinCandle::getLow).min().orElse(0);
	 * 
	 * // Trend filters boolean upTrend = prev.getEma9() <= prev.getEma21() &&
	 * last.getEma9() > last.getEma21() && last.getClose() > vwap;
	 * 
	 * boolean downTrend = prev.getEma9() >= prev.getEma21() && last.getEma9() <
	 * last.getEma21() && last.getClose() < vwap;
	 * 
	 * double ltp = priceService.getLTP(symbol); int atm = (int)
	 * oiDataService.getATMStrike(ltp, true);
	 * 
	 * // LONG Setup if (upTrend && last.getClose() > orHigh) { OIInfo oi =
	 * oiDataService.getOiForStrike(symbol, atm,true).orElse(null); if (oi == null
	 * || oi.getCallOI() == null || oi.getPutOI() == null) return Optional.empty();
	 * if (oi.getCallOI() <= oi.getPutOI()) return Optional.empty(); // require call
	 * OI > put OI
	 * 
	 * return Optional.of(buildSignal(symbol, TradeMasterSignal.Direction.LONG, atm
	 * + "CE", oi.getCallLtp(), ltp,
	 * "EMA crossover + VWAP above + ORH breakout + Call OI rising")); }
	 * 
	 * // SHORT Setup if (downTrend && last.getClose() < orLow) { OIInfo oi =
	 * oiDataService.getOiForStrike(symbol, atm,false).orElse(null); if (oi == null
	 * || oi.getCallOI() == null || oi.getPutOI() == null) return Optional.empty();
	 * if (oi.getPutOI() <= oi.getCallOI()) return Optional.empty(); // require put
	 * OI > call OI
	 * 
	 * return Optional.of(buildSignal(symbol, TradeMasterSignal.Direction.SHORT, atm
	 * + "PE", oi.getPutLtp(), ltp,
	 * "EMA crossover + VWAP below + ORL breakdown + Put OI rising")); }
	 * 
	 * return Optional.empty(); }
	 * 
	 *//**
		 * VWAP replacement without volume → use typical price average of last N
		 * candles.
		 *//*
			 * private double computePseudoVWAP(List<Nifty5MinCandle> candles) { return
			 * candles.stream() .mapToDouble(c -> (c.getHigh() + c.getLow() + c.getClose())
			 * / 3.0) .average() .orElse(0.0); }
			 * 
			 * private TradeMasterSignal buildSignal(String symbol,
			 * TradeMasterSignal.Direction dir, String optSym, double optLtp, double spot,
			 * String reason) { TradeMasterSignal s = new TradeMasterSignal(); s.symbol =
			 * symbol; s.direction = dir; s.optionSymbol = "AUTO:" + optSym;
			 * s.optionEntryPrice = optLtp; s.optionSL = optLtp * 0.5; s.optionTarget =
			 * optLtp * 1.8; s.qty = 1; s.underlyingAtSignal = spot; s.reason = reason;
			 * s.timestamp = LocalDateTime.now(); return s; }
			 */
}
