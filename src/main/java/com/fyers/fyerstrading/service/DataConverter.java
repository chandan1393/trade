package com.fyers.fyerstrading.service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;

import com.fyers.fyerstrading.entity.StockDailyPrice;

public class DataConverter {

	public static BarSeries convertToSeries(String symbol, List<StockDailyPrice> data) {

		BarSeries series = new BaseBarSeriesBuilder().withName(symbol)
				.withNumTypeOf(org.ta4j.core.num.DoubleNum::valueOf).build();
		for (StockDailyPrice price : data) {

			ZonedDateTime endTime = price.getTradeDate().atTime(15, 30).atZone(ZoneId.of("Asia/Kolkata"));

			BaseBar bar = new BaseBar(Duration.ofDays(1), endTime, price.getOpenPrice(), price.getHighPrice(),
					price.getLowPrice(), price.getClosePrice(), price.getVolume());

			series.addBar(bar);
		}

		return series;
	}
}
