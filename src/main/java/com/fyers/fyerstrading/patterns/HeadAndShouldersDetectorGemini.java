package com.fyers.fyerstrading.patterns;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;

public class HeadAndShouldersDetectorGemini {
	public static boolean isPresent(BarSeries series) {
		int end = series.getEndIndex();
		if (end < 50)
			return false;

		HighPriceIndicator high = new HighPriceIndicator(series);
		VolumeIndicator volume = new VolumeIndicator(series);

		// 1. Define 3 segments of time (approx 10-15 days each)
		double p1 = getHighest(high, end - 40, end - 27); // Left Shoulder
		double p2 = getHighest(high, end - 26, end - 13); // Head
		double p3 = getHighest(high, end - 12, end); // Right Shoulder

		// 2. Strict Quantitative Rules
		boolean headIsHighest = (p2 > p1 * 1.01) && (p2 > p3 * 1.01);
		boolean shouldersLevel = Math.abs(p1 - p3) / p1 < 0.03; // Within 3% of each other

		// 3. Volume Divergence: Head volume should be lower than Left Shoulder
		double volLeft = getAvgVolume(volume, end - 40, end - 27);
		double volHead = getAvgVolume(volume, end - 26, end - 13);
		boolean volumeWeakening = volHead < volLeft;

		return headIsHighest && shouldersLevel && volumeWeakening;
	}

	private static double getHighest(HighPriceIndicator indicator, int start, int end) {
		double max = 0;
		for (int i = start; i <= end; i++) {
			max = Math.max(max, indicator.getValue(i).doubleValue());
		}
		return max;
	}

	private static double getAvgVolume(VolumeIndicator indicator, int start, int end) {
		double sum = 0;
		for (int i = start; i <= end; i++) {
			sum += indicator.getValue(i).doubleValue();
		}
		return sum / (end - start + 1);
	}
}
