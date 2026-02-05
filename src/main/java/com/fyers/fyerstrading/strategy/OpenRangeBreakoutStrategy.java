package com.fyers.fyerstrading.strategy;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.Nifty5MinCandle;
import com.fyers.fyerstrading.entity.NiftyDailyCandle;
import com.fyers.fyerstrading.model.ORBResult;
import com.fyers.fyerstrading.service.common.CandleFetcher;

@Service
public class OpenRangeBreakoutStrategy {

	@Autowired
	private CandleFetcher priceService;


	private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

	// --- configurable parameters
	private final int OR_LENGTH_MIN = 30; // first 30 min as open range
	private final int CANDLE_MINUTES = 5;
	private final double RISK_PER_TRADE = 2000.0; // fixed ₹ risk
	private final double ATR_TARGET_MULT = 3.0;
	private final double ATR_SL_MULT = 1.0;
	private final double OR_RANGE_SL_FRAC = 0.35;
	private final LocalTime LAST_ENTRY_TIME = LocalTime.of(14, 30);

	public ORBResult backtestOpenRangeForDate(LocalDate date) {
	    System.out.printf("\n=== ORB Backtest for %s ===%n", date);

	    // --- 1️⃣ Get 5-min candles for the day
	    List<Nifty5MinCandle> candles = priceService.getNifty5MinCandelBWDate(
	            LocalDateTime.of(date, LocalTime.of(9, 15)),
	            LocalDateTime.of(date, LocalTime.of(15, 30)));

	    if (candles == null || candles.size() < (OR_LENGTH_MIN / CANDLE_MINUTES)) {
	        System.out.println("Not enough candles for OR calculation. Exiting.");
	        return null;
	    }

	    // --- 2️⃣ Compute Open Range
	    LocalTime orStart = LocalTime.of(9, 15);
	    LocalTime orEnd = orStart.plusMinutes(OR_LENGTH_MIN);

	    List<Nifty5MinCandle> orCandles = candles.stream()
	            .filter(c -> !c.getTimestamp().toLocalTime().isBefore(orStart)
	                    && c.getTimestamp().toLocalTime().isBefore(orEnd))
	            .collect(Collectors.toList());

	    if (orCandles.isEmpty()) return null;

	    double orHigh = orCandles.stream().mapToDouble(Nifty5MinCandle::getHigh).max().orElse(Double.NaN);
	    double orLow = orCandles.stream().mapToDouble(Nifty5MinCandle::getLow).min().orElse(Double.NaN);
	    double orRange = orHigh - orLow;

	    System.out.printf("Open Range High=%.2f Low=%.2f Range=%.2f%n", orHigh, orLow, orRange);

	    // --- 3️⃣ Daily Trend Filter
	    boolean useTrendFilter = true;
	    boolean dailyTrendLong = true;
	    try {
	        List<NiftyDailyCandle> recentDaily = priceService.findRecentBeforeDate(date, 20);
	        if (!recentDaily.isEmpty()) {
	            NiftyDailyCandle prevDay = recentDaily.get(recentDaily.size() - 1);
	            List<NiftyDailyCandle> last20 = recentDaily.size() <= 20 ? recentDaily
	                    : recentDaily.subList(recentDaily.size() - 20, recentDaily.size());
	            double ema20 = priceService.calculateEMA(last20, 20);
	            dailyTrendLong = prevDay.getClose() > ema20;
	            System.out.printf("PrevDayClose=%.2f EMA20=%.2f → Trend=%s%n",
	                    prevDay.getClose(), ema20, dailyTrendLong ? "BULLISH" : "BEARISH");
	        } else useTrendFilter = false;
	    } catch (Exception e) {
	        System.out.println("Trend filter unavailable: " + e.getMessage());
	        useTrendFilter = false;
	    }

	    // --- 4️⃣ ATR Calculation (daily ATR 14)
	    List<NiftyDailyCandle> atrCandles = priceService.findRecentBeforeDate(date, 15);
	    double atr = priceService.calculateNiftyDailyATR(atrCandles, 14);
	    if (Double.isNaN(atr) || atr <= 0) atr = orRange * 0.5;

	    // --- 5️⃣ Trade simulation: 1 lot, 1 target, TSL
	    double totalPnL = 0.0, grossProfit = 0.0, grossLoss = 0.0, maxDrawdown = 0.0;
	    double equity = 0.0, peakEquity = 0.0;
	    int trades = 0, wins = 0, losses = 0;

	    int startIndex = 0;
	    for (int idx = 0; idx < candles.size(); idx++) {
	        if (!candles.get(idx).getTimestamp().toLocalTime().isBefore(orEnd)) {
	            startIndex = idx;
	            break;
	        }
	    }

	    for (int i = startIndex; i < candles.size(); i++) {
	        Nifty5MinCandle curr = candles.get(i);
	        LocalTime time = curr.getTimestamp().toLocalTime();

	        if (time.isAfter(LAST_ENTRY_TIME)) break;

	        boolean breakoutUp = curr.getHigh() > orHigh;
	        boolean breakoutDown = curr.getLow() < orLow;

	        if (!breakoutUp && !breakoutDown) continue;
	        if (useTrendFilter) {
	            if (breakoutUp && !dailyTrendLong) continue;
	            if (breakoutDown && dailyTrendLong) continue;
	        }

	        boolean isLong = breakoutUp;
	        double entry = isLong ? curr.getHigh() + 0.05 : curr.getLow() - 0.05;

	        // SL & Target
	        double slDistance = Math.max(orRange * OR_RANGE_SL_FRAC, ATR_SL_MULT * atr);
	        double sl = isLong ? entry - slDistance : entry + slDistance;
	        double target = isLong ? entry + ATR_TARGET_MULT * atr : entry - ATR_TARGET_MULT * atr;

	        int qty = 1; // 1 lot
	        double trailSL = sl;
	        double exitPrice = entry;
	        LocalDateTime exitTime = null;
	        String exitReason = "EOD";

	        for (int j = i; j < candles.size(); j++) {
	            Nifty5MinCandle f = candles.get(j);
	            double high = f.getHigh(), low = f.getLow(), close = f.getClose();

	            if (isLong) {
	                if (low <= trailSL) { // TSL hit
	                    exitPrice = trailSL;
	                    exitReason = "SL";
	                    exitTime = f.getTimestamp();
	                    break;
	                }
	                if (high >= target) { // Target hit
	                    exitPrice = target;
	                    exitReason = "Target";
	                    exitTime = f.getTimestamp();
	                    break;
	                }
	                if ((close - entry) >= atr) trailSL = Math.max(trailSL, close - 0.5 * atr);
	            } else {
	                if (high >= trailSL) { // TSL hit
	                    exitPrice = trailSL;
	                    exitReason = "SL";
	                    exitTime = f.getTimestamp();
	                    break;
	                }
	                if (low <= target) { // Target hit
	                    exitPrice = target;
	                    exitReason = "Target";
	                    exitTime = f.getTimestamp();
	                    break;
	                }
	                if ((entry - close) >= atr) trailSL = Math.min(trailSL, close + 0.5 * atr);
	            }
	        }

	        if (exitTime == null) {
	            Nifty5MinCandle last = candles.get(candles.size() - 1);
	            exitPrice = last.getClose();
	            exitTime = last.getTimestamp();
	        }

	        double pnl = (isLong ? (exitPrice - entry) : (entry - exitPrice)) * qty;
	        totalPnL += pnl;
	        equity += pnl;
	        peakEquity = Math.max(peakEquity, equity);
	        maxDrawdown = Math.max(maxDrawdown, peakEquity - equity);

	        trades++;
	        if (pnl > 0) wins++; else losses++;

	        System.out.printf("%s | Entry=%.2f | SL=%.2f | Target=%.2f | Exit=%.2f | Qty=%d | PnL=%.2f | Exit=%s (%s)%n",
	                (isLong ? "LONG" : "SHORT"), entry, sl, target, exitPrice, qty, pnl,
	                exitTime.toLocalTime(), exitReason);

	        break; // only 1 trade per day
	    }

	    double winRate = trades > 0 ? (wins * 100.0 / trades) : 0;
	    double avgWin = wins > 0 ? grossProfit / wins : 0;
	    double avgLoss = losses > 0 ? grossLoss / losses : 0;

	    System.out.printf("SUMMARY → Trades=%d Wins=%d Losses=%d WinRate=%.2f%% NetPnL=%.2f%n",
	            trades, wins, losses, winRate, totalPnL);

	    return new ORBResult(date, trades, wins, losses, totalPnL,
	            grossProfit, grossLoss, maxDrawdown, winRate, avgWin, avgLoss);
	}


