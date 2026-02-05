package com.fyers.fyerstrading.restcontroller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.model.MasterCandle;
import com.fyers.fyerstrading.model.ORBResult;
import com.fyers.fyerstrading.model.SMCBBacktestSummary;
import com.fyers.fyerstrading.model.TradeResult;
import com.fyers.fyerstrading.patterns.ImpulseStrengthPatternDetector;
import com.fyers.fyerstrading.repo.FyersAuthRepository;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.service.AuthService;
import com.fyers.fyerstrading.service.BackTestHourlyStartegy;
import com.fyers.fyerstrading.service.DailyConsolidationEmaBacktest;
import com.fyers.fyerstrading.service.LongConsolidatedStockService;
import com.fyers.fyerstrading.service.LongResistanceBreakoutBacktest;
import com.fyers.fyerstrading.service.PatternBacktestScanner;
import com.fyers.fyerstrading.service.ResistanceBreakoutBacktestService;
import com.fyers.fyerstrading.service.common.CandleFetcher;
import com.fyers.fyerstrading.strategy.IntradayVWAPBacktest;
import com.fyers.fyerstrading.strategy.OpenRangeBreakoutStrategy;
import com.fyers.fyerstrading.strategy.PositionalNiftyStrategyUpdated;
import com.fyers.fyerstrading.strategy.RSIReversalStrategy;

@RestController
public class BackTestController {

	@Autowired
	AuthService authService;

	@Autowired
	FyersAuthRepository fyersAuthRepository;

	@Autowired
	private PositionalNiftyStrategyUpdated posStrategy;

	@Autowired
	private RSIReversalStrategy rsiReversalStrategy;

	@Autowired
	private BackTestHourlyStartegy backTestHourlyStartegy;

	@Autowired
	private StockMasterRepository masterRepository;

	@Autowired
	private OpenRangeBreakoutStrategy openRangeBreakoutStrategy;

	

	@Autowired
	private LongConsolidatedStockService longConsolidatedStockService;

	@Autowired
	private CandleFetcher priceService;

	@Autowired
	private ResistanceBreakoutBacktestService resistanceBreakoutBacktestService;

	@Autowired
	LongResistanceBreakoutBacktest longResistanceBreakoutBacktest;
	@Autowired
	IntradayVWAPBacktest intradayVWAPBacktest;
	
	@Autowired
	DailyConsolidationEmaBacktest dailyConsolidationEmaBacktest;

	@Autowired
	CandleFetcher candleFetcher;
	@Autowired
	PatternBacktestScanner scanner;
	
	@Autowired
	ImpulseStrengthPatternDetector impulseStrengthPatternDetector;
	
	@Autowired
	StockDailyPriceRepository dailyPrice;
	

	@GetMapping("/backTest")
	public void backTest() {
		LocalDate start = LocalDate.now().minusYears(2);
		LocalDate end = LocalDate.now().minusDays(1);
		posStrategy.backtest("NSE:NIFTY-50:EQ", start, end);

	}

	@GetMapping("/backTestHourly")
	public void backTestHourly() {
		List<StockMaster> list = masterRepository.findByIsInFnoTrue();
		List<String> allStocks = list.stream().map(StockMaster::getSymbol).collect(Collectors.toList());

		LocalDate startDate = LocalDate.of(2025, 10, 01);
		LocalDate endDate = LocalDate.now();

		System.out.println("Running backtest from " + startDate + " to " + endDate);

		LocalDate currentDate = startDate;
		while (!currentDate.isAfter(endDate)) {
			System.out.println("\n=== Backtesting for: " + currentDate + " ===");
			try {
				backTestHourlyStartegy.backtestFirstHourBreakout(allStocks, currentDate);
			} catch (Exception e) {
				System.err.println("Error on " + currentDate + ": " + e.getMessage());
				e.printStackTrace();
			}
			currentDate = currentDate.plusDays(1);
		}

		System.out.println("\n✅ Backtest completed for all days.");
	}

	@GetMapping("/rsiReversalStrategy")
	public void rsiReversalStrategy() {
		List<StockMaster> list = masterRepository.findByIsInFnoTrue();
		List<String> allStocks = list.stream().map(StockMaster::getSymbol).collect(Collectors.toList());

		LocalDate startDate = LocalDate.of(2025, 9, 29);
		LocalDate endDate = LocalDate.now();

		System.out.println("Running backtest from " + startDate + " to " + endDate);

		LocalDate currentDate = startDate;
		while (!currentDate.isAfter(endDate)) {
			System.out.println("\n=== Backtesting for: " + currentDate + " ===");
			try {
				rsiReversalStrategy.backtestRSIReversal(allStocks, currentDate);
			} catch (Exception e) {
				System.err.println("Error on " + currentDate + ": " + e.getMessage());
				e.printStackTrace();
			}
			currentDate = currentDate.plusDays(1);
		}

		System.out.println("\n✅ Backtest completed for all days.");
	}

