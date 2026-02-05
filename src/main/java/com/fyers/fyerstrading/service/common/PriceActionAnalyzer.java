package com.fyers.fyerstrading.service.common;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.model.MasterCandle;

@Service
public class PriceActionAnalyzer {
	
	
	@Autowired
	CandleFetcher candleFetcher;
	  
	    public boolean isMarketOpen() {
	        LocalTime now = LocalTime.now();
	        return now.isAfter(LocalTime.of(9,15)) && now.isBefore(LocalTime.of(15,30));
	    }
	
	 public boolean breaksPreviousHigh(String symbol, String tf) {
	        List<MasterCandle> c = candleFetcher.getLastNCandles(symbol,tf,3);
	        return c.get(2).getHigh() > c.get(1).getHigh();
	    }
	    
	    
	    public boolean breaksPreviousLow(String symbol, String tf) {
	        List<MasterCandle> c = candleFetcher.getLastNCandles(symbol,tf,3);
	        return c.get(2).getLow() < c.get(1).getLow();
	    }

	    public boolean isHHHL(String symbol, String timeframe) {

	        List<MasterCandle> list = candleFetcher.getLastNCandles(symbol, timeframe, 6);

	        if (list == null || list.size() < 4)
	            return false;

	        // last 3 candles
	        MasterCandle c1 = list.get(list.size() - 3);
	        MasterCandle c2 = list.get(list.size() - 2);
	        MasterCandle c3 = list.get(list.size() - 1);

	        // Higher High = latest high > previous high
	        boolean hh = c3.getHigh() > c2.getHigh() && c2.getHigh() > c1.getHigh();

	        // Higher Low = latest low > previous low
	        boolean hl = c3.getLow() > c2.getLow() && c2.getLow() > c1.getLow();

	        return hh && hl;
	    }
	    
	    public boolean isLHLL(String symbol, String timeframe) {

	        List<MasterCandle> list = candleFetcher.getLastNCandles(symbol, timeframe, 6);

	        if (list == null || list.size() < 4)
	            return false;

	        // last 3 candles
	        MasterCandle c1 = list.get(list.size() - 3);
	        MasterCandle c2 = list.get(list.size() - 2);
	        MasterCandle c3 = list.get(list.size() - 1);

	        // Lower High = latest high < previous high
	        boolean lh = c3.getHigh() < c2.getHigh() && c2.getHigh() < c1.getHigh();

	        // Lower Low = latest low < previous low
	        boolean ll = c3.getLow() < c2.getLow() && c2.getLow() < c1.getLow();

	        return lh && ll;
	    }

}
