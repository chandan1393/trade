package com.fyers.fyerstrading.strategy;

import org.springframework.stereotype.Component;

@Component
public class PositionalNiftyStrategy {

	/*
	 * private final CandleFetcher priceService; private final OptionChainService
	 * oiDataService;
	 * 
	 * public PositionalNiftyStrategy(CandleFetcher priceService, OptionChainService
	 * oiDataService) { this.priceService = priceService; this.oiDataService =
	 * oiDataService; }
	 * 
	 * @Override public Optional<TradeMasterSignal> run(String symbol) { // Run only
	 * after market close (e.g. after 8 PM) if
	 * (LocalDateTime.now(ZoneId.of("Asia/Kolkata")).getHour() < 20) { return
	 * Optional.empty(); }
	 * 
	 * // Get last 60 daily candles for smoother EMA calculation
	 * List<NiftyDailyCandle> candles = priceService.getLastNCandlesNiftyDaily(60);
	 * if (candles == null || candles.size() < 50) { return Optional.empty(); }
	 * 
	 * NiftyDailyCandle last = candles.get(0); NiftyDailyCandle prev =
	 * candles.get(1);
	 * 
	 * // Calculate EMA21 & EMA50 from close prices double ema21 =
	 * calculateEMA(candles, 21); double ema50 = calculateEMA(candles, 50);
	 * 
	 * // Trend filter boolean upTrend = ema21 > ema50 && last.getClose() >
	 * prev.getClose(); boolean downTrend = ema21 < ema50 && last.getClose() <
	 * prev.getClose();
	 * 
	 * // Swing high/low breakout check (recent 5 bars) double recentHigh =
	 * candles.subList(1, 6).stream()
	 * .mapToDouble(NiftyDailyCandle::getHigh).max().orElse(0); double recentLow =
	 * candles.subList(1, 6).stream()
	 * .mapToDouble(NiftyDailyCandle::getLow).min().orElse(0);
	 * 
	 * double spot = priceService.getLTP(symbol); int atm =
	 * oiDataService.getATMStrike(spot,true);
	 * 
	 * // Bullish setup if (upTrend && last.getClose() > recentHigh) { return
	 * oiDataService.getOiForStrike(symbol,atm,true).flatMap(oi -> { if
	 * (oi.getCallOiChange() > 0 && oi.getPutOiChange() < 0) { return
	 * Optional.of(buildSignal(symbol, TradeMasterSignal.Direction.LONG, atm + "CE",
	 * oi.getCallLtp(), spot,
	 * "Daily EMA21>EMA50 + breakout above swing high + Call OI rising")); } return
	 * Optional.empty(); }); }
	 * 
	 * // Bearish setup if (downTrend && last.getClose() < recentLow) { return
	 * oiDataService.getOiForStrike(symbol,atm,true).flatMap(oi -> { if
	 * (oi.getPutOiChange() > 0 && oi.getCallOiChange() < 0) { return
	 * Optional.of(buildSignal(symbol, TradeMasterSignal.Direction.SHORT, atm +
	 * "PE", oi.getPutLtp(), spot,
	 * "Daily EMA21<EMA50 + breakdown below swing low + Put OI rising")); } return
	 * Optional.empty(); }); }
	 * 
	 * return Optional.empty(); }
	 * 
	 *//**
		 * Calculate EMA for the given period from a list of candles (descending order).
		 *//*
			 * private double calculateEMA(List<NiftyDailyCandle> candles, int period) {
			 * double multiplier = 2.0 / (period + 1); double ema =
			 * candles.get(candles.size() - period).getClose(); // start with older value
			 * 
			 * // Process from oldest to most recent for (int i = candles.size() - period +
			 * 1; i < candles.size(); i++) { double close = candles.get(i).getClose(); ema =
			 * ((close - ema) * multiplier) + ema; } return ema; }
			 * 
			 * private TradeMasterSignal buildSignal(String symbol,
			 * TradeMasterSignal.Direction direction, String optionSymbol, double optionLtp,
			 * double spot, String reason) { TradeMasterSignal signal = new
			 * TradeMasterSignal(); signal.setSymbol(symbol);
			 * signal.setDirection(direction); signal.setOptionSymbol("AUTO:" +
			 * optionSymbol); signal.setOptionEntryPrice(optionLtp);
			 * signal.setOptionSL(optionLtp * 0.7); // Wider SL for positional
			 * signal.setOptionTarget(optionLtp * 2.5); // Bigger reward for positional
			 * signal.setQty(1); signal.setUnderlyingAtSignal(spot);
			 * signal.setReason(reason); signal.setTimestamp(LocalDateTime.now()); return
			 * signal; }
			 */
}
