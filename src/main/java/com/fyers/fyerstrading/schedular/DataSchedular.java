package com.fyers.fyerstrading.schedular;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.service.StockDataService;

@Component
public class DataSchedular {

	@Autowired
	StockMasterRepository stockMasterRepository;
	
	@Autowired
	StockDataService stockDataService;
	
	@Autowired
	StockDailyPriceRepository stockDailyPriceRepository;
	
	
	
	//@Scheduled(cron = "0 */15 17-18 * * MON-FRI")
	public void fetchAndSaveCurrentData() {
		String schedulerName = "fetchAndSaveCurrentData";
		try {
			List<StockMaster> stocks = stockMasterRepository.findAll();
			for (StockMaster stock : stocks) {
				stockDataService.fetchAndUpdateCurrentDayStockData( "1DAY",stock);
			}
	    } catch (Exception e) {
	        System.err.println("❌ Exception in scheduler [" + schedulerName + "]: " + e.getMessage());	       
	    }

	}
	

	@Scheduled(cron = "0 05 17 * * MON-FRI")
	public void downloadAndSaveDailyPriceUsingCSV() {
		String schedulerName = "downloadAndSaveDailyPriceUsingCSV";
		try {
	        RestTemplate restTemplate = new RestTemplate();
	        String url = "http://localhost:8080/updateAllStocksFromLastDateUsingCSV";
	        String response = restTemplate.getForObject(url, String.class);
	        System.out.println("Response from API: " + response);
	    } catch (Exception e) {
	    	System.err.println("❌ Exception in scheduler [" + schedulerName + "]: " + e.getMessage());
	    }
	   
	}
	
	@Scheduled(cron = "0 05 17 * * MON-FRI")
	public void downloadAndSaveNiftyDailyHistoricalData() {
		stockDataService.fetchAndSaveNiftyDailyData("NSE:NIFTY50-INDEX");

	}
	
	
	@Scheduled(cron = "0 */5 9-15 * * MON-FRI", zone = "Asia/Kolkata")
	public void fetchAndSaveNifty5MinLatestCandle() {
		String schedulerName = "fetchAndSaveNifty5MinLatestCandle";
		try {
	        // wait 5 sec after the candle closes
	        Thread.sleep(2000);

	        LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));
	        LocalTime marketOpen = LocalTime.of(9, 20);
	        LocalTime marketClose = LocalTime.of(15, 35);

	        if (now.isBefore(marketOpen) || now.isAfter(marketClose)) {
	            System.out.println("Skipping candle fetch, market closed. Time: " + now);
	            return;
	        }

	        stockDataService.fetchAndSaveNifty5MinLatestCandle();
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	        System.err.println("❌ Exception in scheduler [" + schedulerName + "]: " + e.getMessage());
	    }
	    
		
		
	}


	
	@Scheduled(cron = "0 */5 9-15 * * MON-FRI", zone = "Asia/Kolkata")
	public void fetchAndSaveStock5MinLatestCandle() {
		String schedulerName = "fetchAndSaveStock5MinLatestCandle";
		try {
	        // wait 5 sec after the candle closes
	        Thread.sleep(2000);

	        LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));
	        LocalTime marketOpen = LocalTime.of(9, 20);
	        LocalTime marketClose = LocalTime.of(15, 35);

	        if (now.isBefore(marketOpen) || now.isAfter(marketClose)) {
	            System.out.println("Skipping candle fetch, market closed. Time: " + now);
	            return;
	        }
	        List<StockMaster> list = stockMasterRepository.findByIsInFnoTrue();
	        List<String> allStocks=list.stream().map(a->a.getSymbol()).collect(Collectors.toList());
	        
	        stockDataService.fetchAndSave5MinCandlesForAllFNO(allStocks);
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	        System.err.println("❌ Exception in scheduler [" + schedulerName + "]: " + e.getMessage());

	    }
	   
	}


  	
}
