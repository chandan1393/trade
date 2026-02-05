package com.fyers.fyerstrading.patterns;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.statistics.SimpleLinearRegressionIndicator;

public class AscendingTriangleDetectorGemini {
    public static boolean isPresent(BarSeries series) {
        int window = 20;
        int end = series.getEndIndex();
        if (end < window) return false;

        // 1. Resistance: Slope of Highs should be nearly 0
        HighPriceIndicator highPrice = new HighPriceIndicator(series);
        SimpleLinearRegressionIndicator highSlope = new SimpleLinearRegressionIndicator(highPrice, window, SimpleLinearRegressionIndicator.SimpleLinearRegressionType.SLOPE);
        boolean flatTop = Math.abs(highSlope.getValue(end).doubleValue()) < 0.05;

        // 2. Support: Slope of Lows must be positive
        LowPriceIndicator lowPrice = new LowPriceIndicator(series);
        SimpleLinearRegressionIndicator lowSlope = new SimpleLinearRegressionIndicator(lowPrice, window, SimpleLinearRegressionIndicator.SimpleLinearRegressionType.SLOPE);
        boolean risingBottom = lowSlope.getValue(end).doubleValue() > 0.1;

        return flatTop && risingBottom;
    }
}
