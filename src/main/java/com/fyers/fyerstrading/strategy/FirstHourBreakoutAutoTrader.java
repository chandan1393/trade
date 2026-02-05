package com.fyers.fyerstrading.strategy;

import com.fyers.fyerstrading.entity.FNO5MinCandle;
import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.entity.TradeExecution;
import com.fyers.fyerstrading.entity.TradeExecutionHourly;
import com.fyers.fyerstrading.entity.TradeSetup;
import com.fyers.fyerstrading.entity.TradeSetupHourly;
import com.fyers.fyerstrading.model.TradeMasterSignal;
import com.fyers.fyerstrading.repo.FNO5MinCandleRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.repo.TradeExecutionHourlyRepository;
import com.fyers.fyerstrading.repo.TradeSetupHourlyRepository;
import com.fyers.fyerstrading.service.FyersApiService;
import com.fyers.fyerstrading.service.common.CandleFetcher;
import com.fyers.fyerstrading.utility.TradingUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Auto trader for First-Hour Breakout -> places entry orders, manages OCO, trailing SL.
 * NOTE: replace FyersApiService stubs with your real implementation.
 */
@Service
public class FirstHourBreakoutAutoTrader {

    @Autowired private FNO5MinCandleRepository candleRepo;
    @Autowired private CandleFetcher priceService;
    @Autowired private FyersApiService fyersApi;
    @Autowired private TradeSetupHourlyRepository setupRepo;
    @Autowired private TradeExecutionHourlyRepository execRepo;
    
    @Autowired
	StockMasterRepository masterRepository;

    // Strategy / risk parameters (tune as needed)
    private static final double RISK_PER_TRADE = 2000.0;      // ₹ fixed risk
    private static final double ATR_SL_MULTIPLIER = 0.75;    // stop = 0.75 * ATR
    private static final double ATR_TARGET_MULTIPLIER = 1.5; // target = 1.5 * ATR
    private static final double ATR_TRAIL_STEP = 0.5;        // trail by 0.5 * ATR when profit >= 1x ATR
    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

    // in-memory map for quick active trade lookups (persisted copies saved to DB via repo)
    private final Map<String, TradeSetupHourly> activeSetups = new ConcurrentHashMap<>();

    /**
     * Scheduler: run scanner every 5 minutes between 10:20 and 14:45 (after first hour ends).
     * Cron format: second minute hour day month dayOfWeek
     */
  //  @Scheduled(cron = "0 */5 10-14 * * MON-FRI", zone = "Asia/Kolkata")
    public void scheduledScan() {
        LocalTime now = LocalTime.now(ZONE);
        // skip close to market close
        if (now.isBefore(LocalTime.of(10, 16)) || now.isAfter(LocalTime.of(14, 45))) return;

        // TODO: get list of symbols to scan (your F&O stock list)
        List<String> symbols = getActiveFnoSymbols();

        for (String symbol : symbols) {
            try {
                scanAndMaybePlace(symbol);
            } catch (Exception ex) {
                System.err.println("scan error for " + symbol + " : " + ex.getMessage());
            }
        }
    }

    /**
     * Process a single symbol: compute first-hour range, check breakout confirmation and filters.
     * If criteria pass and we don't already have an active trade for symbol, place entry.
     */
 // add this field globally in your class:
    private final Map<String, BreakoutCandidate> potentialBreakouts = new ConcurrentHashMap<>();

    private static class BreakoutCandidate {
        boolean breakoutUp;
        double triggerPrice;
        LocalDateTime timestamp;
    }

