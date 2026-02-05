package com.fyers.fyerstrading.restcontroller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.entity.IndexOIData;
import com.fyers.fyerstrading.entity.NiftyDailyCandle;
import com.fyers.fyerstrading.model.DailyLevels;
import com.fyers.fyerstrading.model.DailyOiSummary;
import com.fyers.fyerstrading.model.WeeklyOiSummary;
import com.fyers.fyerstrading.repo.IndexOIRepository;
import com.fyers.fyerstrading.repo.NiftyDailyCandleRepo;
import com.fyers.fyerstrading.service.DailyIndexOIAnalyzer;
import com.fyers.fyerstrading.service.WeeklyOiAnalyzer;

@RestController
@RequestMapping("/api/oi")
public class OIAnalyzerController {

	@Autowired
	private IndexOIRepository oiRepo;

	@Autowired
	private WeeklyOiAnalyzer weeklyOiAnalyzer;

	@Autowired
	private DailyIndexOIAnalyzer dailyOiAnalyzer;

	@Autowired
	private NiftyDailyCandleRepo niftyDailyRepo;

	/*
	 * ========================= WEEKLY (ROLLING) ANALYSIS =========================
	 */

	/**
	 * Example: GET /api/oi/weekly?date=2026-01-22
	 */
	@GetMapping("/weekly")
	public WeeklyOiSummary runWeeklyOiAnalysis(@RequestParam String date) {

		LocalDate tradeDate = LocalDate.parse(date);
		LocalDateTime now = tradeDate.atTime(15, 30);

		// ✅ Rolling 5 trading days start
		LocalDateTime start = getRollingStartFromDb("NSE:NIFTY50-INDEX", 5, now);

		List<IndexOIData> weekData = oiRepo.findBySymbolAndDateBetween("NSE:NIFTY50-INDEX", start, now);

		if (weekData == null || weekData.isEmpty()) {
			throw new RuntimeException("No OI data found for rolling window");
		}

		// NOTE: weeklyHigh / weeklyLow should come from index candles
		double weeklyHigh = 0; // plug candle data here
		double weeklyLow = 0;

		return weeklyOiAnalyzer.analyzeWeek(weekData, weeklyHigh, weeklyLow, null);
	}

	/*
	 * ========================= DAILY SUPPORT / RESISTANCE
	 * =========================
	 */

	/**
	 * Example: GET /api/oi/daily-levels?date=2026-01-22
	 */
	@GetMapping("/daily-levels")
	public DailyLevels getDailyLevels(@RequestParam String date) {

		LocalDate tradeDate = LocalDate.parse(date);

		LocalDateTime start = tradeDate.atTime(9, 15);
		LocalDateTime end = tradeDate.atTime(15, 30);

		List<IndexOIData> dayData = oiRepo.findBySymbolAndDateBetween("NSE:NIFTY50-INDEX", start, end);

		if (dayData == null || dayData.isEmpty()) {
			throw new RuntimeException("No OI data for " + date);
		}

		return dailyOiAnalyzer.findDailyLevels(dayData);
	}

	/*
	 * ========================= DAILY FULL TRADE PLAN =========================
	 */

	/**
	 * Example: GET /api/oi/daily-plan?date=2026-01-22
	 */
	@GetMapping("/daily-plan")
	public DailyOiSummary getDailyTradePlan(@RequestParam String date) {

		LocalDate tradeDate = LocalDate.parse(date);

		LocalDateTime start = tradeDate.atTime(9, 15);
		LocalDateTime end = tradeDate.atTime(15, 30);

		List<IndexOIData> dayData = oiRepo.findBySymbolAndDateBetween("NSE:NIFTY50-INDEX", start, end);

		if (dayData == null || dayData.isEmpty()) {
			throw new RuntimeException("No OI data for " + date);
		}

		// Rolling weekly context
		LocalDateTime rollingStart = getRollingStartFromDb("NSE:NIFTY50-INDEX", 5, end);

		List<IndexOIData> rollingWeek = oiRepo.findBySymbolAndDateBetween("NSE:NIFTY50-INDEX", rollingStart, end);

		WeeklyOiSummary weekly = weeklyOiAnalyzer.analyzeWeek(rollingWeek, 0, 0, null);

		NiftyDailyCandle niftyDailyCandel = niftyDailyRepo.findRecordOnDate(tradeDate);
		// Day OHLC should come from candle table
		double dayHigh = niftyDailyCandel.getHigh();
		double dayLow = niftyDailyCandel.getLow();
		double dayClose = niftyDailyCandel.getClose();

		return dailyOiAnalyzer.analyzeDay(dayData, weekly, dayHigh, dayLow, dayClose);
	}

	/*
	 * ========================= ROLLING START (DB DRIVEN) =========================
	 */

	public LocalDateTime getRollingStartFromDb(String symbol, int tradingDays, LocalDateTime now) {

		List<LocalDate> dates = oiRepo.findLastTradingDatesRaw(symbol, now, PageRequest.of(0, tradingDays)).stream()
				.map(java.sql.Date::toLocalDate).toList();

		if (dates.isEmpty()) {
			throw new RuntimeException("No trading dates found");
		}

		LocalDate startDate = dates.stream().min(LocalDate::compareTo).get();

		return startDate.atTime(9, 15);
	}

}
