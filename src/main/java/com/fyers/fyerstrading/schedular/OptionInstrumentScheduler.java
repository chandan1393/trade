package com.fyers.fyerstrading.schedular;

import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.service.OptionInstrumentUpdateService;

@Service
public class OptionInstrumentScheduler {

	@Autowired
	private OptionInstrumentUpdateService updateService;

	// Runs every weekday morning
	@Scheduled(cron = "0 15 7 * * MON-FRI", zone = "Asia/Kolkata")
	public void runMonthlyOptionUpdate() {

		LocalDate today = LocalDate.now();

		if (isLastTradingDay(today)) {
			updateService.refreshOptionInstruments(today);
		}
	}

	// ---- Utility ----
	private boolean isLastTradingDay(LocalDate date) {

		LocalDate nextDay = date.plusDays(1);

		// Skip weekends
		while (nextDay.getDayOfWeek() == DayOfWeek.SATURDAY || nextDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
			nextDay = nextDay.plusDays(1);
		}

		// If next trading day is in next month → today is last trading day
		return nextDay.getMonth() != date.getMonth();
	}
}
