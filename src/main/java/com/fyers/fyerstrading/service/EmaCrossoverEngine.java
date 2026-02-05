package com.fyers.fyerstrading.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fyers.fyerstrading.model.Candle;
import com.fyers.fyerstrading.utility.ExpiryUtils;

@Service
public class EmaCrossoverEngine {

	/*
	 * private static final int EMA_SHORT_PERIOD = 9; private static final int
	 * EMA_LONG_PERIOD = 21;
	 * 
	 * private final List<Double> closingPrices = new ArrayList<>(); private boolean
	 * inTrade = false;
	 * 
	 * @Autowired private FyersOptionChainService optionChainService;
	 * 
	 * @Autowired private OptionStrikeSelector strikeSelector;
	 * 
	 * @Autowired private OptionTradeExecutionService tradeService;
	 * 
	 * public void onNiftyPriceTick(double niftySpot) {
	 * closingPrices.add(niftySpot);
	 * 
	 * if (closingPrices.size() < EMA_LONG_PERIOD) return;
	 * 
	 * double ema9 = calculateEMA(closingPrices, EMA_SHORT_PERIOD); double ema21 =
	 * calculateEMA(closingPrices, EMA_LONG_PERIOD);
	 * 
	 * if (!inTrade && ema9 > ema21) { placeOptionTrade(niftySpot, "bullish");
	 * inTrade = true; } else if (!inTrade && ema9 < ema21) {
	 * placeOptionTrade(niftySpot, "bearish"); inTrade = true; }
	 * 
	 * // Optional: add logic to exit after N candles or crossover reversal }
	 * 
	 * private void placeOptionTrade(double spotPrice, String direction) { String
	 * expiryPrefix = ExpiryUtils.getExpiryPrefix(); List<OIData> oiDataList =
	 * optionChainService.fetchOptionChain("NSE:NIFTY50-INDEX", 10);
	 * 
	 * String optionSymbol = strikeSelector.selectStrikeSymbolFromOI(spotPrice,
	 * direction, expiryPrefix, oiDataList); if (optionSymbol == null) return;
	 * 
	 * double optionLtp = getOptionLtpFromOiData(optionSymbol, oiDataList); if
	 * (optionLtp == 0) return;
	 * 
	 * double sl = optionLtp - 20; double target = optionLtp + 40;
	 * 
	 * if (!tradeService.hasOpenTrade(optionSymbol)) {
	 * tradeService.openTrade(optionSymbol, "BUY", optionLtp, sl, target); } }
	 * 
	 * private double calculateEMA(List<Double> prices, int period) { double
	 * multiplier = 2.0 / (period + 1); double ema = prices.get(prices.size() -
	 * period); // Start with SMA seed for (int i = prices.size() - period + 1; i <
	 * prices.size(); i++) { ema = ((prices.get(i) - ema) * multiplier) + ema; }
	 * return ema; }
	 * 
	 * private double getOptionLtpFromOiData(String optionSymbol, List<OIData>
	 * oiDataList) { try { int strike =
	 * Integer.parseInt(optionSymbol.replaceAll("[^0-9]", "")); boolean isCall =
	 * optionSymbol.endsWith("CE");
	 * 
	 * return oiDataList.stream() .filter(oi -> oi.getStrikePrice().intValue() ==
	 * strike) .map(oi -> isCall ? oi.getCallLTP() : oi.getPutLTP()) .findFirst()
	 * .orElse(0.0); } catch (Exception e) {
	 * System.err.println("Error parsing LTP from symbol: " + optionSymbol); return
	 * 0.0; } }
	 * 
	 * 
	 * 
	 * public void onCandleClose(Candle candle) { double close = candle.getClose();
	 * closingPrices.add(close);
	 * 
	 * if (closingPrices.size() < 21) return;
	 * 
	 * double ema9 = calculateEMA(closingPrices, 9); double ema21 =
	 * calculateEMA(closingPrices, 21);
	 * 
	 * double prevEma9 = calculateEMA(closingPrices.subList(0, closingPrices.size()
	 * - 1), 9); double prevEma21 = calculateEMA(closingPrices.subList(0,
	 * closingPrices.size() - 1), 21);
	 * 
	 * if (!inTrade && prevEma9 < prevEma21 && ema9 > ema21) {
	 * placeOptionTrade(close, "bullish"); } else if (!inTrade && prevEma9 >
	 * prevEma21 && ema9 < ema21) { placeOptionTrade(close, "bearish"); } }
	 */
}

