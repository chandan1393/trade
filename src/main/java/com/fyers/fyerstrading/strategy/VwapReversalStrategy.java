package com.fyers.fyerstrading.strategy;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.fyers.fyerstrading.entity.FNO5MinCandle;
import com.fyers.fyerstrading.model.TradeMasterSignal;
import com.fyers.fyerstrading.service.common.CandleFetcher;

public class VwapReversalStrategy  {

	/*
	 * private final CandleFetcher priceService; private final OptionChainService
	 * oiDataService;
	 * 
	 * public VwapReversalStrategy(CandleFetcher priceService, OptionChainService
	 * oiDataService) { this.priceService = priceService; this.oiDataService =
	 * oiDataService; }
	 * 
	 * @Override public Optional<TradeMasterSignal> run(String symbol) { LocalTime
	 * now = LocalTime.now(); if (now.isBefore(LocalTime.of(9, 20)) ||
	 * now.isAfter(LocalTime.of(15, 20))) { return Optional.empty(); }
	 * 
	 * // fetch last 30 candles (VWAP requires history) List<FNO5MinCandle> candles
	 * = priceService.getLastNCandlesStocks5Min(symbol, 30); if (candles == null ||
	 * candles.size() < 6) return Optional.empty();
	 * 
	 * double vwap = computeVWAP(candles); FNO5MinCandle last = candles.get(0);
	 * 
	 * // average volume check double avgVol =
	 * candles.stream().mapToDouble(FNO5MinCandle::getVolume).average().orElse(0);
	 * 
	 * // Bounce up from VWAP if (last.getLow() <= vwap && last.getClose() > vwap &&
	 * last.getVolume() >= avgVol * 1.2) { double ltp = priceService.getLTP(symbol);
	 * int atm = oiDataService.getATMStrike(ltp, false);
	 * 
	 * var oi = oiDataService.getOiForStrike(symbol,atm,true).orElse(null); if (oi
	 * == null) return Optional.empty();
	 * 
	 * double optionLtp = oi.getCallLtp();
	 * 
	 * double optionSL = Math.max(0.1, optionLtp * 0.5); double optionTarget =
	 * optionLtp * 1.8;
	 * 
	 * TradeMasterSignal sgn = new TradeMasterSignal(); sgn.symbol = symbol;
	 * sgn.direction = TradeMasterSignal.Direction.LONG; sgn.optionSymbol = "AUTO:"
	 * + atm + "CE"; sgn.optionEntryPrice = optionLtp; sgn.optionSL = optionSL;
	 * sgn.optionTarget = optionTarget; sgn.qty = 1; sgn.underlyingAtSignal = ltp;
	 * sgn.reason = "VWAP Bounce Reversal"; sgn.timestamp = LocalDateTime.now();
	 * return Optional.of(sgn); }
	 * 
	 * // Rejection from VWAP (short) if (last.getHigh() >= vwap && last.getClose()
	 * < vwap && last.getVolume() >= avgVol * 1.2) { double ltp =
	 * priceService.getLTP(symbol); int atm = oiDataService.getATMStrike(ltp,
	 * false);
	 * 
	 * var oi = oiDataService.getOiForStrike(symbol,atm,false).orElse(null); if (oi
	 * == null) return Optional.empty();
	 * 
	 * double optionLtp = oi.getPutLtp();
	 * 
	 * double optionSL = Math.max(0.1, optionLtp * 0.5); double optionTarget =
	 * optionLtp * 1.8;
	 * 
	 * TradeMasterSignal sgn = new TradeMasterSignal(); sgn.symbol = symbol;
	 * sgn.direction = TradeMasterSignal.Direction.SHORT; sgn.optionSymbol = "AUTO:"
	 * + atm + "PE"; sgn.optionEntryPrice = optionLtp; sgn.optionSL = optionSL;
	 * sgn.optionTarget = optionTarget; sgn.qty = 1; sgn.underlyingAtSignal = ltp;
	 * sgn.reason = "VWAP Rejection Reversal"; sgn.timestamp = LocalDateTime.now();
	 * return Optional.of(sgn); }
	 * 
	 * return Optional.empty(); }
	 * 
	 * private double computeVWAP(List<FNO5MinCandle> candles) { double pv = 0, vol
	 * = 0; for (FNO5MinCandle c : candles) { double typical = (c.getHigh() +
	 * c.getLow() + c.getClose()) / 3.0; pv += typical * c.getVolume(); vol +=
	 * c.getVolume(); } return vol == 0 ? 0 : pv / vol; }
	 */
}
