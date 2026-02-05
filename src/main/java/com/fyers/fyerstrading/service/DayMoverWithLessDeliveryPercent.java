package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fyers.fyerstrading.entity.DeliverySpikeStock;
import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.TopMoversStock;
import com.fyers.fyerstrading.repo.DeliverySpikeStockRepository;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.repo.TopMoversRepository;
import com.fyers.fyerstrading.utility.TradingUtil;

@Service
public class DayMoverWithLessDeliveryPercent {
	
	
	@Autowired
	StockMasterRepository masterRepository;
	
	@Autowired
	StockDailyPriceRepository dailyPriceRepository;
	
	
	
	@Autowired
	private DeliverySpikeStockRepository deliverySpikeStockRepository;
	
	@Autowired
	TopMoversRepository topMoversRepo;

	@Transactional
	public void findAndSaveRisingStocksWithLowDelivery() {
	    // 1. Find the last processed trade date
	    LocalDate lastProcessedDate = deliverySpikeStockRepository.findTopByOrderByTradeDateDesc()
	            .map(DeliverySpikeStock::getTradeDate)
	            .orElse(LocalDate.of(2025, 1, 1)); // fallback start

	    LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));

	    // 2. Loop from next day until today
	    for (LocalDate date = lastProcessedDate.plusDays(1); !date.isAfter(today); date = date.plusDays(1)) {
	        List<StockDailyPrice> results = dailyPriceRepository
	                .findRisingFnoStocksWithDeliveryAndMovePercent(date, 1, 10, 3.0);

	        if (results.isEmpty()) {
	            continue;
	        }
	        final LocalDate tradeDate = date;
	        List<DeliverySpikeStock> toSave = results.stream()
	                .map(sdp -> {
	                    double open = sdp.getOpenPrice();
	                    double close = sdp.getClosePrice();
	                    double changePercent = ((close - open) / open) * 100;

	                    DeliverySpikeStock spike = new DeliverySpikeStock();
	                    spike.setSymbol(sdp.getStock().getSymbol());
	                    spike.setTradeDate(tradeDate);
	                    spike.setOpenPrice(open);
	                    spike.setClosePrice(close);
	                    spike.setDeliveryPercent(sdp.getDeliveryPercent());
	                    spike.setPercentMove(TradingUtil.roundToTwoDecimalPlaces(changePercent));
	                    spike.setFnO(sdp.getStock().isInFno());
	                    return spike;
	                })
	                .collect(Collectors.toList());

	        if (!toSave.isEmpty()) {
	            deliverySpikeStockRepository.saveAll(toSave);
	            System.out.println("✅ Saved " + toSave.size() + " spike records for " + date);
	        }
	    }
	}

	
	
	public void findAndSaveTopMoversStocks(LocalDate date) {

	    List<StockDailyPrice> results = dailyPriceRepository
	            .findRisingStocksWithDeliveryAndMovePercent(date, 1, 100, 8.0);

	    if (results.isEmpty()) {
	        System.out.println("No stocks found for " + date);
	        return;
	    }

	    Set<String> seen = new HashSet<>();
	    List<TopMoversStock> toSave = new ArrayList<>();

	    for (StockDailyPrice sdp : results) {
	        String symbol = sdp.getStock().getSymbol();
	        LocalDate tradeDate = sdp.getTradeDate();
	        String key = symbol + "_" + tradeDate;

	        if (seen.contains(key)) continue;
	        seen.add(key);

	        // Check if already exists to avoid duplicate insert
	        if (topMoversRepo.existsBySymbolAndTradeDate(symbol, tradeDate)) continue;

	        double open = sdp.getOpenPrice();
	        double close = sdp.getClosePrice();
	        double delivery = sdp.getDeliveryPercent();
	        double changePercent = ((close - open) / open) * 100;
	        changePercent = TradingUtil.roundToTwoDecimalPlaces(changePercent);
	        TopMoversStock topMover = new TopMoversStock();
	        topMover.setSymbol(symbol);
	        topMover.setTradeDate(tradeDate);
	        topMover.setOpenPrice(open);
	        topMover.setClosePrice(close);
	        topMover.setDeliveryPercent(delivery);
	        topMover.setPercentMove(changePercent);
	        topMover.setFnO(sdp.getStock().isInFno()); // if available

	        toSave.add(topMover);
	    }

	    if (!toSave.isEmpty()) {
	    	topMoversRepo.saveAll(toSave);
	        System.out.println("Saved " + toSave.size() + " spike records for " + date);
	    }
	}
	
	

}
