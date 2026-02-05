package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.IntradayLivePosition;
import com.fyers.fyerstrading.entity.IntradayTrade;
import com.fyers.fyerstrading.enu.Side;
import com.fyers.fyerstrading.model.CandleContext;
import com.fyers.fyerstrading.model.MasterCandle;
import com.fyers.fyerstrading.model.OptionContract;
import com.fyers.fyerstrading.model.OrderResponse;
import com.fyers.fyerstrading.repo.IntradayLivePositionRepository;
import com.fyers.fyerstrading.repo.IntradayTradeRepository;
import com.fyers.fyerstrading.service.common.CandleFetcher;
import com.fyers.fyerstrading.strategy.IntradayVWAPSignalEngine;

@Service
public class IntradayVWAPLiveService {

	/*
	 * @Autowired private CandleFetcher candleFetcher;
	 * 
	 * @Autowired private OrderExecutionServiceForIntradayVWAP orderService;
	 * 
	 * @Autowired private OptionSelectionService optionSelectionService;
	 * 
	 * @Autowired private IntradayLivePositionRepository positionRepo;
	 * 
	 * @Autowired private IntradayTradeRepository tradeRepo;
	 * 
	 * private static final double CAPITAL = 100_000; private static final double
	 * RISK_PCT = 0.01;
	 * 
	 * // ================= ENTRY POINT ================= public void
	 * onNewCandle(String spotSymbol) {
	 * 
	 * CandleContext ctx = loadCandleContext(spotSymbol); if (ctx == null) return;
	 * 
	 * Optional<IntradayLivePosition> openPos =
	 * positionRepo.findBySpotSymbolAndTradeDate(spotSymbol, ctx.today);
	 * 
	 * if (openPos.isEmpty()) { tryEnterTrade(spotSymbol, ctx); } else {
	 * manageOpenPosition(openPos.get(), ctx); } }
	 * 
	 * // ================= STEP 1 : LOAD CANDLES ================= private
	 * CandleContext loadCandleContext(String symbol) {
	 * 
	 * LocalDateTime end = LocalDateTime.now(); LocalDateTime start =
	 * end.minusDays(4);
	 * 
	 * List<MasterCandle> candles = candleFetcher.getCandlesBWDates(symbol, "5m",
	 * start, end);
	 * 
	 * if (candles == null || candles.isEmpty()) return null;
	 * 
	 * Map<LocalDate, List<MasterCandle>> byDate = candles.stream()
	 * .collect(Collectors.groupingBy(c -> c.getTime().toLocalDate(), TreeMap::new,
	 * Collectors.toList()));
	 * 
	 * if (byDate.size() < 2) return null;
	 * 
	 * List<LocalDate> days = new ArrayList<>(byDate.keySet());
	 * 
	 * LocalDate today = days.get(days.size() - 1); LocalDate prevDay =
	 * days.get(days.size() - 2);
	 * 
	 * List<MasterCandle> todayCandles = byDate.get(today); List<MasterCandle>
	 * prevCandles = byDate.get(prevDay);
	 * 
	 * if (todayCandles == null || todayCandles.size() < 20) return null;
	 * 
	 * return new CandleContext(today, prevDay, todayCandles, prevCandles); }
	 * 
	 * // ================= STEP 2 : ENTRY ================= private void
	 * tryEnterTrade(String spotSymbol, CandleContext ctx) {
	 * 
	 * IntradayTrade signal = IntradayVWAPSignalEngine.evaluate(spotSymbol,
	 * ctx.todayCandles, ctx.prevDayCandles, CAPITAL, RISK_PCT);
	 * 
	 * if (signal == null) return;
	 * 
	 * OptionContract option = optionSelectionService.selectOption(spotSymbol,
	 * signal.getSide());
	 * 
	 * double optionEntryPrice = orderService.getLtp(option.getOptionSymbol());
	 * 
	 * int qty = option.getQty();
	 * 
	 * OrderResponse res = orderService.placeEntryOrder(option.getOptionSymbol(),
	 * Side.BUY, qty, optionEntryPrice);
	 * 
	 * IntradayLivePosition pos = new IntradayLivePosition();
	 * pos.setOptionSymbol(option.getOptionSymbol()); pos.setSpotSymbol(spotSymbol);
	 * pos.setSide(signal.getSide());
	 * 
	 * // OPTION DATA pos.setOptionEntryPrice(optionEntryPrice); pos.setQty(qty);
	 * 
	 * // SPOT DATA (ALL LOGIC USES THIS)
	 * pos.setSpotEntryPrice(signal.getEntryPrice());
	 * pos.setSpotSl(signal.getInitialSl());
	 * pos.setSpotBestPrice(signal.getEntryPrice());
	 * 
	 * pos.setTrailActive(false); pos.setBrokerOrderId(res.getId());
	 * pos.setTradeDate(ctx.today); pos.setEntryTime(LocalDateTime.now());
	 * 
	 * positionRepo.save(pos); }
	 * 
	 * // ================= STEP 3 : MANAGE OPEN POSITION ================= private
	 * void manageOpenPosition(IntradayLivePosition pos, CandleContext ctx) {
	 * 
	 * double spotLtp = orderService.getLtp(pos.getSpotSymbol());
	 * 
	 * boolean exit = applySpotTrailing(pos, spotLtp);
	 * 
	 * positionRepo.save(pos);
	 * 
	 * if (exit) { exitOptionTrade(pos, ctx.today); } }
	 * 
	 * // ================= STEP 4 : TRAILING ================= private boolean
	 * applySpotTrailing(IntradayLivePosition pos, double spotLtp) {
	 * 
	 * return pos.getSide() == Side.BUY ? trailBuy(pos, spotLtp) : trailSell(pos,
	 * spotLtp); }
	 * 
	 * private boolean trailBuy(IntradayLivePosition pos, double spotLtp) {
	 * 
	 * pos.setSpotBestPrice(Math.max(pos.getSpotBestPrice(), spotLtp));
	 * 
	 * double risk = pos.getSpotEntryPrice() - pos.getSpotSl();
	 * 
	 * if (!pos.isTrailActive() && pos.getSpotBestPrice() >= pos.getSpotEntryPrice()
	 * + risk) { pos.setTrailActive(true); }
	 * 
	 * if (pos.isTrailActive()) { double newSl = pos.getSpotBestPrice() - risk;
	 * pos.setSpotSl(Math.max(pos.getSpotSl(), newSl)); }
	 * 
	 * return spotLtp <= pos.getSpotSl(); }
	 * 
	 * private boolean trailSell(IntradayLivePosition pos, double spotLtp) {
	 * 
	 * pos.setSpotBestPrice(Math.min(pos.getSpotBestPrice(), spotLtp));
	 * 
	 * double risk = pos.getSpotSl() - pos.getSpotEntryPrice();
	 * 
	 * if (!pos.isTrailActive() && pos.getSpotBestPrice() <= pos.getSpotEntryPrice()
	 * - risk) { pos.setTrailActive(true); }
	 * 
	 * if (pos.isTrailActive()) { double newSl = pos.getSpotBestPrice() + risk;
	 * pos.setSpotSl(Math.min(pos.getSpotSl(), newSl)); }
	 * 
	 * return spotLtp >= pos.getSpotSl(); }
	 * 
	 * // ================= STEP 5 : EXIT ================= private void
	 * exitOptionTrade(IntradayLivePosition pos, LocalDate tradeDate) {
	 * 
	 * double optionExitPrice = orderService.getLtp(pos.getOptionSymbol());
	 * OrderResponse res=orderService.placeExitOrder(pos.getOptionSymbol(),
	 * Side.SELL, pos.getQty());
	 * 
	 * IntradayTrade trade = new IntradayTrade();
	 * trade.setSymbol(pos.getOptionSymbol()); trade.setSide(pos.getSide());
	 * trade.setTradeDate(tradeDate);
	 * trade.setEntryPrice(pos.getOptionEntryPrice());
	 * trade.setExitPrice(optionExitPrice); trade.setQuantity(pos.getQty());
	 * trade.setExitReason("SPOT_TSL"); trade.setLive(true);
	 * trade.setPnl((optionExitPrice - pos.getOptionEntryPrice()) * pos.getQty());
	 * 
	 * tradeRepo.save(trade); positionRepo.delete(pos); }
	 */
}
