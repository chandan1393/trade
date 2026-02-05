package com.fyers.fyerstrading.utility;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.entity.StockTechnicalIndicator;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.repo.StockTechnicalIndicatorRepository;

@Service
public class CalculationUtil {

	@Autowired
	StockMasterRepository stockMasterRepository;

	@Autowired
	StockDailyPriceRepository stockDataRepository;
	@Autowired
	StockTechnicalIndicatorRepository stockTechnicalIndicatorRepository;

	@Transactional
	public void calculateAndSaveIndicators(StockMaster stock) {
		List<StockDailyPrice> stockDailyDataList = stockDataRepository.findAllByStockSymbol(stock.getSymbol());

		if (stockDailyDataList.size() < 2) { // Ensure at least 2 data points for calculations
			return;
		}

		List<Double> closePriceList = stockDailyDataList.stream().map(StockDailyPrice::getClosePrice)
				.collect(Collectors.toList());

		List<StockTechnicalIndicator> technicalIndicators = new ArrayList<>();

		for (int i = 0; i < stockDailyDataList.size(); i++) {
			StockDailyPrice stockDailyData = stockDailyDataList.get(i);
			double closePrice = stockDailyData.getClosePrice();

			// ✅ Create indicator linked to StockDailyPrice
			StockTechnicalIndicator indicator = new StockTechnicalIndicator();
			indicator.setStockDailyPrice(stockDailyData);
			indicator.setTradeDate(stockDailyData.getTradeDate());

			// ✅ Percent Change Calculation
			if (i > 0) {
				double prevClose = stockDailyDataList.get(i - 1).getClosePrice();
				double changePercent = ((closePrice - prevClose) / prevClose) * 100;
				indicator.setPercentChange(TradingUtil.roundToTwoDecimalPlaces(changePercent));
			}

			// ✅ EMAs Calculation (Ensure correct sublist sizes)
			if (i >= 8)
				indicator.setEma9(TradingUtil.calculateEMA(closePriceList.subList(i - 8, i + 1), 9,
						technicalIndicators.get(i - 1).getEma9()));
			if (i >= 9)
				indicator.setEma10(TradingUtil.calculateEMA(closePriceList.subList(i - 9, i + 1), 10,
						technicalIndicators.get(i - 1).getEma10()));
			if (i >= 13)
				indicator.setEma14(TradingUtil.calculateEMA(closePriceList.subList(i - 13, i + 1), 14,
						technicalIndicators.get(i - 1).getEma14()));
			if (i >= 19)
				indicator.setEma20(TradingUtil.calculateEMA(closePriceList.subList(i - 19, i + 1), 20,
						technicalIndicators.get(i - 1).getEma20()));
			if (i >= 20)
				indicator.setEma21(TradingUtil.calculateEMA(closePriceList.subList(i - 20, i + 1), 21,
						technicalIndicators.get(i - 1).getEma21()));
			if (i >= 49)
				indicator.setEma50(TradingUtil.calculateEMA(closePriceList.subList(i - 49, i + 1), 50,
						technicalIndicators.get(i - 1).getEma50()));
			if (i >= 199)
				indicator.setEma200(TradingUtil.calculateEMA(closePriceList.subList(i - 199, i + 1), 200,
						technicalIndicators.get(i - 1).getEma200()));

			// ✅ RSI & ATR Calculation (Ensure sufficient data)
			if (i >= 14) { // Needs at least 15 prices
				Map<String, Double> rsiMap = TradingUtil.calculateRSI(closePriceList.subList(i - 14, i + 1), 14,
						technicalIndicators.get(i - 1).getAvgGain(), technicalIndicators.get(i - 1).getAvgLoss());
				indicator.setAvgGain(rsiMap.getOrDefault("avgGain", 0.0));
				indicator.setAvgLoss(rsiMap.getOrDefault("avgLoss", 0.0));
				indicator.setRsi(rsiMap.getOrDefault("RSI", 0.0));
			}
			if (i == 13) {
			    // First ATR after 14 candles (i.e., on 14th data point)
			    double initialATR = TradingUtil.calculateInitialATR(stockDailyDataList.subList(0, 14));
			    indicator.setAtr(initialATR);
			} else if (i > 13) {
			    Double prevATR = technicalIndicators.get(i - 1).getAtr();
			    indicator.setAtr(TradingUtil.calculateATRWilder(
			        stockDailyDataList.subList(i - 13, i + 1), 14, prevATR
			    ));
			}

			technicalIndicators.add(indicator);
		}

		// ✅ Bulk Save All Indicators
		stockTechnicalIndicatorRepository.saveAll(technicalIndicators);
	}
	
	
	
	
	
	
	@Transactional
	public void recalculateAndSaveATR(StockMaster stock) {
	    List<StockDailyPrice> priceList = stockDataRepository.findAllByStockSymbol(stock.getSymbol());
	    List<StockTechnicalIndicator> indicatorList = stockTechnicalIndicatorRepository.findAllByStock(stock);

	    if (priceList.size() < 14 || indicatorList.size() != priceList.size()) {
	        return; // Data mismatch or insufficient data
	    }

	    // Create a map for quick lookup by trade date
	    Map<LocalDate, StockTechnicalIndicator> indicatorMap = indicatorList.stream()
	            .collect(Collectors.toMap(StockTechnicalIndicator::getTradeDate, i -> i));

	    Double prevATR = null;

	    for (int i = 0; i < priceList.size(); i++) {
	        StockDailyPrice price = priceList.get(i);
	        StockTechnicalIndicator indicator = indicatorMap.get(price.getTradeDate());

	        if (indicator == null) continue;

	        // Step 1: Set ATR to null first
	        indicator.setAtr(null);

	        if (i >= 13) {
	            List<StockDailyPrice> atrSubList = priceList.subList(i - 13, i + 1);

	            if (i == 13) {
	                // First ATR using average TR
	                prevATR = TradingUtil.calculateInitialATR(atrSubList);
	            } else {
	                // Subsequent ATRs using Wilder’s method
	                prevATR = TradingUtil.calculateATRWilder(atrSubList, 14, prevATR);
	            }

	            indicator.setAtr(prevATR);
	        }
	    }

	    stockTechnicalIndicatorRepository.saveAll(indicatorList);
	}



}