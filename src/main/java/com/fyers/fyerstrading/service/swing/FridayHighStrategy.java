package com.fyers.fyerstrading.service.swing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.entity.StockTechnicalIndicator;
import com.fyers.fyerstrading.model.FridayHighBacktestResult;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;

@Service
public class FridayHighStrategy {

	private static final Logger logger = LoggerFactory.getLogger(FridayHighStrategy.class);

	
	@Autowired
	StockDailyPriceRepository stockDataRepository;
	
	@Autowired
	StockMasterRepository stockMasterRepository;
	
	
	public void runBacktestForCategoriesFridayHighStrategy() {
		
		List<String> categories = List.of("NSE:NIFTYSMLCAP250-INDEX", "NSE:NIFTYMIDSML400-INDEX", "NSE:NIFTYMIDCAP150-INDEX"
				,"NSE:NIFTYMICROCAP250-INDEX","NSE:NIFTY50-INDEX","NSE:NIFTYTOTALMKT-INDEX");

		for (String category : categories) {
			logger.info("Starting backtest for category: {}", category);
			backtestFridayHighStrategy(category);

		}

	}

	public List<FridayHighBacktestResult> backtestFridayHighStrategy(String category) {
	    List<FridayHighBacktestResult> results = new ArrayList<>();
	    List<StockMaster> stocks = stockMasterRepository.findStocksByIndexSymbol(category);

	    for (StockMaster stock : stocks) {
	        List<StockDailyPrice> priceHistory = stockDataRepository.findAfterDate(stock.getSymbol(), LocalDate.of(2010, 1, 1));
	        priceHistory.sort(Comparator.comparing(StockDailyPrice::getTradeDate));
	        results.addAll(processStockData(stock.getSymbol(), priceHistory));
	    }

	    printBacktestSummary(results);
	    return results;
	}

	private List<FridayHighBacktestResult> processStockData(String symbol, List<StockDailyPrice> data) {
	    List<FridayHighBacktestResult> results = new ArrayList<>();
	    Map<YearWeek, List<StockDailyPrice>> groupedByWeek = data.stream()
	        .collect(Collectors.groupingBy(price -> YearWeek.from(price.getTradeDate())));

	    List<YearWeek> sortedWeeks = new ArrayList<>(groupedByWeek.keySet());
	    Collections.sort(sortedWeeks);

	    for (int i = 0; i < sortedWeeks.size() - 1; i++) {
	        List<StockDailyPrice> thisWeek = groupedByWeek.get(sortedWeeks.get(i));
	        List<StockDailyPrice> nextWeek = groupedByWeek.get(sortedWeeks.get(i + 1));
	        if (thisWeek == null || nextWeek == null || nextWeek.isEmpty()) continue;

	        Optional<StockDailyPrice> fridayOpt = thisWeek.stream()
	            .filter(p -> p.getTradeDate().getDayOfWeek() == DayOfWeek.FRIDAY)
	            .findFirst();
	        if (fridayOpt.isEmpty()) continue;

	        StockDailyPrice friday = fridayOpt.get();
	        StockTechnicalIndicator tech = friday.getTechnicalIndicator();
	        if (tech == null) continue;

	        double avgVolume = calculateAverageVolume(thisWeek);
	        double candleRange = friday.getHighPrice() - friday.getLowPrice();

	        // Filters
	        if (tech.getRsi() == null || tech.getRsi() < 55 || tech.getRsi() > 65) continue;
	        if (tech.getEma9() == null || tech.getEma9() > friday.getClosePrice()) continue;
	        if (friday.getVolume() < avgVolume * 1.5) continue;
	        if (tech.getAtr() == null || tech.getAtr() < friday.getClosePrice() * 0.0075) continue;
	        if (candleRange < friday.getClosePrice() * 0.0075) continue; // minimum 0.75% candle
	        if (friday.getClosePrice() < friday.getOpenPrice()) continue; // reject bearish
	        if ((friday.getHighPrice() - friday.getClosePrice()) > candleRange * 0.25) continue;

	        // Entry on Monday breakout
	        Optional<StockDailyPrice> mondayEntryOpt = nextWeek.stream()
	            .filter(p -> p.getTradeDate().getDayOfWeek() == DayOfWeek.MONDAY && p.getHighPrice() > friday.getHighPrice())
	            .findFirst();
	        if (mondayEntryOpt.isEmpty()) continue;

	        StockDailyPrice entryDay = mondayEntryOpt.get();
	        double entryPrice = Math.max(entryDay.getOpenPrice(), friday.getHighPrice());
	        double atr = tech.getAtr();

	        double target = entryPrice + (atr * 1.5);
	        double stopLoss = entryPrice - (atr * 1.0);

	        double exitPrice = nextWeek.get(nextWeek.size() - 1).getClosePrice();
	        boolean hitTarget = false, hitStop = false;

	        for (StockDailyPrice day : nextWeek) {
	            if (day.getTradeDate().isBefore(entryDay.getTradeDate())) continue;

	            if (day.getLowPrice() <= stopLoss) {
	                exitPrice = stopLoss;
	                hitStop = true;
	                break;
	            } else if (day.getHighPrice() >= target) {
	                exitPrice = target;
	                hitTarget = true;
	                break;
	            }
	        }

	        double returnPct = ((exitPrice - entryPrice) / entryPrice) * 100.0;

	        FridayHighBacktestResult result = new FridayHighBacktestResult();
	        result.setSymbol(symbol);
	        result.setFridayDate(friday.getTradeDate());
	        result.setFridayClose(friday.getClosePrice());
	        result.setNextWeekHigh(nextWeek.stream().mapToDouble(StockDailyPrice::getHighPrice).max().orElse(0.0));
	        result.setNextWeekLow(nextWeek.stream().mapToDouble(StockDailyPrice::getLowPrice).min().orElse(0.0));
	        result.setNextWeekClose(exitPrice);
	        result.setReturnPercent(returnPct);
	        result.setTargetPrice(target);
	        result.setStopLoss(stopLoss);
	        result.setHitTarget(hitTarget);
	        result.setHitStopLoss(hitStop);

	        results.add(result);
	    }

	    return results;
	}

	
	
