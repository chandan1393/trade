package com.fyers.fyerstrading.model;

import java.time.LocalDate;
import java.util.List;

public class CandleContext {

	public final LocalDate today;
	public final LocalDate prevDay;

	public final List<MasterCandle> todayCandles;
	public final List<MasterCandle> prevDayCandles;

	public final MasterCandle latestCandle;

	public CandleContext(LocalDate today, LocalDate prevDay, List<MasterCandle> todayCandles,
			List<MasterCandle> prevDayCandles) {
		this.today = today;
		this.prevDay = prevDay;
		this.todayCandles = todayCandles;
		this.prevDayCandles = prevDayCandles;
		this.latestCandle = todayCandles.get(todayCandles.size() - 1);
	}
}
