package com.fyers.fyerstrading.service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.StockTechnicalIndicator;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.utility.TradingUtil;
import com.fyers.fyerstrading.utility.Util;
import com.tts.in.model.StockHistoryModel;

@Service
public class HelperClassForCalculation {

	@Autowired
	StockDailyPriceRepository dailyPriceRepository;
	
	public StockHistoryModel prepareRequestModel(String symbol, String timeframe, LocalDate startDate,
			LocalDate endDate) {
		StockHistoryModel model = new StockHistoryModel();
		model.Symbol = symbol;
		model.Resolution = Util.convertTimeframeToResolution(timeframe);
		model.DateFormat = "1";
		model.RangeFrom = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		model.RangeTo = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		model.ContFlag = 0;
		return model;
	}

	public StockTechnicalIndicator calculateIndicators(StockDailyPrice current, StockTechnicalIndicator lastIndicator) {
	    StockTechnicalIndicator indicator = new StockTechnicalIndicator();
	    indicator.setStockDailyPrice(current); // Link to the daily price record
	    indicator.setTradeDate(current.getTradeDate());

	    if (lastIndicator != null) {
	        double prevClosePrice = lastIndicator.getStockDailyPrice().getClosePrice();

	        // ✅ EMA calculations with rounding
	        indicator.setEma9(TradingUtil.roundToTwoDecimalPlaces(
	                calculateEMA(lastIndicator.getEma9(), current.getClosePrice(), 9)));
	        indicator.setEma10(TradingUtil.roundToTwoDecimalPlaces(
	                calculateEMA(lastIndicator.getEma10(), current.getClosePrice(), 10)));
	        indicator.setEma14(TradingUtil.roundToTwoDecimalPlaces(
	                calculateEMA(lastIndicator.getEma14(), current.getClosePrice(), 14)));
	        indicator.setEma20(TradingUtil.roundToTwoDecimalPlaces(
	                calculateEMA(lastIndicator.getEma20(), current.getClosePrice(), 20)));
	        indicator.setEma21(TradingUtil.roundToTwoDecimalPlaces(
	                calculateEMA(lastIndicator.getEma21(), current.getClosePrice(), 21)));
	        indicator.setEma50(TradingUtil.roundToTwoDecimalPlaces(
	                calculateEMA(lastIndicator.getEma50(), current.getClosePrice(), 50)));
	        indicator.setEma200(TradingUtil.roundToTwoDecimalPlaces(
	                calculateEMA(lastIndicator.getEma200(), current.getClosePrice(), 200)));

	        // ✅ ATR calculation
	        double atr = calculateATR(lastIndicator, current);
	        indicator.setAtr(TradingUtil.roundToTwoDecimalPlaces(atr));

	        // ✅ Percent Change
	        double percentChange = ((current.getClosePrice() - prevClosePrice) / prevClosePrice) * 100;
	        indicator.setPercentChange(TradingUtil.roundToTwoDecimalPlaces(percentChange));

	        // ✅ RSI calculation
	        double priceChange = current.getClosePrice() - prevClosePrice;
	        double gain = Math.max(priceChange, 0);
	        double loss = Math.abs(Math.min(priceChange, 0));

	        double avgGain = ((lastIndicator.getAvgGain() * 13) + gain) / 14;
	        double avgLoss = ((lastIndicator.getAvgLoss() * 13) + loss) / 14;

	        double rs = (avgLoss == 0) ? 100 : avgGain / avgLoss;
	        double rsi = 100 - (100 / (1 + rs));

	        indicator.setAvgGain(TradingUtil.roundToTwoDecimalPlaces(avgGain));
	        indicator.setAvgLoss(TradingUtil.roundToTwoDecimalPlaces(avgLoss));
	        indicator.setRsi(TradingUtil.roundToTwoDecimalPlaces(rsi));

	    } else {
	        // ✅ For first candle with no prior indicators
	        indicator.setAvgGain(0.0);
	        indicator.setAvgLoss(0.0);
	        indicator.setRsi(50.0); // Neutral RSI starting point

	        // Optionally initialize EMAs, ATR with close price
	        double close = current.getClosePrice();
	        indicator.setEma9(close);
	        indicator.setEma10(close);
	        indicator.setEma14(close);
	        indicator.setEma20(close);
	        indicator.setEma21(close);
	        indicator.setEma50(close);
	        indicator.setEma200(close);
	        indicator.setAtr(0.0);
	        indicator.setPercentChange(0.0);
	    }

	    return indicator;
	}

	public Double calculateEMA(Double prevEma, Double closePrice, int period) {
		if (prevEma == null)
			return closePrice;
		double multiplier = 2.0 / (period + 1);
		return ((closePrice - prevEma) * multiplier) + prevEma;
	}

	public Double calculateATR(StockTechnicalIndicator lastIndicator, StockDailyPrice current) {
		if (lastIndicator == null || lastIndicator.getStockDailyPrice() == null || lastIndicator.getAtr()==null)
			return 0.0;

		double prevClose = lastIndicator.getStockDailyPrice().getClosePrice();
		double highLow = current.getHighPrice() - current.getLowPrice();
		double highPrevClose = Math.abs(current.getHighPrice() - prevClose);
		double lowPrevClose = Math.abs(current.getLowPrice() - prevClose);

		double trueRange = Math.max(highLow, Math.max(highPrevClose, lowPrevClose));
		return ((lastIndicator.getAtr() * 13) + trueRange) / 14;
	}

	
}
