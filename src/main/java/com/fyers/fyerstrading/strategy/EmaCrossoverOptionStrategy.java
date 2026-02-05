/*
 * package com.fyers.fyerstrading.strategy;
 * 
 * import java.time.LocalDateTime; import java.time.LocalTime; import
 * java.util.Arrays; import java.util.List; import java.util.Optional;
 * 
 * import com.fyers.fyerstrading.entity.FNO5MinCandle; import
 * com.fyers.fyerstrading.model.TradeMasterSignal; import
 * com.fyers.fyerstrading.service.OIDataService; import
 * com.fyers.fyerstrading.service.OrderService; import
 * com.fyers.fyerstrading.service.PriceService;
 * 
 * public class EmaCrossoverOptionStrategy implements Strategy {
 * 
 * private final PriceService priceService; private final OIDataService
 * oiDataService; private final OrderService orderService;
 * 
 * // config private final int baseLookback = 5; private final double
 * baseTolerancePercent = 0.2;
 * 
 * public EmaCrossoverOptionStrategy(PriceService priceService, OIDataService
 * oiDataService, OrderService orderService) { this.priceService = priceService;
 * this.oiDataService = oiDataService; this.orderService = orderService; }
 * 
 * @Override public Optional<TradeMasterSignal> run(String symbol) { LocalTime
 * now = LocalTime.now(); if (now.isBefore(LocalTime.of(9, 20)) ||
 * now.isAfter(LocalTime.of(15, 20))) { return Optional.empty(); }
 * 
 * // get last 2 candles List<FNO5MinCandle> lastTwo =
 * priceService.getLastNCandles(symbol, 2); if (lastTwo == null ||
 * lastTwo.size() < 2) return Optional.empty();
 * 
 * FNO5MinCandle prev = lastTwo.get(1); FNO5MinCandle curr = lastTwo.get(0);
 * 
 * if (prev.getEma9() == null || prev.getEma21() == null || curr.getEma9() ==
 * null || curr.getEma21() == null) { return Optional.empty(); }
 * 
 * boolean longCross = prev.getEma9() <= prev.getEma21() && curr.getEma9() >
 * curr.getEma21(); boolean shortCross = prev.getEma9() >= prev.getEma21() &&
 * curr.getEma9() < curr.getEma21(); if (!longCross && !shortCross) return
 * Optional.empty();
 * 
 * // detect base List<FNO5MinCandle> baseCandles =
 * priceService.getLastNCandles(symbol, baseLookback + 1); if (baseCandles ==
 * null || baseCandles.size() < baseLookback + 1) return Optional.empty();
 * 
 * double maxHigh = baseCandles.subList(1, 1 + baseLookback).stream()
 * .mapToDouble(FNO5MinCandle::getHigh).max().orElse(Double.NaN); double minLow
 * = baseCandles.subList(1, 1 + baseLookback).stream()
 * .mapToDouble(FNO5MinCandle::getLow).min().orElse(Double.NaN);
 * 
 * double mid = (maxHigh + minLow) / 2.0; double tol = mid *
 * (baseTolerancePercent / 100.0); boolean isBase = (maxHigh - minLow) <= tol;
 * if (!isBase) return Optional.empty();
 * 
 * double ltp = priceService.getUnderlyingLTP(symbol); int atm = (int)
 * oiDataService.getATMStrike(ltp, false); // F&O stock
 * 
 * // compute average OI across ATM ±2 strikes List<Integer> strikes =
 * Arrays.asList(atm - 100, atm - 50, atm, atm + 50, atm + 100); double
 * avgCallOi = 0, avgPutOi = 0; int count = 0;
 * 
 * for (int s : strikes) { var oi = oiDataService.getOiForStrike(symbol, s,
 * true).orElse(null); var oiPut = oiDataService.getOiForStrike(symbol, s,
 * false).orElse(null); if (oi == null || oiPut == null) continue;
 * 
 * avgCallOi += oi.getCallLTP(); // or use callOI if needed avgPutOi +=
 * oiPut.getPutLTP(); // or use putOI if needed count++; } if (count == 0)
 * return Optional.empty(); avgCallOi /= count; avgPutOi /= count;
 * 
 * boolean bullishOI = avgPutOi > avgCallOi; boolean bearishOI = avgCallOi >
 * avgPutOi;
 * 
 * if (longCross && bullishOI) { var oi = oiDataService.getOiForStrike(symbol,
 * atm, true).orElse(null); if (oi == null) return Optional.empty();
 * 
 * double optionLtp = oi.getCallLTP(); double delta = 0.5;
 * 
 * double niftySL = curr.getClose() - minLow; double optionSL = Math.max(0.1,
 * optionLtp - niftySL * delta); double niftyTarget = curr.getClose() +
 * (curr.getClose() - minLow); double optionTarget = optionLtp + (niftyTarget -
 * curr.getClose()) * delta;
 * 
 * TradeMasterSignal sgn = new TradeMasterSignal(); sgn.direction =
 * TradeMasterSignal.Direction.LONG; sgn.symbol = symbol; sgn.optionSymbol =
 * "AUTO:" + atm + "CE"; sgn.optionEntryPrice = optionLtp; sgn.optionSL =
 * optionSL; sgn.optionTarget = optionTarget; sgn.qty = 1;
 * sgn.underlyingAtSignal = ltp; sgn.reason =
 * "EMA crossover + base + OI confirm"; sgn.timestamp = LocalDateTime.now();
 * return Optional.of(sgn); }
 * 
 * if (shortCross && bearishOI) { var oi = oiDataService.getOiForStrike(symbol,
 * atm, false).orElse(null); if (oi == null) return Optional.empty();
 * 
 * double optionLtp = oi.getPutLTP(); double delta = 0.5;
 * 
 * double niftySL = maxHigh - curr.getClose(); double optionSL = Math.max(0.1,
 * optionLtp - niftySL * delta); double niftyTarget = curr.getClose() - (maxHigh
 * - curr.getClose()); double optionTarget = optionLtp + (curr.getClose() -
 * niftyTarget) * delta;
 * 
 * TradeMasterSignal sgn = new TradeMasterSignal(); sgn.direction =
 * TradeMasterSignal.Direction.SHORT; sgn.symbol = symbol; sgn.optionSymbol =
 * "AUTO:" + atm + "PE"; sgn.optionEntryPrice = optionLtp; sgn.optionSL =
 * optionSL; sgn.optionTarget = optionTarget; sgn.qty = 1;
 * sgn.underlyingAtSignal = ltp; sgn.reason =
 * "EMA crossover + base + OI confirm"; sgn.timestamp = LocalDateTime.now();
 * return Optional.of(sgn); }
 * 
 * return Optional.empty(); } }
 */