    private void scanAndMaybePlace(String symbol) {
        LocalDate today = LocalDate.now(ZONE);
        List<FNO5MinCandle> candles = candleRepo.findAllBWDate(
                symbol,
                LocalDateTime.of(today, LocalTime.of(9, 15)),
                LocalDateTime.of(today, LocalTime.of(15, 30))
        );
        if (candles == null || candles.isEmpty()) return;

        LocalTime firstHourEnd = LocalTime.of(10, 15);
        List<FNO5MinCandle> firstHour = candles.stream()
                .filter(c -> {
                    LocalTime t = c.getTimestamp().toLocalTime();
                    return !t.isBefore(LocalTime.of(9, 15)) && !t.isAfter(firstHourEnd);
                })
                .collect(Collectors.toList());
        if (firstHour.size() < 12) return;

        double firstHourHigh = firstHour.stream().mapToDouble(FNO5MinCandle::getHigh).max().orElse(Double.NaN);
        double firstHourLow = firstHour.stream().mapToDouble(FNO5MinCandle::getLow).min().orElse(Double.NaN);
        double avgFirstHourVol = firstHour.stream().mapToDouble(FNO5MinCandle::getVolume).average().orElse(0);

        FNO5MinCandle curr = candles.get(candles.size() - 1);
        LocalTime now = LocalTime.now(ZONE);
        if (curr.getTimestamp().toLocalTime().isBefore(firstHourEnd)) return; // wait for 10:15+

        // Skip if already active
        if (activeSetups.containsKey(symbol)) return;

        // Check if this symbol already had a breakout candidate
        BreakoutCandidate candidate = potentialBreakouts.get(symbol);

        if (candidate == null) {
            // 🔹 Phase 1: Detect initial breakout
            if (curr.getHigh() > firstHourHigh) {
                candidate = new BreakoutCandidate();
                candidate.breakoutUp = true;
                candidate.triggerPrice = firstHourHigh;
                candidate.timestamp = curr.getTimestamp();
                potentialBreakouts.put(symbol, candidate);
                System.out.printf("[%s] Potential UP breakout detected at %.2f%n", symbol, firstHourHigh);
                return;
            } else if (curr.getLow() < firstHourLow) {
                candidate = new BreakoutCandidate();
                candidate.breakoutUp = false;
                candidate.triggerPrice = firstHourLow;
                candidate.timestamp = curr.getTimestamp();
                potentialBreakouts.put(symbol, candidate);
                System.out.printf("[%s] Potential DOWN breakout detected at %.2f%n", symbol, firstHourLow);
                return;
            } else {
                return; // no breakout yet
            }
        } else {
            // 🔹 Phase 2: Confirm on next candle
            if (curr.getTimestamp().isBefore(candidate.timestamp.plusMinutes(5))) {
                return; // same candle (not next)
            }

            boolean confirmUp = candidate.breakoutUp && curr.getClose() > candidate.triggerPrice;
            boolean confirmDown = !candidate.breakoutUp && curr.getClose() < candidate.triggerPrice;
            if (!confirmUp && !confirmDown) {
                potentialBreakouts.remove(symbol);
                return;
            }

            // Technical filters
            double ema9 = priceService.calculateEMAFor5MinStocks(symbol, 9, curr.getTimestamp());
            double rsi = priceService.calculateRSIFor5Min(symbol, 14, curr.getTimestamp());
            double vwap = priceService.calculateVWAP(symbol, today, curr.getTimestamp());
            if (Double.isNaN(ema9) || Double.isNaN(rsi) || Double.isNaN(vwap)) return;

            if (confirmUp && (curr.getClose() < vwap || rsi < 55)) return;
            if (confirmDown && (curr.getClose() > vwap || rsi > 45)) return;

            boolean emaConfirm = (confirmUp && curr.getClose() > ema9) || (confirmDown && curr.getClose() < ema9);
            if (!emaConfirm) return;

            if (curr.getVolume() < avgFirstHourVol * 1.5) return;
            double body = Math.abs(curr.getClose() - curr.getOpen());
            double range = curr.getHigh() - curr.getLow();
            if (range == 0 || (body / range) < 0.6) return;

            // ATR
            double atr = calculateATR(candles, 14);
            if (Double.isNaN(atr) || atr <= 0) return;

            // ✅ Entry logic modified — buy *just above* breakout candle's high/low
            double buffer = 0.05; // small buffer to ensure breakout confirmation
            double entry = confirmUp ? curr.getHigh() + buffer : curr.getLow() - buffer;
            double sl = confirmUp ? entry - (atr * ATR_SL_MULTIPLIER) : entry + (atr * ATR_SL_MULTIPLIER);
            double target = confirmUp ? entry + (atr * ATR_TARGET_MULTIPLIER) : entry - (atr * ATR_TARGET_MULTIPLIER);
            double riskPerShare = Math.abs(entry - sl);
            int qty = Math.max(1, (int) Math.floor(RISK_PER_TRADE / riskPerShare));

            // Save setup
            TradeSetupHourly setup = new TradeSetupHourly();
            setup.setSymbol(symbol);
            setup.setDirection(confirmUp ? TradeMasterSignal.Direction.LONG : TradeMasterSignal.Direction.SHORT);
            setup.setEntryPrice(entry);
            setup.setStopLoss(TradingUtil.roundToTwoDecimalPlaces(sl));
            setup.setTargetPrice(TradingUtil.roundToTwoDecimalPlaces(target));
            setup.setQty(qty);
            setup.setAtr(atr);
            setup.setCreatedAt(LocalDateTime.now(ZONE));
            setupRepo.save(setup);

            activeSetups.put(symbol, setup);
            potentialBreakouts.remove(symbol);

            System.out.printf("✅ Confirmed %s breakout | %s | entry=%.2f (above breakout candle) sl=%.2f target=%.2f qty=%d%n",
                    symbol, confirmUp ? "LONG" : "SHORT", entry, sl, target, qty);
        }
    }



    
	/*
	 * public void onOrderExecuted(String orderId, double executedPrice, int
	 * executedQty, String symbol) { // find setup by entryOrderId TradeSetupHourly
	 * setup = setupRepo.findByEntryOrderId(orderId); if (setup == null) {
	 * System.err.println("Filled order not matched to setup: " + orderId); return;
	 * }
	 * 
	 * // create OCO on broker (target & stop). Use broker OCO endpoint or place
	 * both orders and link them. String ocoId = fyersApi.placeOCOOrder( symbol,
	 * setup.getDirection() == TradeMasterSignal.Direction.LONG ? "SELL" : "BUY",
	 * setup.getTargetPrice(), setup.getStopLoss(), setup.getQty() );
	 * 
	 * // persist execution TradeExecutionHourly exec = new TradeExecutionHourly();
	 * exec.setSymbol(symbol); exec.setEntryOrderId(orderId);
	 * exec.setEntryPrice(executedPrice); exec.setQty(executedQty);
	 * exec.setOcoId(ocoId); exec.setEntryTimestamp(LocalDateTime.now(ZONE));
	 * execRepo.save(exec);
	 * 
	 * setup.setActive(true); setup.setOcoId(ocoId); setupRepo.save(setup);
	 * 
	 * System.out.printf("Order filled for %s at %.2f qty=%d; OCO %s placed%n",
	 * symbol, executedPrice, executedQty, ocoId); }
	 */
    /**
     * Periodic task (every 1 minute) to update trailing SL for active trades.
     * Reads active setups and adjusts stop via broker if trailing condition met.
     */
	/*
	 * @Scheduled(fixedRate = 60_000, zone = "Asia/Kolkata") public void
	 * manageTrailingStops() { for (TradeSetup setup : setupRepo.findActiveSetups())
	 * { try { // get latest market price double ltp =
	 * priceService.getUnderlyingLTP(setup.getSymbol()); double atr =
	 * setup.getAtr(); double entry = setup.getEntryPrice(); boolean longSide =
	 * setup.getDirection() == TradeMasterSignal.Direction.LONG;
	 * 
	 * // If not yet reached 1*ATR profit, skip double profit = longSide ? ltp -
	 * entry : entry - ltp; if (profit < atr) continue;
	 * 
	 * // new trail = current close - 0.5*ATR (for long), or current close + 0.5*ATR
	 * (for short) double newTrail = longSide ? Math.max(setup.getStopLoss(), ltp -
	 * ATR_TRAIL_STEP * atr) : Math.min(setup.getStopLoss(), ltp + ATR_TRAIL_STEP *
	 * atr);
	 * 
	 * // only update if trail moves favourably by at least some minimal tick if
	 * (longSide && newTrail > setup.getStopLoss() +
	 * tickThreshold(setup.getSymbol())) { // update stop order on broker (via fyers
	 * API) — assumes you can modify stop in OCO
	 * fyersApi.modifyOCOStop(setup.getOcoId(), newTrail);
	 * setup.setStopLoss(newTrail); setupRepo.save(setup);
	 * System.out.printf("Trailed SL up for %s -> %.2f%n", setup.getSymbol(),
	 * newTrail); } else if (!longSide && newTrail < setup.getStopLoss() -
	 * tickThreshold(setup.getSymbol())) { fyersApi.modifyOCOStop(setup.getOcoId(),
	 * newTrail); setup.setStopLoss(newTrail); setupRepo.save(setup);
	 * System.out.printf("Trailed SL down for %s -> %.2f%n", setup.getSymbol(),
	 * newTrail); } } catch (Exception e) { System.err.println("Trailing error for "
	 * + setup.getSymbol() + ": " + e.getMessage()); } } }
	 */

    private double tickThreshold(String symbol) {
        // minimal price move to trigger modify request, to avoid spamming broker
        return 0.05; // adjust as needed
    }

    // ATR calc reused from earlier backtest logic (simple true-range moving avg)
    private double calculateATR(List<FNO5MinCandle> candles, int period) {
        if (candles == null || candles.size() < period) return Double.NaN;
        int start = Math.max(1, candles.size() - period);
        double sumTR = 0.0;
        int count = 0;
        for (int i = start; i < candles.size(); i++) {
            FNO5MinCandle c = candles.get(i);
            FNO5MinCandle prev = candles.get(i - 1);
            double tr = Math.max(c.getHigh() - c.getLow(),
                    Math.max(Math.abs(c.getHigh() - prev.getClose()), Math.abs(c.getLow() - prev.getClose())));
            sumTR += tr;
            count++;
        }
        return count == 0 ? Double.NaN : sumTR / count;
    }

    // TODO: replace with your worklist of symbols (F&O stock list)
    private List<String> getActiveFnoSymbols() {
    	List<StockMaster> list = masterRepository.findByIsInFnoTrue();
		List<String> allStocks=list.stream().map(a->a.getSymbol()).collect(Collectors.toList());
		return allStocks;
    }
}