	@GetMapping("/openRangeBreakoutStrategy")
	public void openRangeBreakoutStrategy() {

		LocalDate startDate = LocalDate.of(2025, 1, 1);
		LocalDate endDate = LocalDate.now();

		System.out.println("Running backtest from " + startDate + " to " + endDate);

		int totalTrades = 0;
		int totalWins = 0;
		int totalLosses = 0;
		double totalPnL = 0.0;
		double grossProfit = 0.0;
		double grossLoss = 0.0;
		double maxDrawdown = 0.0;

		double equity = 0.0;
		double peakEquity = 0.0;

		List<ORBResult> allResults = new ArrayList<>();

		LocalDate currentDate = startDate;
		while (!currentDate.isAfter(endDate)) {
			try {
				ORBResult result = openRangeBreakoutStrategy.backtestOpenRangeForDate(currentDate);
				if (result != null && result.trades > 0) {
					allResults.add(result);

					totalTrades += result.trades;
					totalWins += result.wins;
					totalLosses += result.losses;
					totalPnL += result.totalPnL;
					grossProfit += result.grossProfit;
					grossLoss += result.grossLoss;

					// ✅ Update true portfolio drawdown cumulatively
					equity += result.totalPnL;
					peakEquity = Math.max(peakEquity, equity);
					double drawdown = peakEquity - equity;
					maxDrawdown = Math.max(maxDrawdown, drawdown);
				}
			} catch (Exception e) {
				System.err.println("Error on " + currentDate + ": " + e.getMessage());
			}
			currentDate = currentDate.plusDays(1);
		}

		double winRate = totalTrades > 0 ? (totalWins * 100.0 / totalTrades) : 0;
		double avgPnL = totalTrades > 0 ? totalPnL / totalTrades : 0;
		double avgWin = totalWins > 0 ? grossProfit / totalWins : 0;
		double avgLoss = totalLosses > 0 ? grossLoss / totalLosses : 0;
		double profitFactor = (grossLoss != 0) ? Math.abs(grossProfit / grossLoss) : 0;

		// ✅ Correct expectancy formula
		double expectancy = (winRate / 100.0) * avgWin + ((100.0 - winRate) / 100.0) * avgLoss;

		System.out.println("\n==========================");
		System.out.println("📊 FINAL CONSOLIDATED SUMMARY");
		System.out.println("==========================");
		System.out.printf("Total Days Tested = %d%n", allResults.size());
		System.out.printf("Total Trades = %d%n", totalTrades);
		System.out.printf("Winning Trades = %d%n", totalWins);
		System.out.printf("Losing Trades = %d%n", totalLosses);
		System.out.printf("Win Rate = %.2f%%%n", winRate);
		System.out.printf("Net PnL = %.2f%n", totalPnL);
		System.out.printf("Average PnL per Trade = %.2f%n", avgPnL);
		System.out.printf("Gross Profit = %.2f%n", grossProfit);
		System.out.printf("Gross Loss = %.2f%n", grossLoss);
		System.out.printf("Average Win = %.2f%n", avgWin);
		System.out.printf("Average Loss = %.2f%n", avgLoss);
		System.out.printf("Profit Factor = %.2f%n", profitFactor);
		System.out.printf("Expectancy = %.2f%n", expectancy);
		System.out.printf("Max Drawdown = %.2f%n", maxDrawdown);
		System.out.println("==========================\n");
	}

	
	@GetMapping("/backtestLongConsolidatestock")
	public void backtestLongConsolidatestock() {

		LocalDate startDate = LocalDate.of(2025, 7, 1);
		LocalDate endDate = LocalDate.now();

		List<StockMaster> fnoStocks = masterRepository.findByIsInFnoTrue();
		for (StockMaster stock : fnoStocks) {
			List<StockDailyPrice> candles = priceService.geStocktDailyCandles(stock.getSymbol(), startDate, endDate);
			longConsolidatedStockService.scanStock(stock.getSymbol(), candles, endDate);

		}

	}

	@GetMapping("/backtestLongConsolidatestocksd")
	public void backtestLongConsolidatestocksd() {
		LocalDate endDate = LocalDate.of(2025, 10, 1);

		List<StockMaster> fnoStocks = masterRepository.findByIsInFnoTrue();
		for (StockMaster stock : fnoStocks) {
			List<StockDailyPrice> candles = priceService.getRecordsBeforDate(stock.getSymbol(), endDate, 180);
			longConsolidatedStockService.scanStock(stock.getSymbol(), candles, endDate);
		}

	}

	@GetMapping("/breakout-analysis")
	public ResponseEntity<?> analyze() {
		return ResponseEntity.ok(longConsolidatedStockService.analyzeBreakouts());
	}

	@GetMapping("/ccccc")
	public void ccc() {
		List<StockMaster> fnoStocks = masterRepository.findByIsInFnoTrue();
		for (StockMaster stock : fnoStocks) {
			List<MasterCandle> candles = candleFetcher.getLastNCandles(stock.getSymbol(), "1d", 300);
			longResistanceBreakoutBacktest.backtest(stock.getSymbol(), candles);
		}

	}

