package com.fyers.fyerstrading.schedular;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.entity.IndexOIData;
import com.fyers.fyerstrading.model.Level;
import com.fyers.fyerstrading.repo.IndexOIRepository;
import com.fyers.fyerstrading.service.IndexOIAnalyzer;
import com.fyers.fyerstrading.service.IndexOILevelDetector;
import com.fyers.fyerstrading.service.LivePriceStore;

@Component
public class IntradayOIAnalyzerScheduler {

	@Autowired
	private IndexOIAnalyzer analyzer;
	@Autowired
	private IndexOILevelDetector levelDetector;
	@Autowired
	private IndexOIRepository oiDataRepository;
	@Autowired
	private LivePriceStore livePriceStore;

	private static final String SYMBOL = "NSE:NIFTY50-INDEX";

	@Scheduled(cron = "5 */3 9-15 * * MON-FRI", zone = "Asia/Kolkata")
	public void checkIntradayOi() {

		try {
			LocalDateTime now = LocalDateTime.now();

			// 1) Find latest slot
			LocalDateTime t1 = oiDataRepository.findLatestTimestamp(SYMBOL, now);
			if (t1 == null)
				return;

			// 2) Find previous slot
			LocalDateTime t0 = oiDataRepository.findPreviousTimestamp(SYMBOL, t1);
			if (t0 == null)
				return;

			// 3) Load both
			List<IndexOIData> current = oiDataRepository.findBySymbolAndTimestamp(SYMBOL, t1);
			List<IndexOIData> previous = oiDataRepository.findBySymbolAndTimestamp(SYMBOL, t0);

			if (current.isEmpty() || previous.isEmpty())
				return;

			// 4) Analyze
			String signal = analyzer.analyze(current, previous);

			// 5) Levels
			double spot = fetchNiftySpot();
			Level level = levelDetector.detectBaseOrResistance(current, spot);

			System.out.println(t1 + " → Signal=" + signal);
			if (level != null) {
				System.out.println("Level: " + level.type + " at " + level.strike + " strength=" + level.strength);
			}

			// tradeEngine.process(signal, level);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private double fetchNiftySpot() {
		Double niftyPrice = livePriceStore.getPrice("NSE:NIFTY50-INDEX");
		return niftyPrice;
	}
}
