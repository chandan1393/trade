package com.fyers.fyerstrading.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.dto.StockOiSnapshot;
import com.fyers.fyerstrading.entity.StockOIData;
import com.fyers.fyerstrading.model.StockOiLevels;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.repo.StockOIRepository;

@Service
public class StockOIService {

	private final AtomicBoolean running = new AtomicBoolean(false);

	@Autowired
	private FyersApiService apiClient;

	@Autowired
	private StockOIRepository oiRepository;

	@Autowired
	private StockMasterRepository masterRepository;
	
	@Autowired
	private  StockOIRepository repo;
   
	@Autowired
	private  StockOILevelsFinder levelsFinder;
    

	public void fetchAndStore(List<String> fnoStocks) {

		if (!running.compareAndSet(false, true))
			return;

		try {
			for (String stock : fnoStocks) {
				try {
					List<StockOIData> data = apiClient.getStockOptionChain(stock, 15, null);
					if (!data.isEmpty()) {
						oiRepository.saveAll(data);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} finally {
			running.set(false);
		}
	}

	public int getATMStrike(double ltp, boolean isIndex) {
		if (isIndex) {
			return (int) (Math.round(ltp / 50.0) * 50); // Nifty strikes multiple of 50
		} else {
			return (int) (Math.round(ltp / 10.0) * 10); // F&O stocks multiple of 10
		}
	}

	public StockOiSnapshot getSnapshot(String symbol) {

		List<StockOIData> current = repo.findBySymbolAndLatest(symbol);

		List<StockOIData> previous = repo.findPrevious(symbol);

		StockOiLevels levels = levelsFinder.findStockOiLevels(current, previous);

		StockOiSnapshot snapshot = new StockOiSnapshot();
		snapshot.setSymbol(symbol);
		snapshot.setStrikes(current);
		snapshot.setSupport(levels.getSupport());
		snapshot.setResistance(levels.getResistance());

		return snapshot;
	}

}
