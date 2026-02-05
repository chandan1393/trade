package com.fyers.fyerstrading.schedular;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.entity.WeeklyBreakoutSetup;
import com.fyers.fyerstrading.model.BreakoutSetupResult;
import com.fyers.fyerstrading.model.WeeklyCandle;
import com.fyers.fyerstrading.repo.BreakoutSetupRepository;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.repo.WeeklyBreakOutBacktestResultRepository;
import com.fyers.fyerstrading.service.TelegramService;
import com.fyers.fyerstrading.service.WeeklyBreakoutBacktester;
import com.fyers.fyerstrading.utility.CandleConverter;

@Service
public class WeeklyBreakOutSchedular {

	@Autowired
	StockMasterRepository masterRepo;

	@Autowired
	StockDailyPriceRepository dailyPriceRepository;

	@Autowired
	private WeeklyBreakOutBacktestResultRepository backtestResultRepo;

	@Autowired
	private BreakoutSetupRepository breakoutSetupRepo;

	@Autowired
	WeeklyBreakoutBacktester backtest;
	
	@Autowired
	TelegramService telegramService;
	
	@Scheduled(cron = "0 0 20 ? * SUN") // Runs every Sunday at 8 PM
	public void scanWeeklyBreakoutSetups() {
		
		String schedulerName = "scanWeeklyBreakoutSetups";
		try {
			List<StockMaster> allStocks = masterRepo.findAll();
			List<WeeklyBreakoutSetup> newSetups = new ArrayList<>();

			for (StockMaster stock : allStocks) {
				List<StockDailyPrice> dailyData = dailyPriceRepository.findAfterDate(
					stock.getSymbol(), 
					LocalDate.now().minusMonths(6) // Last 6 months is enough
				);

				List<WeeklyCandle> weeklyCandles = CandleConverter.convertToWeekly(dailyData);

				List<BreakoutSetupResult> setups = backtest.findMonthlyBreakoutSetups(weeklyCandles, stock.getSymbol());

				for (BreakoutSetupResult wc : setups) {
					// Avoid duplicate setups
					boolean exists = breakoutSetupRepo.existsBySymbolAndWeekStarting(stock.getSymbol(), wc.getWeekStart());
					if (exists) continue;

					WeeklyBreakoutSetup entity = new WeeklyBreakoutSetup();
					entity.setSymbol(stock.getSymbol());
					entity.setWeekStarting(wc.getWeekStart());
					entity.setOpen(wc.getOpen());
					entity.setHigh(wc.getHigh());
					entity.setLow(wc.getLow());
					entity.setClose(wc.getClose());
					entity.setVolume(wc.getVolume());
					entity.setAvgVolume(wc.getAvgVolume());
					entity.setBodyPercent(wc.getBodyPercent());
					entity.setUpperWickPercent(wc.getUpperWickPercent());

					newSetups.add(entity);
				}
			}

			if (!newSetups.isEmpty()) {
				breakoutSetupRepo.saveAll(newSetups);
				StringBuilder msg = new StringBuilder("📈 *New Weekly Breakout Setups*\n");
				for (WeeklyBreakoutSetup s : newSetups) {
					msg.append("\n")
					   .append("🔹 ").append(s.getSymbol())
					   .append(" | Week: ").append(s.getWeekStarting())
					   .append(" | Close: ").append(s.getClose())
					   .append(" | Vol: ").append(s.getVolume());
				}
				try {
					telegramService.sendMessage(msg.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println("New weekly breakout setups saved: " + newSetups.size());
			} else {
				System.out.println("No new breakout setups this week.");
			}
		} catch (Exception e) {
			System.err.println("❌ Exception in scheduler [" + schedulerName + "]: " + e.getMessage());
		}
		
		
		
	}

}
