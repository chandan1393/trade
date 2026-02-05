package com.fyers.fyerstrading.schedular;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.service.StockOIService;
import com.fyers.fyerstrading.utility.MarketTimeUtil;

@Component
@ConditionalOnProperty(name = "oi.stock.enabled", havingValue = "true")
public class StockOIScheduler {

	@Autowired
	private StockOIService stockOIService;

	@Autowired
	StockMasterRepository stockMasterRepo;
	@Autowired
	private ConfigurableApplicationContext context;

	@Scheduled(fixedDelayString = "${oi.stock.frequency.ms}")
	public void run() {

		if (!context.isActive())
			return;
		if (!MarketTimeUtil.isMarketOpen())
			return;

		stockOIService.fetchAndStore(getFnOStocks());
	}

	private List<String> getFnOStocks() {
		List<StockMaster> list = stockMasterRepo.findByIsInFnoTrue();
		List<String> allStocks = list.stream().map(a -> a.getSymbol()).collect(Collectors.toList());
		return allStocks;
	}
}
