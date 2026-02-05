package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.TradeSetupForFNO;
import com.fyers.fyerstrading.model.FnoBacktestResult;
import com.fyers.fyerstrading.model.MasterCandle;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.TradeSetupFNORepository;
import com.fyers.fyerstrading.service.common.CandleFetcher;

@Service
public class DailyConsolidationEmaBacktest {

	@Autowired
	private TradeSetupFNORepository setupRepo;

	@Autowired
	private StockDailyPriceRepository dailyRepo;

	private static final int LOOKAHEAD_DAYS = 6; // total days to check
	private static final int CONSOLIDATION_DAYS = 3; // 2 or 3 (change here)

	public void runBacktest() {

		List<TradeSetupForFNO> setups = setupRepo.findAll();
		List<FnoBacktestResult> results = new ArrayList<>();

		System.out.println("===== DAILY EMA9 CONSOLIDATION BACKTEST =====");
		System.out.println("Total setups: " + setups.size());
		System.out.println("-------------------------------------------");

		for (TradeSetupForFNO setup : setups) {

			FnoBacktestResult result = backtestSingleSetup(setup);
			results.add(result);

			System.out.println(setup.getStockSymbol() + " | SetupDate=" + setup.getTradeFoundDate() + " | Result="
					+ result.getExitReason() + " | PnL=" + result.getPnlPoints());
		}

		printSummary(results);
	}

	// =====================================================
	// SINGLE SETUP BACKTEST
	// =====================================================
	private FnoBacktestResult backtestSingleSetup(TradeSetupForFNO setup) {

		LocalDate startDate = setup.getTradeFoundDate().plusDays(1);
		LocalDate endDate = startDate.plusDays(LOOKAHEAD_DAYS+10);

		List<StockDailyPrice> days = dailyRepo.findAllBWDate(setup.getStockSymbol(), startDate, endDate);

		if (days.size() < CONSOLIDATION_DAYS + 1) {
			return noTrade(setup, "INSUFFICIENT_DATA");
		}

		// -----------------------------
		// 1️⃣ FIND CONSOLIDATION WINDOW
		// -----------------------------
		for (int i = 0; i <= days.size() - CONSOLIDATION_DAYS - 1; i++) {

			List<StockDailyPrice> consolidationDays = days.subList(i, i + CONSOLIDATION_DAYS);

			if (!isTightConsolidationAboveEMA(consolidationDays))
				continue;

			// -----------------------------
			// 2️⃣ ENTRY DAY (NEXT DAY)
			// -----------------------------
			StockDailyPrice entryDay = days.get(i + CONSOLIDATION_DAYS);

			double entryPrice = entryDay.getTechnicalIndicator().getEma9();

			// -----------------------------
			// 3️⃣ EXIT AFTER ENTRY
			// -----------------------------
			return simulateExit(setup, days.subList(i + CONSOLIDATION_DAYS, days.size()), entryPrice);
		}

		return noTrade(setup, "NO_CONSOLIDATION");
	}

	// =====================================================
	// CONSOLIDATION CHECK
	// =====================================================
	private boolean isTightConsolidationAboveEMA(List<StockDailyPrice> days) {

		double maxHigh = Double.MIN_VALUE;
		double minLow = Double.MAX_VALUE;
		double atrSum = 0;

		for (StockDailyPrice d : days) {

			double close = d.getClosePrice();
			double ema9 = d.getTechnicalIndicator().getEma9();
			double atr = d.getTechnicalIndicator().getAtr();

			// must stay above EMA9
			if (close <= ema9)
				return false;

			double range = d.getHighPrice() - d.getLowPrice();

			// low volatility day
			if (range > 0.8 * atr)
				return false;

			maxHigh = Math.max(maxHigh, d.getHighPrice());
			minLow = Math.min(minLow, d.getLowPrice());
			atrSum += atr;
		}

		double avgAtr = atrSum / days.size();

		// overall tight range
		return (maxHigh - minLow) <= 1.2 * avgAtr;
	}

	// =====================================================
	// EXIT LOGIC (DAILY)
	// =====================================================
	private FnoBacktestResult simulateExit(TradeSetupForFNO setup, List<StockDailyPrice> days, double entryPrice) {

		double sl = setup.getStopLoss();
		double t1 = setup.getTarget1();

		for (StockDailyPrice d : days) {

			if (d.getLowPrice() <= sl) {
				return buildResult(setup, entryPrice, sl, "SL");
			}

			if (d.getHighPrice() >= t1) {
				return buildResult(setup, entryPrice, t1, "T1");
			}
		}

		return buildResult(setup, entryPrice, entryPrice, "TIME_EXIT");
	}

	// =====================================================
	// RESULT HELPERS
	// =====================================================
	private FnoBacktestResult noTrade(TradeSetupForFNO setup, String reason) {

		FnoBacktestResult r = new FnoBacktestResult();
		r.setSymbol(setup.getStockSymbol());
		r.setSetupDate(setup.getTradeFoundDate());
		r.setExitReason(reason);
		r.setPnlPoints(0);
		return r;
	}

	private FnoBacktestResult buildResult(TradeSetupForFNO setup, double entry, double exit, String reason) {

		FnoBacktestResult r = new FnoBacktestResult();
		r.setSymbol(setup.getStockSymbol());
		r.setSetupDate(setup.getTradeFoundDate());
		r.setEntryPrice(round(entry));
		r.setExitPrice(round(exit));
		r.setExitReason(reason);
		r.setPnlPoints(round(exit - entry));
		return r;
	}

	private void printSummary(List<FnoBacktestResult> results) {

		long total = results.size();
		long wins = results.stream().filter(r -> "T1".equals(r.getExitReason())).count();
		long loss = results.stream().filter(r -> "SL".equals(r.getExitReason())).count();
		long time = results.stream().filter(r -> "TIME_EXIT".equals(r.getExitReason())).count();

		double avgPnl = results.stream().mapToDouble(FnoBacktestResult::getPnlPoints).average().orElse(0);

		System.out.println("========== SUMMARY ==========");
		System.out.println("Total setups : " + total);
		System.out.println("Wins (T1)    : " + wins);
		System.out.println("Losses (SL)  : " + loss);
		System.out.println("Time Exit    : " + time);
		System.out.println("Avg PnL     : " + round(avgPnl));
		System.out.println("=============================");
	}

	private double round(double v) {
		return Math.round(v * 100.0) / 100.0;
	}
}
