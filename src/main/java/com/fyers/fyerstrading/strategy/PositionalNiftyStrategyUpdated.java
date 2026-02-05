package com.fyers.fyerstrading.strategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.entity.NiftyDailyCandle;
import com.fyers.fyerstrading.model.TradeMasterSignal;
import com.fyers.fyerstrading.service.common.CandleFetcher;

@Component
public class PositionalNiftyStrategyUpdated {

	@Autowired
    private final CandleFetcher priceService;

    public PositionalNiftyStrategyUpdated(CandleFetcher priceService) {
        this.priceService = priceService;
    }

    /** Backtest over a date range with single active trade at a time */
    public void backtest(String symbol, LocalDate startDate, LocalDate endDate) {
        // Fetch all daily candles in one go
        List<NiftyDailyCandle> dailyCandles = priceService.getNiftyDailyCandelBWDate(startDate.minusDays(50), endDate.plusDays(1));
        if (dailyCandles.isEmpty()) return;

        Map<LocalDate, NiftyDailyCandle> candleMap = new TreeMap<>();
        for (NiftyDailyCandle c : dailyCandles) candleMap.put(c.getTradeDate(), c);

        double totalPnL = 0.0;
        LocalDate date = startDate;

        TradeMasterSignal activeTrade = null;

        while (!date.isAfter(endDate)) {

            // If no active trade, check for a new signal
            if (activeTrade == null) {
                Optional<TradeMasterSignal> signalOpt = run(symbol, date, candleMap);
                if (signalOpt.isPresent()) {
                    activeTrade = signalOpt.get();
                    System.out.printf("%s | Trade opened: Entry: %.2f Direction: %s Reason: %s%n",
                            date, activeTrade.optionEntryPrice, activeTrade.direction, activeTrade.reason);
                }
            }

            // If there is an active trade, check for exit
            if (activeTrade != null) {
                NiftyDailyCandle todayCandle = candleMap.get(date);
                if (todayCandle != null) {
                    double entry = activeTrade.optionEntryPrice;
                    double stop = activeTrade.optionSL;
                    double target = activeTrade.optionTarget;
                    double exitPrice = entry;
                    boolean exitTrade = false;

                    if (activeTrade.direction == TradeMasterSignal.Direction.LONG) {
                        if (todayCandle.getLow() <= stop) {
                            exitPrice = stop;
                            exitTrade = true;
                        } else if (todayCandle.getHigh() >= target) {
                            exitPrice = target;
                            exitTrade = true;
                        } else {
                            exitPrice = todayCandle.getClose();
                        }
                    } else { // SHORT
                        if (todayCandle.getHigh() >= stop) {
                            exitPrice = stop;
                            exitTrade = true;
                        } else if (todayCandle.getLow() <= target) {
                            exitPrice = target;
                            exitTrade = true;
                        } else {
                            exitPrice = todayCandle.getClose();
                        }
                    }

                    if (exitTrade || date.equals(endDate)) {
                        double pnl = activeTrade.direction == TradeMasterSignal.Direction.LONG
                                ? exitPrice - entry
                                : entry - exitPrice;
                        totalPnL += pnl;
                        System.out.printf("%s | Trade closed: Entry: %.2f Exit: %.2f PnL: %.2f%n",
                                date, entry, exitPrice, pnl);

                        activeTrade = null; // ready for next trade
                    }
                }
            }

            date = date.plusDays(1);
        }

        System.out.printf("Total PnL over period: %.2f%n", totalPnL);
    }

    /** Run strategy for a given date using pre-fetched candle map */
    public Optional<TradeMasterSignal> run(String symbol, LocalDate date, Map<LocalDate, NiftyDailyCandle> candleMap) {
        NiftyDailyCandle today = candleMap.get(date);
        NiftyDailyCandle prev = candleMap.get(date.minusDays(1));
        if (today == null || prev == null) return Optional.empty();

        double ema21 = calculateEMA(candleMap, 21, date);
        double ema50 = calculateEMA(candleMap, 50, date);

        boolean upTrend = ema21 > ema50;
        boolean downTrend = ema21 < ema50;

        double recentHigh = prev.getHigh();
        double recentLow = prev.getLow();
        double spot = today.getClose();

        if (upTrend && spot > recentHigh) {
            return Optional.of(buildSignal(symbol, TradeMasterSignal.Direction.LONG, spot,
                    "Daily EMA21>EMA50 + breakout above swing high"));
        }

        if (downTrend && spot < recentLow) {
            return Optional.of(buildSignal(symbol, TradeMasterSignal.Direction.SHORT, spot,
                    "Daily EMA21<EMA50 + breakdown below swing low"));
        }

        return Optional.empty();
    }

    private TradeMasterSignal buildSignal(String symbol, TradeMasterSignal.Direction dir, double spot, String reason) {
        TradeMasterSignal s = new TradeMasterSignal();
        s.symbol = symbol;
        s.direction = dir;
        s.optionSymbol = null; // OI unavailable
        s.optionEntryPrice = spot;
        s.optionSL = dir == TradeMasterSignal.Direction.LONG ? spot * 0.95 : spot * 1.05;
        s.optionTarget = dir == TradeMasterSignal.Direction.LONG ? spot * 1.05 : spot * 0.95;
        s.qty = 1;
        s.underlyingAtSignal = spot;
        s.reason = reason;
        s.timestamp = LocalDateTime.now();
        return s;
    }

    /** Calculate EMA dynamically using closing prices from candleMap */
    public double calculateEMA(Map<LocalDate, NiftyDailyCandle> candleMap, int period, LocalDate date) {
        List<LocalDate> dates = candleMap.keySet().stream().sorted().toList();
        int idx = dates.indexOf(date);
        if (idx < period - 1) return Double.NaN; // not enough data

        double multiplier = 2.0 / (period + 1);
        // Start with SMA for first EMA
        double sma = 0.0;
        for (int i = idx - period + 1; i <= idx; i++) {
            sma += candleMap.get(dates.get(i)).getClose();
        }
        sma /= period;

        double ema = sma;
        // Calculate EMA for remaining days up to 'date'
        for (int i = idx - period + 2; i <= idx; i++) {
            double close = candleMap.get(dates.get(i)).getClose();
            ema = (close - ema) * multiplier + ema;
        }
        return ema;
    }
}
