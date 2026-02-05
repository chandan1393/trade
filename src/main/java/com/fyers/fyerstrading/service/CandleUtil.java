package com.fyers.fyerstrading.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fyers.fyerstrading.entity.FNO5MinCandle;

public class CandleUtil {

    
    public static List<FNO5MinCandle> toHigherTF(List<FNO5MinCandle> candles, int tfMinutes) {
        if (candles == null || candles.isEmpty()) return Collections.emptyList();

        List<FNO5MinCandle> result = new ArrayList<>();
        int groupSize = tfMinutes / 5;  // since source is 5-min data

        for (int i = 0; i < candles.size(); i += groupSize) {
            List<FNO5MinCandle> group = candles.subList(i, Math.min(i + groupSize, candles.size()));
            if (group.isEmpty()) continue;

            FNO5MinCandle first = group.get(0);
            FNO5MinCandle last = group.get(group.size() - 1);

            double open = first.getOpen();
            double high = group.stream().mapToDouble(FNO5MinCandle::getHigh).max().orElse(open);
            double low = group.stream().mapToDouble(FNO5MinCandle::getLow).min().orElse(open);
            double close = last.getClose();
            double volume = group.stream().mapToDouble(FNO5MinCandle::getVolume).sum();

            result.add(new FNO5MinCandle(
                    first.getSymbol(),
                    first.getTimestamp(),  // start time of block
                    open, high, low, close, volume
            ));
        }

        return result;
    }
}

