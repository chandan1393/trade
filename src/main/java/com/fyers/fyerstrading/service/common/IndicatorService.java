package com.fyers.fyerstrading.service.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.model.MasterCandle;

@Service
public class IndicatorService {

    @Autowired CandleFetcher candleFetcher;
    @Autowired IndicatorCalculator calc;

    // ---------- EMA ----------
    public double getEMA(String symbol, String timeframe, int period) {
        List<MasterCandle> list = candleFetcher.getLastNCandles(symbol, timeframe, period + 20);
        return calc.ema(list, period);
    }

    // ---------- RSI ----------
    public double getRSI(String symbol, String timeframe, int period) {
        List<MasterCandle> list = candleFetcher.getLastNCandles(symbol, timeframe, period + 20);
        return calc.rsi(list, period);
    }

    // ---------- VWAP ----------
    public double getVWAP(String symbol, String timeframe) {
        List<MasterCandle> list = candleFetcher.getLastNCandles(symbol, timeframe, 50);
        return calc.vwap(list);
    }

    public double getAvgVolume(String symbol, String timeframe, int n) {
        List<MasterCandle> list = candleFetcher.getLastNCandles(symbol, timeframe, n + 10);
        return calc.avgVolume(list, n);
    }

    // ---------- Trend Conditions ----------
    public boolean isBullishTrend(String symbol, String tf) {
        double ema20 = getEMA(symbol, tf, 20);
        double ema50 = getEMA(symbol, tf, 50);
        double price = candleFetcher.getLTP(symbol);
        return price > ema20 && ema20 > ema50;
    }

    public boolean isBearishTrend(String symbol, String tf) {
        double ema20 = getEMA(symbol, tf, 20);
        double ema50 = getEMA(symbol, tf, 50);
        double price = candleFetcher.getLTP(symbol);
        return price < ema20 && ema20 < ema50;
    }

    public boolean rsiPass(String symbol, String tf, boolean bullish) {
        double rsi = getRSI(symbol, tf, 14);
        return bullish ? rsi > 55 : rsi < 45;
    }

    public boolean aboveVWAP(String symbol, String tf) {
        double price = candleFetcher.getLTP(symbol);
        return price > getVWAP(symbol, tf);
    }

    public boolean belowVWAP(String symbol, String tf) {
        double price = candleFetcher.getLTP(symbol);
        return price < getVWAP(symbol, tf);
    }
}
