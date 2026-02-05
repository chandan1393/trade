package com.fyers.fyerstrading.patterns;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;

public class BullishRectangleDetectorGemini {
    public static boolean isPresent(BarSeries series) {
        int window = 20;
        int end = series.getEndIndex();
        if (end < window) return false;

        HighPriceIndicator highIndicator = new HighPriceIndicator(series);
        LowPriceIndicator lowIndicator = new LowPriceIndicator(series);

        double maxHigh = 0;
        double minLow = Double.MAX_VALUE;

        for (int i = end - window; i <= end; i++) {
            maxHigh = Math.max(maxHigh, highIndicator.getValue(i).doubleValue());
            minLow = Math.min(minLow, lowIndicator.getValue(i).doubleValue());
        }

        // STRICT FILTER: The "box" height must be less than 1.5% of the price
        double boxHeightPct = (maxHigh - minLow) / minLow;
        
        // VOLUME FILTER: Volume should be declining during the consolidation
        SMAIndicator avgVol = new SMAIndicator(new VolumeIndicator(series), 20);
        boolean lowVolume = series.getLastBar().getVolume().doubleValue() < avgVol.getValue(end).doubleValue();

        return boxHeightPct < 0.015 && lowVolume;
    }
}