	@GetMapping("/intradayVWAPBacktest")
	public void intradayVWAPBacktest() {

	    List<StockMaster> fnoStocks = masterRepository.findByIsInFnoTrue();

	    double overallPnL = 0;
	    int totalTrades = 0;
	    int wins = 0;
	    int losses = 0;

	    System.out.println("\n====================================");
	    System.out.println("INTRADAY VWAP BACKTEST STARTED");
	    System.out.println("====================================");

	    for (StockMaster stock : fnoStocks) {

	        LocalDateTime start = LocalDate.of(2025, 9, 1).atTime(9, 15);
	        LocalDateTime end   = LocalDate.of(2025, 12, 31).atTime(15, 30);

	        List<MasterCandle> candles =
	                candleFetcher.getCandlesBWDates(stock.getSymbol(), "5m", start, end);

	        if (candles == null || candles.isEmpty())
	            continue;

	        List<TradeResult> trades =
	                IntradayVWAPBacktest.backtest(
	                        stock.getSymbol(),
	                        candles,
	                        100_000,
	                        0.01
	                );

	        if (trades == null || trades.isEmpty())
	            continue;

	        System.out.println("\n==============================");
	        System.out.println("STOCK : " + stock.getSymbol());
	        System.out.println("==============================");

	        double stockPnL = 0;

	        for (TradeResult tr : trades) {

	            System.out.println(
	                    tr.getTradeDate() + " | " +
	                    tr.getSide() +
	                    " | Setup: " + tr.getSetupTime() +
	                    " | Entry: " + String.format("%.2f", tr.getEntryPrice()) +
	                    " (" + tr.getEntryTime() + ")" +
	                    " | Exit: " + String.format("%.2f", tr.getExitPrice()) +
	                    " (" + tr.getExitTime() + ")" +
	                    " | Qty: " + tr.getQty() +
	                    " | PnL: " + String.format("%.2f", tr.getPnl()) +
	                    " | " + tr.getExitReason()
	            );

	            stockPnL += tr.getPnl();

	            if (tr.getPnl() > 0)
	                wins++;
	            else
	                losses++;
	        }

	        System.out.println("---- STOCK SUMMARY ----");
	        System.out.println("Trades : " + trades.size());
	        System.out.println("PnL    : " + String.format("%.2f", stockPnL));

	        overallPnL += stockPnL;
	        totalTrades += trades.size();
	    }

	    // ================= FINAL SUMMARY =================
	    System.out.println("\n====================================");
	    System.out.println("FINAL BACKTEST SUMMARY");
	    System.out.println("====================================");

	    System.out.println("Total Trades   : " + totalTrades);
	    System.out.println("Winning Trades : " + wins);
	    System.out.println("Losing Trades  : " + losses);

	    double winRate = totalTrades == 0 ? 0 : (wins * 100.0 / totalTrades);
	    double avgPnL  = totalTrades == 0 ? 0 : (overallPnL / totalTrades);

	    System.out.println("Win Rate (%)   : " + String.format("%.2f", winRate));
	    System.out.println("Avg PnL/Trade  : " + String.format("%.2f", avgPnL));
	    System.out.println("Total PnL      : " + String.format("%.2f", overallPnL));

	    System.out.println("\n====================================");
	    System.out.println("BACKTEST COMPLETED");
	    System.out.println("====================================");
	}


	@GetMapping("/sssssssss")
	public void cccs() {
		dailyConsolidationEmaBacktest.runBacktest();
	}
	
	
	@GetMapping("/run-all")
	public ResponseEntity<String> runScanAll() {
	    // 1. Fetch all stock masters
	    List<StockMaster> allStocks = masterRepository.findAll();
	    
	    if (allStocks.isEmpty()) {
	        return ResponseEntity.badRequest().body("No stocks found in StockMaster table.");
	    }

	    // 2. Trigger the asynchronous batch process
	    scanner.processAllStocksAsync(allStocks);

	    return ResponseEntity.ok("Batch processing started for " + allStocks.size() + " stocks. Check DB for results.");
	}
	
	
	@GetMapping("/impulseStrengthPatternDetector")
	public ResponseEntity<String> impulseStrengthPatternDetector() {
	    // 1. Fetch all stock masters
	    List<StockMaster> allStocks = masterRepository.findByIsInFnoTrue();
	    
	    if (allStocks.isEmpty()) {
	        return ResponseEntity.badRequest().body("No stocks found in StockMaster table.");
	    }

	    
	    for(StockMaster stock:allStocks) {
	    	
	    	List<StockDailyPrice> dbPrices = dailyPrice.findAllBWDate(stock.getSymbol(),
					LocalDate.now().minusYears(2), LocalDate.now());
	    	
	    	impulseStrengthPatternDetector.backtestLastOneYear(dbPrices,stock.getSymbol());
	    	
	    }
	    
	    

	    return ResponseEntity.ok("Batch processing started for " + allStocks.size() + " stocks. Check DB for results.");
	}
	
}
