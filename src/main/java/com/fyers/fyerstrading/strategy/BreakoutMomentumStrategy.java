package com.fyers.fyerstrading.strategy;

public class BreakoutMomentumStrategy {

	/*
	 * private final CandleFetcher priceService; private final StockOIService
	 * oiDataService; private final OrderService orderService;
	 * 
	 * // configuration private final int breakoutLookback = 3; // last 3 candles ->
	 * range private final double minVolumeMultiplier = 1.5; // breakout volume >
	 * avg*multiplier
	 * 
	 * public BreakoutMomentumStrategy(CandleFetcher priceService, StockOIService
	 * oiDataService, OrderService orderService) { this.priceService = priceService;
	 * this.oiDataService = oiDataService; this.orderService = orderService; }
	 * 
	 * @Override public Optional<TradeMasterSignal> run(String symbol) { LocalTime
	 * now = LocalTime.now(); if (now.isBefore(LocalTime.of(9, 20)) ||
	 * now.isAfter(LocalTime.of(15, 20))) { return Optional.empty(); }
	 * 
	 * // Fetch last N candles List<FNO5MinCandle> candles =
	 * priceService.getLastNCandlesStocks5Min(symbol, breakoutLookback + 1); if
	 * (candles == null || candles.size() < breakoutLookback + 1) return
	 * Optional.empty();
	 * 
	 * // Compute range using previous breakoutLookback candles excluding latest
	 * List<FNO5MinCandle> rangeCandles = candles.subList(1, 1 + breakoutLookback);
	 * double high =
	 * rangeCandles.stream().mapToDouble(FNO5MinCandle::getHigh).max().orElse(Double
	 * .NaN); double low =
	 * rangeCandles.stream().mapToDouble(FNO5MinCandle::getLow).min().orElse(Double.
	 * NaN); FNO5MinCandle last = candles.get(0);
	 * 
	 * // Compute average volume double avgVol =
	 * rangeCandles.stream().mapToDouble(FNO5MinCandle::getVolume).average().orElse(
	 * 0); if (last.getVolume() < avgVol * minVolumeMultiplier) return
	 * Optional.empty();
	 * 
	 * // Current underlying LTP double ltp = priceService.getLTP(symbol);
	 * 
	 * // Compute ATM strike for F&O stock (use false for isIndex) int atm = (int)
	 * oiDataService.getATMStrike(ltp, false);
	 * 
	 * // Breakout up if (last.getClose() > high) { var oi =
	 * oiDataService.getOiForStrike(symbol, atm, true).orElse(null); if (oi == null)
	 * return Optional.empty();
	 * 
	 * double optionLtp = oi.getCallLtp(); double delta = 0.5; // fallback delta, or
	 * fetch from service if available double niftySL = last.getClose() - low;
	 * double optionSL = Math.max(0.1, optionLtp - niftySL * delta); double
	 * niftyTarget = last.getClose() + (last.getClose() - low); double optionTarget
	 * = optionLtp + (niftyTarget - last.getClose()) * delta;
	 * 
	 * TradeMasterSignal signal = new TradeMasterSignal(); signal.symbol = symbol;
	 * signal.direction = TradeMasterSignal.Direction.LONG;
	 * signal.underlyingAtSignal = ltp; signal.optionSymbol = "AUTO:" + atm + "CE";
	 * signal.optionEntryPrice = optionLtp; signal.optionSL = optionSL;
	 * signal.optionTarget = optionTarget; signal.qty = 1; signal.reason =
	 * "BreakoutMomentum: breakout above " + high; signal.timestamp =
	 * LocalDateTime.now(); return Optional.of(signal); }
	 * 
	 * // Breakout down if (last.getClose() < low) { var oi =
	 * oiDataService.getOiForStrike(symbol, atm, false).orElse(null); if (oi ==
	 * null) return Optional.empty();
	 * 
	 * double optionLtp = oi.getPutLtp(); double delta = 0.5; double niftySL = high
	 * - last.getClose(); double optionSL = Math.max(0.1, optionLtp - niftySL *
	 * delta); double niftyTarget = last.getClose() - (high - last.getClose());
	 * double optionTarget = optionLtp + (last.getClose() - niftyTarget) * delta;
	 * 
	 * TradeMasterSignal signal = new TradeMasterSignal(); signal.symbol = symbol;
	 * signal.direction = TradeMasterSignal.Direction.SHORT;
	 * signal.underlyingAtSignal = ltp; signal.optionSymbol = "AUTO:" + atm + "PE";
	 * signal.optionEntryPrice = optionLtp; signal.optionSL = optionSL;
	 * signal.optionTarget = optionTarget; signal.qty = 1; signal.reason =
	 * "BreakoutMomentum: breakdown below " + low; signal.timestamp =
	 * LocalDateTime.now(); return Optional.of(signal); }
	 * 
	 * return Optional.empty(); }
	 */
}
