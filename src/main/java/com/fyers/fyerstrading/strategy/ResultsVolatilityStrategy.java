package com.fyers.fyerstrading.strategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fyers.fyerstrading.entity.FNO5MinCandle;
import com.fyers.fyerstrading.model.TradeMasterSignal;
import com.fyers.fyerstrading.service.common.CandleFetcher;

public class ResultsVolatilityStrategy {

	

	/*
	 * private final CandleFetcher priceService; private final OptionChainService
	 * oiDataService; private final Set<String> resultStocks;
	 * 
	 * public ResultsVolatilityStrategy(CandleFetcher priceService,
	 * OptionChainService oiDataService, Set<String> resultStocks) {
	 * this.priceService = priceService; this.oiDataService = oiDataService;
	 * this.resultStocks = resultStocks; }
	 * 
	 * @Override public Optional<TradeMasterSignal> run(String symbol) { if
	 * (!resultStocks.contains(symbol)) return Optional.empty();
	 * 
	 * LocalTime now = LocalTime.now(); if (now.isBefore(LocalTime.of(9, 45)) ||
	 * now.isAfter(LocalTime.of(15, 20))) return Optional.empty();
	 * 
	 * // first 6 candles (9:15–9:45) List<FNO5MinCandle> candles =
	 * priceService.get5MinStockCandlesBetween(symbol,LocalDate.now(),
	 * LocalTime.of(9, 15), LocalTime.of(9, 45)); if (candles.size() < 6) return
	 * Optional.empty();
	 * 
	 * double openingRangeHigh =
	 * candles.stream().mapToDouble(FNO5MinCandle::getHigh).max().orElse(Double.NaN)
	 * ; double openingRangeLow =
	 * candles.stream().mapToDouble(FNO5MinCandle::getLow).min().orElse(Double.NaN);
	 * 
	 * double ltp = priceService.getLTP(symbol); int atm = (int)
	 * oiDataService.getATMStrike(ltp, false);
	 * 
	 * if (ltp > openingRangeHigh) { // Bullish breakout var oi =
	 * oiDataService.getOiForStrike(symbol, atm, true).orElse(null); if (oi == null)
	 * return Optional.empty();
	 * 
	 * double optionLtp = oi.getCallLtp(); double optionSL = Math.max(0.1, optionLtp
	 * * 0.7); double optionTarget = optionLtp * 1.9;
	 * 
	 * TradeMasterSignal s = new TradeMasterSignal(); s.symbol = symbol;
	 * s.optionSymbol = "AUTO:" + atm + "CE"; s.direction =
	 * TradeMasterSignal.Direction.LONG; s.optionEntryPrice = optionLtp; s.optionSL
	 * = optionSL; s.optionTarget = optionTarget; s.qty = 1; s.reason =
	 * "Opening Range Breakout UP"; s.timestamp = LocalDateTime.now(); return
	 * Optional.of(s);
	 * 
	 * } else if (ltp < openingRangeLow) { // Bearish breakout var oi =
	 * oiDataService.getOiForStrike(symbol, atm, false).orElse(null); if (oi ==
	 * null) return Optional.empty();
	 * 
	 * double optionLtp = oi.getPutLtp(); double optionSL = Math.max(0.1, optionLtp
	 * * 0.7); double optionTarget = optionLtp * 1.9;
	 * 
	 * TradeMasterSignal s = new TradeMasterSignal(); s.symbol = symbol;
	 * s.optionSymbol = "AUTO:" + atm + "PE"; s.direction =
	 * TradeMasterSignal.Direction.SHORT; s.optionEntryPrice = optionLtp; s.optionSL
	 * = optionSL; s.optionTarget = optionTarget; s.qty = 1; s.reason =
	 * "Opening Range Breakout DOWN"; s.timestamp = LocalDateTime.now(); return
	 * Optional.of(s); }
	 * 
	 * return Optional.empty(); }
	 */
}
