package com.fyers.fyerstrading.patterns;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.StockTechnicalIndicator;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;

@Service
public class ImpulseStrengthPatternDetector {

	public List<LocalDate> backtestLastOneYear(List<StockDailyPrice> data, String stockName) {

		List<LocalDate> patternDates = new ArrayList<>();

		if (data == null || data.size() < 100) {
			return patternDates;
		}

		StockDailyPrice last = data.get(data.size() - 1);
		if (last == null || last.getTradeDate() == null) {
			return patternDates;
		}

		LocalDate cutoff = last.getTradeDate().minusYears(1);

		for (int i = 0; i < data.size() - 3; i++) {

			StockDailyPrice candle = data.get(i);
			if (candle == null || candle.getTradeDate() == null) {
				continue;
			}

			// Only last 1 year
			if (candle.getTradeDate().isBefore(cutoff)) {
				continue;
			}

			if (isPatternAtIndex(data, i)) {
				// ✅ impulse candle date (big candle)
				patternDates.add(candle.getTradeDate());
			}
		}

		// ----------- Logging (Impulse Candle Dates Only) -----------
		System.out.println("Stock name: " + stockName);
		patternDates.forEach(d -> System.out.println("Impulse candle date: " + d));

		return patternDates;
	}

	// =====================================================
	// Core pattern logic (NULL SAFE)
	// =====================================================

	private static boolean isPatternAtIndex(List<StockDailyPrice> data, int i) {

		if (data == null || i < 20 || i + 3 >= data.size()) {
			return false;
		}

		StockDailyPrice impulse = data.get(i);
		if (impulse == null)
			return false;

		Double closePrice = impulse.getClosePrice();
		Double openPrice = impulse.getOpenPrice();
		Double volume = impulse.getVolume();
		StockTechnicalIndicator ti = impulse.getTechnicalIndicator();

		if (closePrice == null || openPrice == null || volume == null || ti == null || ti.getEma21() == null) {
			return false;
		}

		// 1) Big green candle
		if (closePrice <= openPrice)
			return false;

		// 2) Above EMA21
		 if (!(closePrice > ti.getEma9() && ti.getEma9() > ti.getEma21())) {
		        return false;
		    }

		// 3) Huge volume vs last 20 days
		double avgVol = averageVolume(data, i, 20);
		if (avgVol <= 0 || volume < 4 * avgVol)
			return false;

		// 4) Next 3 days must not close below breakout candle close
		for (int j = i + 1; j <= i + 3; j++) {

			StockDailyPrice next = data.get(j);
			if (next == null || next.getClosePrice() == null)
				return false;

			if (next.getClosePrice() < closePrice) {
				return false;
			}
		}

		return true;
	}

	// =====================================================
	// NULL SAFE VOLUME AVERAGE
	// =====================================================

	private static double averageVolume(List<StockDailyPrice> data, int endIndex, int lookback) {

		if (data == null || data.isEmpty())
			return 0;

		int start = Math.max(0, endIndex - lookback);
		double sum = 0;
		int count = 0;

		for (int i = start; i < endIndex; i++) {

			StockDailyPrice d = data.get(i);
			if (d == null || d.getVolume() == null)
				continue;

			sum += d.getVolume();
			count++;
		}

		return count == 0 ? 0 : sum / count;
	}
}