	public List<Nifty5MinCandle> getCandlesForATR(
	        List<Nifty5MinCandle> previousDayCandles,
	        List<Nifty5MinCandle> currentDayCandles,
	        int breakoutIndex,
	        int atrPeriod) {

	    // We need period + 1 candles for ATR
	    int requiredCandles = atrPeriod + 1;
	    List<Nifty5MinCandle> atrCandles = new ArrayList<>();

	    // 1️⃣ Collect candles from current day before breakout candle (including previous one)
	    int startIndex = Math.max(0, breakoutIndex - requiredCandles);
	    for (int i = startIndex; i < breakoutIndex; i++) {
	        atrCandles.add(currentDayCandles.get(i));
	    }

	    // 2️⃣ If not enough, prepend from previous day's tail
	    int missing = requiredCandles - atrCandles.size();
	    if (missing > 0 && previousDayCandles != null && !previousDayCandles.isEmpty()) {
	        int prevStart = Math.max(0, previousDayCandles.size() - missing);
	        atrCandles.addAll(0, previousDayCandles.subList(prevStart, previousDayCandles.size()));
	    }

	    // 3️⃣ Ensure candles are sorted oldest → newest
	    atrCandles.sort(Comparator.comparing(Nifty5MinCandle::getTimestamp));

	    return atrCandles;
	}


}
