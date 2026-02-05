package com.fyers.fyerstrading.utility;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.model.WeeklyCandle;

public class CandleConverter {

	public static List<WeeklyCandle> convertToWeekly(List<StockDailyPrice> stockDailyPrices) {
	    if (stockDailyPrices == null || stockDailyPrices.isEmpty()) return Collections.emptyList();

	    // Group by week starting Monday
	    Map<LocalDate, List<StockDailyPrice>> groupedByWeekStart = stockDailyPrices.stream()
	        .collect(Collectors.groupingBy(d -> getMondayOfWeek(d.getTradeDate())));

	    List<WeeklyCandle> weeklyCandles = new ArrayList<>();

	    for (Map.Entry<LocalDate, List<StockDailyPrice>> entry : groupedByWeekStart.entrySet()) {
	        LocalDate weekStart = entry.getKey();
	        List<StockDailyPrice> week = entry.getValue();
	        week.sort(Comparator.comparing(StockDailyPrice::getTradeDate));

	        WeeklyCandle wc = new WeeklyCandle();
	        wc.setSymbol(week.get(0).getStock().getSymbol());
	        wc.setWeekStarting(weekStart);  // <-- Changed here
	        wc.setOpen(week.get(0).getOpenPrice());
	        wc.setHigh(week.stream().mapToDouble(StockDailyPrice::getHighPrice).max().orElse(0));
	        wc.setLow(week.stream().mapToDouble(StockDailyPrice::getLowPrice).min().orElse(0));
	        wc.setClose(week.get(week.size() - 1).getClosePrice());
	        wc.setVolume(week.stream().mapToDouble(StockDailyPrice::getVolume).sum());

	        weeklyCandles.add(wc);
	    }

	    weeklyCandles.sort(Comparator.comparing(WeeklyCandle::getWeekStarting)); // <-- Sort by week start
	    return weeklyCandles;
	}
	
	
	private static LocalDate getMondayOfWeek(LocalDate date) {
	    return date.with(java.time.DayOfWeek.MONDAY);
	}


}
