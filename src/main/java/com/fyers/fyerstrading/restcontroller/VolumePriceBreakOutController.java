package com.fyers.fyerstrading.restcontroller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.entity.NiftyDailyCandle;
import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.TradeExecution;
import com.fyers.fyerstrading.entity.TradeHistoryReport;
import com.fyers.fyerstrading.entity.TradeSetup;
import com.fyers.fyerstrading.entity.TradeStatus;
import com.fyers.fyerstrading.model.FyersOrderResponse;
import com.fyers.fyerstrading.model.HoldingsResponse;
import com.fyers.fyerstrading.model.Order;
import com.fyers.fyerstrading.repo.NiftyDailyCandleRepo;
import com.fyers.fyerstrading.repo.TradeExecutionRepository;
import com.fyers.fyerstrading.repo.TradeHistoryReportRepository;
import com.fyers.fyerstrading.repo.TradeSetupRepository;
import com.fyers.fyerstrading.service.FyersApiService;
import com.fyers.fyerstrading.service.VolumePriceBreakOutService;
import com.fyers.fyerstrading.service.swing.priceVolume.BacktestServiceForVolumeAndPriceBreakOut;
import com.fyers.fyerstrading.service.swing.priceVolume.PriceVolumeBreakoutService;
import com.fyers.fyerstrading.service.swing.priceVolume.TradeManagerService;

import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "Volume Price BreakOut Controller Management")
public class VolumePriceBreakOutController {

	@Autowired
	private BacktestServiceForVolumeAndPriceBreakOut backtestService;

	@Autowired
	NiftyDailyCandleRepo niftyRepo;

	@Autowired
	FyersApiService fyersApiService;

	@Autowired
	TradeSetupRepository tradeSetupRepository;

	@Autowired
	TradeExecutionRepository tradeExecutionRepository;

	@Autowired
	PriceVolumeBreakoutService priceVolumeBreakoutService;

	@Autowired
	TradeHistoryReportRepository tradeHistoryReportRepository;

	@Autowired
	TradeManagerService tradeManagerService;

	@Autowired
	VolumePriceBreakOutService vpBreakOutService;

	@GetMapping("/backtestBreakOutSwingTradingStrategyForAll")
	public ResponseEntity<String> executeSwingTradingStrategy() {
		backtestService.backtestCategory();
		return ResponseEntity.ok("Completed");
	}

	@GetMapping("/backtestBreakOutSwingTradingStrategyForSingle")
	public ResponseEntity<List<TradeHistoryReport>> getBackTestForSingleStock(@RequestParam String stockSymbol) {
		List<NiftyDailyCandle> niftyCandles = niftyRepo.findAllAfterDate(LocalDate.of(2009, 1, 1));
		List<TradeHistoryReport> tradeHistoryList = backtestService.backtestStock(stockSymbol, niftyCandles, 10000,
				0.05);
		tradeHistoryReportRepository.saveAll(tradeHistoryList);
		return ResponseEntity.ok(tradeHistoryList);
	}

	@GetMapping("/findSetupsForVolumePriceBreakOut")
	public ResponseEntity<String> swingTradeForVolumeBreakOutSetupsForDate() {
		vpBreakOutService.findTradeSetupsForVolumePrice();
		return ResponseEntity.ok("Completed");
	}

	@GetMapping("/recalculateEntriesForUnexecutedTrades")
	public ResponseEntity<String> recalculateEntriesForUnexecutedTrades() {
		vpBreakOutService.recalculateEntriesForUnexecutedTrades();
		return ResponseEntity.ok("Completed");
	}

	@GetMapping("/recalculateTSLForExecutedTrades")
	public void updateTrailingStopLoss() {
		vpBreakOutService.recalculateTrailingStopLossForExecutedTrades();
	}
	
	@GetMapping("/placeGTTBuyOrderForAll")
	public ResponseEntity<String> placeGTTOrderForAll() {
		vpBreakOutService.checkAndPlaceOrModifyAllEntryGTTOrders();
		return ResponseEntity.ok("Completed");
	}

	@GetMapping("/placeGTTBuyOrderForSingle")
	public ResponseEntity<String> placeGTTOrderForBuy(@RequestParam Long id) {
		Optional<TradeSetup> tradeSetupOpt = tradeSetupRepository.findById(id);
		if (tradeSetupOpt.isPresent()) {
			tradeManagerService.checkAndPlaceOrModifyEntryGTTOrder(tradeSetupOpt.get());

		}
		return ResponseEntity.ok("Completed");
	}

	@GetMapping("/placeGTTSellOrderAfterEntry")
	public ResponseEntity<String> placeGTTOrderAfterEntryForSell(@RequestParam(required = false) Long id) {
		vpBreakOutService.placeGTTSellOrderAfterEntry(id);
		return ResponseEntity.ok("Completed");
	}
	
	@GetMapping("/modifyGTTSellOrdersForUpdatedTSL")
	public ResponseEntity<String> updateGTTOrdersForUpdatedTSL() {
		vpBreakOutService.modifyGTTSellOrdersForUpdatedTSL();
		return ResponseEntity.ok("Success");
	}
	
	@GetMapping("/processPendingEntriesAgainstHoldings")
	public ResponseEntity<String> processPendingEntriesAgainstHoldings() {
		vpBreakOutService.processPendingEntriesAgainstHoldings();
		return ResponseEntity.ok("Success");
	}
	
	@GetMapping("/checkAndProcessUnexecutedTradesForHigh")
	public ResponseEntity<String> checkAndProcessUnexecutedTradesForHigh() {
		vpBreakOutService.checkAndProcessUnexecutedTradesForHigh();
		return ResponseEntity.ok("Success");
	}
	
	
	@GetMapping("/setInitialHighTradeSetup")
	public ResponseEntity<String> setInitialHighTradeSetup() {
		vpBreakOutService.setInitialHighTradeSetup();
		return ResponseEntity.ok("Success");
	}
	
	@GetMapping("/processAfterTarget1")
	public ResponseEntity<String> processAfterTarget1() {
		vpBreakOutService.processAfterTarget1ForAll();
		return ResponseEntity.ok("Success");
	}
}
	
