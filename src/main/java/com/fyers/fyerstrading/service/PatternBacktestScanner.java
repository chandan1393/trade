package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;

import com.fyers.fyerstrading.entity.PatternDetection;
import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.patterns.AscendingTriangleDetectorGemini;
import com.fyers.fyerstrading.patterns.BullishRectangleDetectorGemini;
import com.fyers.fyerstrading.patterns.HeadAndShouldersDetectorGemini;
import com.fyers.fyerstrading.repo.PatternDetectionRepository;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;

@Service
public class PatternBacktestScanner {

	@Autowired
	private PatternDetectionRepository detectionRepository;

	@Autowired
	private StockDailyPriceRepository priceRepository;

	@Async("taskExecutor") // Uses a thread pool to manage memory
	public void processAllStocksAsync(List<StockMaster> stocks) {
		System.out.println("Starting Global Backtest for " + stocks.size() + " stocks...");

		for (StockMaster master : stocks) {
			try {
				String symbol = master.getSymbol();

				// 1. Fetch 5 years of data for THIS specific stock
				List<StockDailyPrice> dbPrices = priceRepository.findBTWDateWithIndicators(symbol,
						LocalDate.now().minusYears(2), LocalDate.now());

				if (dbPrices.size() < 250) {
					continue; // Skip stocks with low history
				}

				// 2. Convert to TA4J
				BarSeries series = DataConverter.convertToSeries(symbol, dbPrices);

				// 3. Run the historical scan (the method we built previously)
				this.runHistoricalScanner(series, symbol);

				System.out.println("Completed scan for: " + symbol);

				// 4. Clear memory to prevent OutOfMemoryError
				// If using JPA, you might want to call entityManager.clear() here

			} catch (Exception e) {
				System.err.println("Error processing " + master.getSymbol() + ": " + e.getMessage());
			}
		}
		System.out.println("--- ALL STOCKS PROCESSED ---");
	}

	public void runHistoricalScanner(BarSeries series, String symbol) {

		// Start from 200 to ensure SMA200 is ready
		for (int i = 200; i < series.getBarCount(); i++) {

			// Snapshot of history till i
			BarSeries window = series.getSubSeries(0, i + 1);
			int end = window.getEndIndex();

			ClosePriceIndicator closePrice = new ClosePriceIndicator(window);
			SMAIndicator sma200 = new SMAIndicator(closePrice, 200);
			VolumeIndicator volIndicator = new VolumeIndicator(window);
			SMAIndicator avgVol20 = new SMAIndicator(volIndicator, 20);

			double currentPrice = closePrice.getValue(end).doubleValue();
			double currentVol = volIndicator.getValue(end).doubleValue();
			double avgVol = avgVol20.getValue(end).doubleValue();
			double ratio = currentVol / avgVol;
			double smaVal = sma200.getValue(end).doubleValue();

			// Trend + Volume filter
			if (currentPrice > smaVal && ratio >= 1.5) {

				String found = null;
				if (HeadAndShouldersDetectorGemini.isPresent(window))
					found = "H&S";
				else if (BullishRectangleDetectorGemini.isPresent(window))
					found = "RECT";
				else if (AscendingTriangleDetectorGemini.isPresent(window))
					found = "TRI";

				if (found != null) {
					LocalDate detectionDate = window.getBar(end).getEndTime().toLocalDate();

					saveToDb(symbol, detectionDate, found, currentPrice, currentVol, ratio, smaVal);
				}
			}
		}
	}

	private void saveToDb(String symbol, LocalDate date, String type, Double price, Double vol, Double ratio,
			Double sma) {
		PatternDetection det = new PatternDetection();
		det.setSymbol(symbol);
		det.setDetectionDate(date);
		det.setPatternType(type);
		det.setPriceAtDetection(price);
		det.setVolumeAtDetection(vol);
		det.setVolumeRatio(ratio);
		det.setSma200Value(sma);

		detectionRepository.save(det);
		System.out.println("Saved discovery: " + type + " for " + symbol + " on " + date);
	}

}
