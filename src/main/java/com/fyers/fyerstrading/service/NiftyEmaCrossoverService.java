package com.fyers.fyerstrading.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.EMATradeSignal;
import com.fyers.fyerstrading.entity.Nifty5MinCandle;
import com.fyers.fyerstrading.model.OrderResponse;
import com.fyers.fyerstrading.model.Range;
import com.fyers.fyerstrading.repo.EMABacktestSignalRepo;
import com.fyers.fyerstrading.repo.EMACrossoverSignalRepo;
import com.fyers.fyerstrading.repo.EMATradeSignalRepo;
import com.fyers.fyerstrading.repo.NiftyFiveMinCandleRepository;
import com.fyers.fyerstrading.service.common.CandleFetcher;
import com.tts.in.model.PlaceOrderModel;
import com.tts.in.utilities.OrderType;
import com.tts.in.utilities.OrderValidity;
import com.tts.in.utilities.ProductType;
import com.tts.in.utilities.TransactionType;

@Service
public class NiftyEmaCrossoverService {

	/*
	 * private final NiftyFiveMinCandleRepository candleRepo; private final
	 * EMACrossoverSignalRepo crossoverSignalRepo; private final
	 * EMABacktestSignalRepo emaBacktestSignalRepo; private final EMATradeSignalRepo
	 * emaTradeSignalRepo; private final IndexOIService oiDataService; private final
	 * FyersApiService fyersApiService;
	 * 
	 * private final CandleFetcher priceService;
	 * 
	 * private static final Logger logger =
	 * LoggerFactory.getLogger(NiftyEmaCrossoverService.class);
	 * 
	 * private EMATradeSignal activeTrade = null; // single intraday trade at a time
	 * 
	 * public NiftyEmaCrossoverService(NiftyFiveMinCandleRepository candleRepo,
	 * EMACrossoverSignalRepo crossoverSignalRepo, EMABacktestSignalRepo
	 * emaBacktestSignalRepo, EMATradeSignalRepo emaTradeSignalRepo, IndexOIService
	 * oiDataService, FyersApiService fyersApiService,CandleFetcher priceService) {
	 * this.candleRepo = candleRepo; this.crossoverSignalRepo = crossoverSignalRepo;
	 * this.emaBacktestSignalRepo = emaBacktestSignalRepo; this.emaTradeSignalRepo =
	 * emaTradeSignalRepo; this.oiDataService = oiDataService; this.fyersApiService
	 * = fyersApiService; this.priceService=priceService; }
	 * 
	 * public void runIntradayStrategy() { LocalTime now =
	 * LocalTime.now(ZoneId.of("Asia/Kolkata"));
	 * 
	 * // ✅ Run only during trading hours (avoid 1st 5 min & post 3:20) if
	 * (now.isBefore(LocalTime.of(9, 25)) || now.isAfter(LocalTime.of(15, 20))) {
	 * logger.info("Outside trading hours: {}", now); return; }
	 * 
	 * try { // ✅ Fetch last few candles (need more for RSI and confirmation)
	 * List<Nifty5MinCandle> lastCandles = candleRepo.findTopNByTimestamp(30); if
	 * (lastCandles.size() < 30) {
	 * logger.warn("Not enough candles for EMA strategy. Found {}",
	 * lastCandles.size()); return; }
	 * 
	 * Collections.reverse(lastCandles); // oldest → latest Nifty5MinCandle prev =
	 * lastCandles.get(lastCandles.size() - 2); Nifty5MinCandle curr =
	 * lastCandles.get(lastCandles.size() - 1);
	 * 
	 * double niftyPrice = curr.getClose();
	 * 
	 * // ✅ Detect base/consolidation Optional<Range> baseOpt = detectBase(8, 0.3);
	 * if (!baseOpt.isPresent()) { logger.debug("No base/consolidation detected.");
	 * return; } Range base = baseOpt.get();
	 * 
	 * // ✅ Calculate RSI for 14-period (on 5-min) double rsi =
	 * priceService.calculateRSI(lastCandles, 14); logger.debug("Current RSI: {}",
	 * String.format("%.2f", rsi));
	 * 
	 * // ✅ Fetch OI bias (for confirmation) int atmStrike =
	 * oiDataService.getATMStrike(niftyPrice, true); double callOIChange =
	 * oiDataService.fetchOIChange("NSE:NIFTY50-INDEX", atmStrike, true); double
	 * putOIChange = oiDataService.fetchOIChange("NSE:NIFTY50-INDEX", atmStrike,
	 * false);
	 * 
	 * // ✅ Handle existing trade first if (activeTrade != null) {
	 * handleExit(activeTrade, prev, curr, niftyPrice, now); return; }
	 * 
	 * // ✅ EMA crossover detection boolean longCross = prev.getEma9() <=
	 * prev.getEma21() && curr.getEma9() > curr.getEma21(); boolean shortCross =
	 * prev.getEma9() >= prev.getEma21() && curr.getEma9() < curr.getEma21();
	 * 
	 * // ✅ Long setup filters if (longCross && curr.getClose() > base.getHigh() //
	 * breakout above base && rsi > 45 && rsi < 70 // bullish but not overbought &&
	 * callOIChange > putOIChange // long-side bias && curr.getClose() >
	 * curr.getEma21()) { // price above both EMAs
	 * 
	 * double stopLoss = base.getLow(); double target = curr.getClose() +
	 * (curr.getClose() - stopLoss) * 1.5; // 1.5R target
	 * 
	 * activeTrade = createTrade("LONG", curr, base, niftyPrice, String
	 * .format("EMA crossover + Base breakout + RSI %.1f + OI bias (%.2f%%)", rsi,
	 * callOIChange));
	 * 
	 * activeTrade.setSl(stopLoss); activeTrade.setTarget(target);
	 * emaTradeSignalRepo.save(activeTrade); logger.info("✅ LONG Trade Opened: {}",
	 * activeTrade); }
	 * 
	 * // ✅ Short setup filters else if (shortCross && curr.getClose() <
	 * base.getLow() // breakdown below base && rsi < 55 && rsi > 25 // bearish but
	 * not oversold && putOIChange > callOIChange // short-side bias &&
	 * curr.getClose() < curr.getEma21()) { // price below both EMAs
	 * 
	 * double stopLoss = base.getHigh(); double target = curr.getClose() - (stopLoss
	 * - curr.getClose()) * 1.5;
	 * 
	 * activeTrade = createTrade("SHORT", curr, base, niftyPrice, String
	 * .format("EMA crossover + Base breakdown + RSI %.1f + OI bias (%.2f%%)", rsi,
	 * putOIChange));
	 * 
	 * activeTrade.setSl(stopLoss); activeTrade.setTarget(target);
	 * emaTradeSignalRepo.save(activeTrade); logger.info("✅ SHORT Trade Opened: {}",
	 * activeTrade); }
	 * 
	 * } catch (Exception e) { logger.error("Error in intraday EMA strategy: {}",
	 * e.getMessage(), e); } }
	 * 
	 *//**
		 * Handle exit logic: stop-loss, opposite crossover, or end-of-day.
		 */
	/*
	 * private void handleExit(EMATradeSignal trade, Nifty5MinCandle prev,
	 * Nifty5MinCandle curr, double niftyPrice, LocalTime now) { boolean exit =
	 * false; double exitPrice = 0; String reason = "";
	 * 
	 * // Stop-loss hit if ("LONG".equals(trade.getDirection()) && curr.getLow() <=
	 * trade.getSl()) { exit = true; exitPrice = trade.getSl(); reason =
	 * "Stop Loss Hit"; } else if ("SHORT".equals(trade.getDirection()) &&
	 * curr.getHigh() >= trade.getSl()) { exit = true; exitPrice = trade.getSl();
	 * reason = "Stop Loss Hit"; } // Opposite EMA crossover else if
	 * ("LONG".equals(trade.getDirection()) && prev.getEma9() >= prev.getEma21() &&
	 * curr.getEma9() < curr.getEma21()) { exit = true; exitPrice = curr.getClose();
	 * reason = "Opposite Crossover"; } else if
	 * ("SHORT".equals(trade.getDirection()) && prev.getEma9() <= prev.getEma21() &&
	 * curr.getEma9() > curr.getEma21()) { exit = true; exitPrice = curr.getClose();
	 * reason = "Opposite Crossover"; } // End-of-day square-off if
	 * (now.isAfter(LocalTime.of(15, 20))) { exit = true; exitPrice =
	 * curr.getClose(); reason = "EOD Square-off"; }
	 * 
	 * if (exit) { trade.setExitPrice(exitPrice);
	 * trade.setExitTime(curr.getTimestamp()); trade.setExitReason(reason);
	 * emaTradeSignalRepo.save(trade); System.out.println("Trade closed: " + trade);
	 * activeTrade = null; } }
	 * 
	 * private EMATradeSignal createTrade(String direction, Nifty5MinCandle candle,
	 * Range base, double niftyPrice, String reason) {
	 * 
	 * double sl = "LONG".equals(direction) ? base.getLow() : base.getHigh(); double
	 * target = "LONG".equals(direction) ? niftyPrice + (niftyPrice - sl) :
	 * niftyPrice - (sl - niftyPrice);
	 * 
	 * // Select ATM strike using current nifty price int atmStrike = (int)
	 * oiDataService.getATMStrike(niftyPrice, true); String optionSymbol =
	 * getOptionSymbol(direction, atmStrike);
	 * 
	 * EMATradeSignal trade = new EMATradeSignal(); trade.setDirection(direction);
	 * trade.setEntryPrice(niftyPrice); trade.setSl(sl); trade.setTarget(target);
	 * trade.setOptionSymbol(optionSymbol); trade.setReason(reason);
	 * trade.setTimestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
	 * 
	 * // Place order via Fyers API PlaceOrderModel model = new PlaceOrderModel();
	 * model.Symbol = optionSymbol; model.Qty = 1; model.OrderType =
	 * OrderType.MarketOrder.getDescription(); model.Side = "LONG".equals(direction)
	 * ? TransactionType.Buy.getValue() : TransactionType.Sell.getValue();
	 * model.ProductType = ProductType.INTRADAY; model.LimitPrice = 0;
	 * model.StopPrice = 0; // For intraday, SL/TP handled in bracket order
	 * model.OrderValidity = OrderValidity.DAY; model.DisclosedQty = 0;
	 * model.OffLineOrder = false; model.StopLoss = sl; model.TakeProfit = target;
	 * model.OrderTag = "EMA_" + direction + "_" + atmStrike;
	 * 
	 * // Place order and handle response OrderResponse orderResponse =
	 * fyersApiService.placeOrder(model);
	 * 
	 * if (orderResponse.getId() != null) {
	 * System.out.println("Order placed successfully, Order ID: " +
	 * orderResponse.getId()); trade.setOrderResponse(orderResponse.getId()); } else
	 * { System.out.println("Place order failed: " + orderResponse.getMessage());
	 * trade.setOrderResponse(orderResponse != null ? orderResponse.getMessage() :
	 * "No response"); }
	 * 
	 * return trade; }
	 * 
	 *//**
		 * Detect base formation over lookback candles with tolerance
		 */
	/*
	 * public Optional<Range> detectBase(int lookback, double tolerancePercent) {
	 * List<Nifty5MinCandle> lastCandles =
	 * candleRepo.findTopNByOrderByTimestampDesc(PageRequest.of(0, lookback));
	 * 
	 * if (lastCandles.size() < lookback) return Optional.empty();
	 * 
	 * // Reverse chronological order → oldest to newest
	 * Collections.reverse(lastCandles);
	 * 
	 * double maxHigh =
	 * lastCandles.stream().mapToDouble(Nifty5MinCandle::getHigh).max().orElse(0);
	 * double minLow =
	 * lastCandles.stream().mapToDouble(Nifty5MinCandle::getLow).min().orElse(0);
	 * double range = maxHigh - minLow;
	 * 
	 * // Relative tolerance (e.g., 1% of current price or fixed % of range) double
	 * avgPrice = (maxHigh + minLow) / 2.0; double allowedRange = avgPrice *
	 * (tolerancePercent / 100.0);
	 * 
	 * // Check for narrow base if (range > allowedRange) return Optional.empty();
	 * 
	 * // Check multiple touches near support/resistance (for stronger base
	 * structure) long nearLowTouches = lastCandles.stream().filter(c ->
	 * Math.abs(c.getLow() - minLow) / avgPrice < 0.002) // within // 0.2% .count();
	 * 
	 * long nearHighTouches = lastCandles.stream().filter(c -> Math.abs(c.getHigh()
	 * - maxHigh) / avgPrice < 0.002) .count();
	 * 
	 * // Ensure at least 2–3 touches on either boundary → confirms a stable base if
	 * (nearLowTouches < 2 && nearHighTouches < 2) return Optional.empty();
	 * 
	 * return Optional.of(new Range(minLow, maxHigh)); }
	 * 
	 *//**
		 * Determine option symbol for LONG/SHORT trade
		 */
	/*
	 * private String getOptionSymbol(String direction, int atmStrike) { return
	 * "LONG".equals(direction) ? "NSE:NIFTY" + atmStrike + "CE" : "NSE:NIFTY" +
	 * atmStrike + "PE"; }
	 * 
	 *//**
		 * Optional: check latest crossover signal (EMA + Base)
		 *//*
			 * public Optional<String> checkCrossoverWithBaseFilter(int baseLookback, double
			 * tolerancePercent) { List<Nifty5MinCandle> lastTwo =
			 * candleRepo.findTop2ByOrderByTimestampDesc(); if (lastTwo.size() < 2) return
			 * Optional.empty();
			 * 
			 * Nifty5MinCandle prev = lastTwo.get(1); Nifty5MinCandle curr = lastTwo.get(0);
			 * Optional<Range> baseOpt = detectBase(baseLookback, tolerancePercent);
			 * 
			 * if (baseOpt.isPresent()) { Range base = baseOpt.get(); if (prev.getEma9() <=
			 * prev.getEma21() && curr.getEma9() > curr.getEma21() && curr.getClose() >
			 * base.getHigh()) return Optional.of("LONG crossover + breakout @ " +
			 * curr.getClose() + " on " + curr.getTimestamp()); if (prev.getEma9() >=
			 * prev.getEma21() && curr.getEma9() < curr.getEma21() && curr.getClose() <
			 * base.getLow()) return Optional.of("SHORT crossover + breakdown @ " +
			 * curr.getClose() + " on " + curr.getTimestamp()); } return Optional.empty(); }
			 * 
			 * public String exitAtDayEnd() {
			 * 
			 * return ""; }
			 */
}
