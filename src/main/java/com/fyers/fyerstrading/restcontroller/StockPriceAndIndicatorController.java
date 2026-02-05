package com.fyers.fyerstrading.restcontroller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.service.BhavcopyDownloader;
import com.fyers.fyerstrading.service.DeliveryUpdaterService;
import com.fyers.fyerstrading.service.OptionInstrumentUpdateService;
import com.fyers.fyerstrading.service.StockDataService;
import com.fyers.fyerstrading.utility.CalculationUtil;

@RestController
public class StockPriceAndIndicatorController {
	@Autowired
	StockMasterRepository stockMasterRepository;

	@Autowired
	StockDataService stockDataService;

	@Autowired
	StockDailyPriceRepository dailyDataRepository;

	@Autowired
	CalculationUtil calculationUtil;

	@Autowired
	BhavcopyDownloader bhavcopyDownloader;

	@Autowired
	DeliveryUpdaterService deliveryUpdaterService;

	@Autowired
	private OptionInstrumentUpdateService optionInstrumentService;

	@GetMapping("/downloadLatestData")
	public void downloadPreviousData(HttpSession session) {

		StockMaster s = new StockMaster();
		s.setSymbol("ADANIENT");
		stockDataService.fetchAndUpdateCurrentDayStockData("1DAY", s);

	}

	@GetMapping("/fetchAndSaveStockData")
	public void fetchAndSaveStockData(HttpSession session) {

		List<StockMaster> stocks = stockMasterRepository.findAll();
		for (StockMaster stock : stocks) {
			stockDataService.fetchAndUpdateCurrentDayStockData("1DAY", stock);
		}

	}

	@GetMapping("/downloadAndSaveNifty5MinHistoricalData")
	public void downloadAndSaveNiftyHistoricalData(HttpSession session) {
		stockDataService.downloadAndSaveNiftyHistoricalData();

	}

	@GetMapping("/fetchAndSaveStocks")
	public void fetchAndSaveStockData() {
		stockDataService.fetchAndSaveStocks();
	}

	@GetMapping("/updateNiftyDailyData")
	public void downloadAndSaveNiftyDailyData(HttpSession session) {
		stockDataService.fetchAndSaveNiftyDailyData("NSE:NIFTY50-INDEX");

	}

	@GetMapping("/fetchAndSaveAllHistoricalData")
	public void fetchAndSaveAllHistoricalData() {
		List<StockMaster> stocks = stockMasterRepository.findStocksByIndexSymbol("NSE:NIFTYMIDSML400-INDEX");
		for (StockMaster stock : stocks) {
			stockDataService.fetchAndSaveAllHistoricalData(stock, "1DAY");
		}

	}

	@GetMapping("/clculateAndSaveTechnicalIndicator")
	public void clculateAndSaveTechnicalIndicator() {
		stockDataService.clculateAndSaveTechnicalIndicator();

	}

	@GetMapping("/mapStocksToIndex")
	public void mapStocksToIndex() {
		stockDataService.mapStocksToIndexWithoutFile();

	}

	@GetMapping("/downloadBaavCopyFromNSE")
	public void downloadBaavCopyFronNSE() {
		try {
			bhavcopyDownloader.downloadCSV(LocalDate.now());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@GetMapping("/updateAllStocksFromLastDateUsingCSV")
	public void updateAllStocksFromLastDate() {

		stockDataService.updateStockPriceDaily();

	}

	@GetMapping("/updateEMAForNifty5Min")
	public void updateEMAForNifty5Min() {
		stockDataService.calculateAndSaveEmaForAllCandles();

	}

	@GetMapping("/fetchAndSaveStock5MinLatestCandle")
	public void fetchAndSaveStock5MinLatestCandle() {
		try {

			List<StockMaster> list = stockMasterRepository.findByIsInFnoTrue();
			List<String> allStocks = list.stream().map(a -> a.getSymbol()).collect(Collectors.toList());

			stockDataService.fetchAndSave5MinCandlesForAllFNO(allStocks);
		} catch (Exception e) {
			Thread.currentThread().interrupt();
			System.err.println("Scheduler interrupted: " + e.getMessage());
		}
	}

	@GetMapping("/refresh")
	public ResponseEntity<Map<String, Object>> refreshOptions() {

		LocalDate runDate =  LocalDate.now();

		optionInstrumentService.refreshOptionInstruments(runDate);

		Map<String, Object> resp = new HashMap<>();
		resp.put("status", "SUCCESS");
		resp.put("message", "Option instruments refreshed");
		resp.put("date", runDate.toString());

		return ResponseEntity.ok(resp);
	}

}
