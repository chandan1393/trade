package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.entity.StockTechnicalIndicator;
import com.fyers.fyerstrading.entity.TradeSetupForFNO;
import com.fyers.fyerstrading.entity.TradeStatus;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.repo.TradeSetupFNORepository;

@Service
public class ImpulseConsolidationBreakoutFNOService {

	@Autowired
	private StockMasterRepository stockMasterRepository;

	@Autowired
	private StockDailyPriceRepository dailyRepo;

	@Autowired
	private TradeSetupFNORepository tradeSetupRepo;

	private static final int CONS_MIN = 2;
	private static final int CONS_MAX = 3;

	public void scanAndSaveSetupsForLastOneYear() {

		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusMonths(3);

		List<StockMaster> stocks = stockMasterRepository.findByIsInFnoTrue();

		System.out.println("Scanning ICB setups from " + startDate + " to " + endDate);

		for (StockMaster stock : stocks) {

			// 1️⃣ Fetch FULL 1 year data ONCE
			List<StockDailyPrice> data = dailyRepo.findBTWDateWithIndicators(stock.getSymbol(), startDate.minusDays(5), // buffer
																														// for
																														// impulse
					endDate.plusDays(1));

			if (data == null || data.size() < 50)
				continue;

			// Sort safety
			data.sort(Comparator.comparing(StockDailyPrice::getTradeDate));

			// 2️⃣ Scan EACH DAY as potential breakout day
			for (StockDailyPrice d : data) {

				LocalDate tradeDate = d.getTradeDate();

				if (tradeDate.isBefore(startDate) || tradeDate.isAfter(endDate))
					continue;

				// skip holidays / missing days
				if (dailyRepo.countByTradeDate(tradeDate) == 0)
					continue;

				/*
				 * // duplicate protection if
				 * (tradeSetupRepo.existsByStockSymbolAndTradeFoundDate( stock.getSymbol(),
				 * tradeDate)) continue;
				 */

				TradeSetupForFNO setup = findSetup(stock.getSymbol(), data, tradeDate);

				if (setup != null) {
					tradeSetupRepo.save(setup);
					System.out.println("✔ ICB SETUP | " + stock.getSymbol() + " | Date=" + tradeDate);
				}
			}
		}

		System.out.println("ICB scan completed for last 1 year");
	}

	private TradeSetupForFNO findSetup(
	        String symbol,
	        List<StockDailyPrice> data,
	        LocalDate tradeDate) {

	    final int LOOKBACK = 20;
	    final int PAUSE_MIN_DAYS = 2;
	    final int PAUSE_MAX_DAYS = 4;

	    for (int i = LOOKBACK; i < data.size() - PAUSE_MAX_DAYS; i++) {

	        StockDailyPrice expansion = data.get(i);

	        if (!expansion.getTradeDate().isBefore(tradeDate))
	            continue;

	        // -----------------------------
	        // 1️⃣ EXPANSION CHECK
	        // -----------------------------
	        double avgRange = averageRange(data, i, LOOKBACK);
	        double dayRange = expansion.getHighPrice() - expansion.getLowPrice();

	        double avgVol = averageVolume(data, i, LOOKBACK);

	        boolean rangeExpansion = dayRange >= 1.5 * avgRange;
	        boolean volumeExpansion = expansion.getVolume() >= 3.0 * avgVol;

	        if (!(rangeExpansion || volumeExpansion))
	            continue;

	        double boxHigh = expansion.getHighPrice();
	        double boxLow = expansion.getLowPrice();

	        // -----------------------------
	        // 2️⃣ PAUSE CHECK
	        // -----------------------------
	        int pauseDays = 0;

	        for (int j = i + 1; j < data.size(); j++) {

	            StockDailyPrice d = data.get(j);

	            if (d.getTradeDate().isAfter(tradeDate))
	                break;

	            double r = d.getHighPrice() - d.getLowPrice();

	            // must stay inside expansion range
	            if (d.getHighPrice() > boxHigh || d.getLowPrice() < boxLow)
	                break;

	            // must be tight
	            if (r > 0.7 * avgRange)
	                break;

	            pauseDays++;
	        }

	        if (pauseDays < PAUSE_MIN_DAYS)
	            continue;

	        // -----------------------------
	        // 3️⃣ MARK SETUP
	        // -----------------------------
	        if (!data.get(i + pauseDays).getTradeDate().isEqual(tradeDate))
	            continue;

	        TradeSetupForFNO setup = new TradeSetupForFNO();
	        setup.setStockSymbol(symbol);
	        setup.setTradeFoundDate(tradeDate);
	        setup.setTradeStatus(TradeStatus.SETUP_FOUND);
	        setup.setTradeEntered(false);
	        setup.setIsActive(true);

	        setup.setNotes("EXPANSION → PAUSE (REPEATING CORE STRUCTURE)");

	        return setup;
	    }

	    return null;
	}


	private double round(double v) {
		return Math.round(v * 100.0) / 100.0;
	}

	private double averageVolume(List<StockDailyPrice> data, int end, int lookback) {
	    int start = Math.max(0, end - lookback);
	    return data.subList(start, end).stream()
	            .mapToDouble(StockDailyPrice::getVolume)
	            .average().orElse(0);
	}

	private double averageRange(List<StockDailyPrice> data, int end, int lookback) {
	    int start = Math.max(0, end - lookback);
	    return data.subList(start, end).stream()
	            .mapToDouble(d -> d.getHighPrice() - d.getLowPrice())
	            .average().orElse(0);
	}

}
