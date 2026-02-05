package com.fyers.fyerstrading.service.common;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.model.MasterCandle;

@Service
public class IndicatorCalculator {

    // ------------------ EMA ------------------
    public double ema(List<MasterCandle> list, int period) {
        if (list == null || list.size() < period) return 0;

        double sma = 0;
        for (int i = list.size() - period; i < list.size(); i++)
            sma += list.get(i).getClose();
        sma /= period;

        double k = 2.0 / (period + 1);
        double ema = sma;

        for (int i = list.size() - period + 1; i < list.size(); i++) {
            double c = list.get(i).getClose();
            ema = c * k + ema * (1 - k);
        }
        return ema;
    }

    // ------------------ RSI ------------------
    public double rsi(List<MasterCandle> list, int period) {
        if (list == null || list.size() <= period) return 50;

        double gain = 0, loss = 0;
        for (int i = list.size() - period; i < list.size(); i++) {
            double diff = list.get(i).getClose() - list.get(i - 1).getClose();
            if (diff > 0) gain += diff;
            else loss -= diff;
        }

        double avgGain = gain / period;
        double avgLoss = (loss == 0) ? 0.00001 : loss / period;

        double rs = avgGain / avgLoss;
        return 100 - (100 / (1 + rs));
    }

    // ------------------ VWAP ------------------
    public double vwap(List<MasterCandle> list) {
        double pv = 0;
        double vol = 0;
        for (MasterCandle c : list) {
            double typical = (c.getHigh() + c.getLow() + c.getClose()) / 3.0;
            pv += typical * c.getVolume();
            vol += c.getVolume();
        }
        return vol == 0 ? list.get(list.size() - 1).getClose() : pv / vol;
    }
    
 // ------------------ ATR ------------------
    public double atr(List<MasterCandle> list, int period) {
        if (list == null || list.size() < period + 1) return 0;

        int size = list.size();

        // Step 1: compute TR for each candle in the last "period" range
        double trSum = 0; 
        for (int i = size - period; i < size; i++) {
            MasterCandle current = list.get(i);
            MasterCandle prev = list.get(i - 1);

            double high = current.getHigh();
            double low = current.getLow();
            double prevClose = prev.getClose();

            double tr = Math.max(high - low,
                         Math.max(Math.abs(high - prevClose),
                                  Math.abs(low - prevClose)));

            trSum += tr;
        }

        // Step 2: Initial ATR = SMA of TR
        double atr = trSum / period;

        // Step 3: Wilder ATR smoothing for remaining candles (same style as EMA)
        for (int i = size - period + 1; i < size; i++) {
            MasterCandle current = list.get(i);
            MasterCandle prev = list.get(i - 1);

            double high = current.getHigh();
            double low = current.getLow();
            double prevClose = prev.getClose();

            double tr = Math.max(high - low,
                         Math.max(Math.abs(high - prevClose),
                                  Math.abs(low - prevClose)));

            atr = ((atr * (period - 1)) + tr) / period;
        }

        return atr;
    }

    // ------------------ Volume Avg ------------------
    public double avgVolume(List<MasterCandle> list, int n) {
        if (list.size() < n) return 0;
        long sum = 0;
        for (int i = list.size() - n; i < list.size(); i++)
            sum += list.get(i).getVolume();
        return sum * 1.0 / n;
    }

    // ------------------ Candle Strength ------------------
    public double candleStrength(MasterCandle c) {
        double body = Math.abs(c.getClose() - c.getOpen());
        double range = c.getHigh() - c.getLow();
        if (range <= 0) return 0;
        return body / range;
    }
}