	public void printBacktestSummary(List<FridayHighBacktestResult> results) {
	    if (results.isEmpty()) {
	        System.out.println("No trades found.");
	        return;
	    }

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    String fileName = "friday_high_trades.txt";

	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
	        for (FridayHighBacktestResult r : results) {
	            String line = String.format(
	                "Stock: %s | Date: %s | Friday Close: %.2f | Next Week Close: %.2f | High: %.2f | Low: %.2f | Return: %.2f%% | SL: %.2f | Target: %.2f | Hit Target: %b | Hit SL: %b",
	                r.getSymbol(), r.getFridayDate().format(formatter), r.getFridayClose(), r.getNextWeekClose(),
	                r.getNextWeekHigh(), r.getNextWeekLow(), r.getReturnPercent(), r.getStopLoss(), r.getTargetPrice(),
	                r.isHitTarget(), r.isHitStopLoss()
	            );
	            writer.write(line);
	            writer.newLine();  // move to next line in the file
	        }
	    } catch (IOException e) {
	        System.err.println("Error writing to file: " + e.getMessage());
	    }

	    System.out.println("------ Friday High Strategy Backtest Results ------");

	    double totalReturn = results.stream().mapToDouble(FridayHighBacktestResult::getReturnPercent).sum();
	    long wins = results.stream().filter(r -> r.getReturnPercent() > 0).count();
	    double avgReturn = totalReturn / results.size();
	    double winRate = (double) wins / results.size() * 100;

	    System.out.println("---------------------------------------------------");
	    System.out.printf("Total Trades: %d\n", results.size());
	    System.out.printf("Average Return: %.2f%%\n", avgReturn);
	    System.out.printf("Winning Trades: %d (%.2f%%)\n", wins, winRate);
	    System.out.println("---------------------------------------------------");

	    System.out.printf("Detailed trades written to: %s\n", fileName);
	}
	
	
	// Calculate average volume for the week
	private double calculateAverageVolume(List<StockDailyPrice> weekData) {
	    return weekData.stream().mapToDouble(StockDailyPrice::getVolume).average().orElse(0.0);
	}

}


