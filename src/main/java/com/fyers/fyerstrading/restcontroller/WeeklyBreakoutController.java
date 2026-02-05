package com.fyers.fyerstrading.restcontroller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.entity.WeeklyBreakOutBacktestResultEntity;
import com.fyers.fyerstrading.entity.WeeklyBreakoutSetup;
import com.fyers.fyerstrading.model.BacktestResult;
import com.fyers.fyerstrading.model.BreakoutSetupResult;
import com.fyers.fyerstrading.model.WeeklyCandle;
import com.fyers.fyerstrading.repo.BreakoutSetupRepository;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.repo.WeeklyBreakOutBacktestResultRepository;
import com.fyers.fyerstrading.service.TelegramService;
import com.fyers.fyerstrading.service.WeeklyBreakoutBacktester;
import com.fyers.fyerstrading.utility.BacktestResultMapper;
import com.fyers.fyerstrading.utility.CandleConverter;

@RestController
public class WeeklyBreakoutController {

	@Autowired
	StockMasterRepository masterRepo;

	@Autowired
	StockDailyPriceRepository dailyPriceRepository;

	@Autowired
	private WeeklyBreakOutBacktestResultRepository backtestResultRepo;

	@Autowired
	private BreakoutSetupRepository breakoutSetupRepo;

	@Autowired
	private WeeklyBreakoutBacktester backtest;

	@Autowired
	private TelegramService telegramService; 
	
	

	@GetMapping("/backTestWeeklyBreakOut")
	public ResponseEntity<String> backTestWeeklyBreakOut() {
		List<StockMaster> allStocks = masterRepo.findAll();
		List<WeeklyBreakoutSetup> allSetups = new ArrayList<>();
		for (StockMaster stock : allStocks) {
			List<StockDailyPrice> dailyData = dailyPriceRepository.findAfterDate(stock.getSymbol(),
					LocalDate.of(2025, 1, 1));

			List<WeeklyCandle> weeklyCandles = CandleConverter.convertToWeekly(dailyData);

			List<BreakoutSetupResult> setups = backtest.findMonthlyBreakoutSetups(weeklyCandles, stock.getSymbol());

			for (BreakoutSetupResult wc : setups) {
				WeeklyBreakoutSetup entity = new WeeklyBreakoutSetup();
				entity.setSymbol(stock.getSymbol());
				entity.setWeekStarting(wc.getWeekStart());
				entity.setOpen(wc.getOpen());
				entity.setHigh(wc.getHigh());
				entity.setLow(wc.getLow());
				entity.setClose(wc.getClose());
				entity.setVolume(wc.getVolume());

				// Add metrics if you calculate them inside findMonthlyBreakoutSetups
				entity.setAvgVolume(wc.getAvgVolume());
				entity.setBodyPercent(wc.getBodyPercent());
				entity.setUpperWickPercent(wc.getUpperWickPercent());
				allSetups.add(entity);

			}

			breakoutSetupRepo.saveAll(allSetups);
		}

		return ResponseEntity.ok("Backtest completed.");
	}
	
	
	
	@GetMapping("/sendTestMessage")
	public ResponseEntity<String> sendTestMessage() {
		 RestTemplate restTemplate = new RestTemplate();
	        String botToken = "8019897525:AAG5VZN9HQvSFUcoA_cStLQHpYw2VlKkl6M";
	        String chatId = "1740666539";
	        String message = "Hello from simple test!";

	        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        Map<String, Object> body = new HashMap<>();
	        body.put("chat_id", chatId);
	        body.put("text", message);

	        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

	        try {
	            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
	            System.out.println("Response: " + response.getBody());
	            return response;
	        } catch (Exception e) {
	            System.err.println("Telegram Error: " + e.getMessage());
	        }
			return null;
	}

}
