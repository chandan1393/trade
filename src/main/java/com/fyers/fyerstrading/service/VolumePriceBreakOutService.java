package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fyers.fyerstrading.entity.NiftyDailyCandle;
import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.entity.TradeExecution;
import com.fyers.fyerstrading.entity.TradeExitReason;
import com.fyers.fyerstrading.entity.TradeSetup;
import com.fyers.fyerstrading.entity.TradeStatus;
import com.fyers.fyerstrading.model.Holding;
import com.fyers.fyerstrading.model.HoldingsResponse;
import com.fyers.fyerstrading.model.NetPosition;
import com.fyers.fyerstrading.model.Position;
import com.fyers.fyerstrading.model.PositionResponse;
import com.fyers.fyerstrading.repo.NiftyDailyCandleRepo;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.repo.TradeExecutionRepository;
import com.fyers.fyerstrading.repo.TradeSetupRepository;
import com.fyers.fyerstrading.service.swing.priceVolume.StrategyForVolumePriceBreakOut;
import com.fyers.fyerstrading.service.swing.priceVolume.TradeManagerService;
import com.fyers.fyerstrading.utility.TradeConstants;
import com.fyers.fyerstrading.utility.TradingUtil;

@Service
public class VolumePriceBreakOutService {

	private static final Logger logger = LoggerFactory.getLogger(VolumePriceBreakOutService.class);
	private static final double INITIAL_CAPITAL = 10000;
	private static final double RISK_PER_TRADE = 0.05;

	@Autowired
	private StockMasterRepository stockMasterRepository;

	@Autowired
	private StockDailyPriceRepository stockDailyPriceRepository;

	@Autowired
	private TradeSetupRepository tradeSetupRepo;

	@Autowired
	private TradeExecutionRepository tradeExecutionRepo;

	@Autowired
	private StrategyForVolumePriceBreakOut strategyForVolumePriceBreakOut;

	@Autowired
	private TradeManagerService tradeManagerService;

	@Autowired
	private FyersApiService fyersApiService;

	public void findTradeSetupsForVolumePrice() {
		LocalDate latestTradeFoundDate = tradeSetupRepo.findLatestTradeDate();
		LocalDate today = LocalDate.now();

		// if nothing in DB, fall back to daysBack
		LocalDate startDate = (latestTradeFoundDate != null) ? latestTradeFoundDate.plusDays(1) : today.minusDays(10);

		for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {
			findTradeSetupsForVolumePrice(date);
		}
	}

	private void findTradeSetupsForVolumePrice(LocalDate date) {
		logger.info("Finding trade setups for date: {}", date);
		List<String> symbols = stockMasterRepository.findAll().stream().map(StockMaster::getSymbol)
				.collect(Collectors.toList());
		Map<String, String> uniqueStockMap = new LinkedHashMap<>();
		for (String symbol : symbols) {
			uniqueStockMap.putIfAbsent(symbol, symbol);
		}

		long countForCurrentDate = stockDailyPriceRepository.countByTradeDate(date);

		if (countForCurrentDate < 1)
			return;

		List<TradeSetup> allSetups = uniqueStockMap.values().parallelStream()
				.map(stock -> evaluateTradeSetupForSymbol(stock, date, null)).filter(Objects::nonNull)
				.collect(Collectors.toList());

		// --- Deduplicate by symbol ---
		Map<String, TradeSetup> uniqueSetups = allSetups.stream()
				.collect(Collectors.toMap(TradeSetup::getStockSymbol, setup -> setup,
						(existing, replacement) -> existing, // Keep first
						LinkedHashMap::new));

		if (!uniqueSetups.isEmpty()) {
			List<TradeSetup> setUpList = new ArrayList<>(uniqueSetups.values());
			tradeSetupRepo.saveAll(setUpList);
			logger.info("Saved {} unique trade setups for date {}", uniqueSetups.size(), date);
		} else {
			logger.info("No valid trade setups found for date {}", date);
		}
	}

	private TradeSetup evaluateTradeSetupForSymbol(String symbol, LocalDate date, List<NiftyDailyCandle> niftyCandles) {
		try {
			List<StockDailyPrice> data = stockDailyPriceRepository.findBTWDateWithIndicators(symbol,
					date.minusDays(100), date.plusDays(1));

			if (data == null || data.size() < 1 || data.size() < 63)
				return null;

			StockDailyPrice current = data.get(data.size() - 1);
			StockDailyPrice prev = data.get(data.size() - 2);

			if (!current.getTradeDate().isEqual(date)) {
				logger.error("Current date is diff from trade date for symbol {} on date {}", symbol, date);
				return null;
			}

			if (current.getDeliveryPercent() != null && current.getDeliveryPercent() < 25) {
				return null;
			}

			List<StockDailyPrice> recent = getRecentDataWindow(data, 63);

			if (!strategyForVolumePriceBreakOut.isValidSetup(recent, niftyCandles))
				return null;

			int score = TradingUtil.calculateSetupRank(recent, recent.get(recent.size() - 1));

			TradeSetup setup = prepareInitialEntrySetup(current, prev, INITIAL_CAPITAL, RISK_PER_TRADE, score, symbol);

			setup.setCapitalPlanned(
					TradingUtil.roundToTwoDecimalPlaces(setup.getEntryPrice() * setup.getPositionSize()));

			if (setup != null)
				logger.info("Setup found for {}", symbol);
			return setup;

		} catch (Exception ex) {
			logger.error("Error evaluating setup for symbol {} on date {}: {}", symbol, date, ex.getMessage(), ex);
			return null;
		}
	}

	
	
	

	
	
	
	private List<StockDailyPrice> getRecentDataWindow(List<StockDailyPrice> data, int days) {
		return data.subList(data.size() - days, data.size());
	}

	public void checkAndPlaceOrModifyAllEntryGTTOrders() {
		List<TradeSetup> setups = tradeSetupRepo.findUnexecutedAndPendingEntrySetups();
		for (TradeSetup setup : setups) {
			try {
				tradeManagerService.checkAndPlaceOrModifyEntryGTTOrder(setup);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void placeGTTSellOrderAfterEntry(Long id) {

		if (id != null) {

			Optional<TradeExecution> tradeExecutionOpt = tradeExecutionRepo.findById(id);
			if (tradeExecutionOpt.isPresent()) {
				tradeManagerService.gttSellOrderAfterEntry(tradeExecutionOpt.get());

			}
		} else {

			List<TradeExecution> entredTradeList = tradeExecutionRepo
					.findByEntryExecutedTrueAndTradeStatus(TradeStatus.ENTERED);
			for (TradeExecution entredTrade : entredTradeList) {
				tradeManagerService.gttSellOrderAfterEntry(entredTrade);
			}

		}
	}

	@Transactional
	public void recalculateEntriesForUnexecutedTrades() {
		List<TradeSetup> setups = tradeSetupRepo.findUnexecutedAndPendingEntrySetupsBeforeDate(LocalDate.now());

		List<TradeSetup> updatedSetups = new ArrayList<>();

		for (TradeSetup setup : setups) {
			List<StockDailyPrice> data = stockDailyPriceRepository.findAllBWDate(setup.getStockSymbol(),
					LocalDate.now().minusDays(50), LocalDate.now().plusDays(1));

			if (data == null || data.size() < 2)
				continue;

			StockDailyPrice current = data.get(data.size() - 1);
			StockDailyPrice prev = data.get(data.size() - 2);

			if (setup.getLastEvaluatedDate() != null && !current.getTradeDate().isAfter(setup.getLastEvaluatedDate()))
				continue;

			if (setup.getStopLoss() > current.getLowPrice()) {
				rejectTradeSetup(setup, "Rejected due to stoploss gt current close");

			} else if (current.getHighPrice() > setup.getInitialHigh() * 1.10) {
				rejectTradeSetup(setup, "Rejected due to price >10% of entry price");

			} else {
				TradeSetup updatedSetup = recalculateEntryCondition(setup, current, prev, INITIAL_CAPITAL,
						RISK_PER_TRADE, setup.getTradeRank());
				updatedSetup.setLastEvaluatedDate(current.getTradeDate());

				updatedSetup.setCapitalPlanned(TradingUtil
						.roundToTwoDecimalPlaces(updatedSetup.getEntryPrice() * updatedSetup.getPositionSize()));
				updatedSetups.add(updatedSetup);

			}

		}

		if (!updatedSetups.isEmpty()) {
			tradeSetupRepo.saveAll(updatedSetups);
			logger.info("Recalculated and saved {} unexecuted trade setups", updatedSetups.size());
		} else {
			logger.info("No valid setups to recalculate for date {}", LocalDate.now());
		}
	}

	@Transactional
	public void recalculateTrailingStopLossForExecutedTrades() {
		LocalDate date = LocalDate.now();

		List<TradeExecution> executions = tradeExecutionRepo
				.findByEntryExecutedAndActiveStatuses(TradeConstants.ACTIVE_STATUSES);
		List<TradeExecution> updatedExecutions = new ArrayList<>();

		for (TradeExecution exec : executions) {
			try {
				TradeSetup setup = exec.getTradeSetup();
				if (setup == null)
					continue;

				List<StockDailyPrice> data = stockDailyPriceRepository.findAllBWDate(exec.getStockSymbol(),
						date.minusDays(20), date.plusDays(1));

				if (data == null || data.isEmpty())
					continue;

				// Perform trailing stop-loss calculation
				TradeExecution updated = calculateTrailingStopLoss(exec, data);

				// Add to batch update list
				updatedExecutions.add(updated);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}

		if (!updatedExecutions.isEmpty()) {
			tradeExecutionRepo.saveAll(updatedExecutions);
			logger.info("Trailing stop-loss recalculated and updated for {} trades", updatedExecutions.size());
		}
	}

	public TradeSetup prepareInitialEntrySetup(StockDailyPrice current, StockDailyPrice prev, Double capital,
			Double baseRiskPerTrade, int score, String symbol) {
		TradeSetup trade = new TradeSetup();
		trade.setStockSymbol(symbol);
		trade.setTradeFoundDate(current.getTradeDate());
		trade.setTradeStatus(TradeStatus.SETUP_FOUND);
		trade.setTradeEntered(false);
		trade.setIsActive(true);
		trade.setInitialHigh(current.getHighPrice());
		return buildEntryDetails(trade, current, prev, capital, baseRiskPerTrade, score);
	}

	public TradeSetup recalculateEntryCondition(TradeSetup existingTrade, StockDailyPrice current, StockDailyPrice prev,
			Double capital, Double baseRiskPerTrade, int score) {
		return buildEntryDetails(existingTrade, current, prev, capital, baseRiskPerTrade, score);
	}

	private TradeSetup buildEntryDetails(TradeSetup trade, StockDailyPrice current, StockDailyPrice prev,
			Double capital, Double baseRiskPerTrade, int score) {

		double bufferPercentage = 0.005;
		double ema9 = current.getTechnicalIndicator().getEma9();
		double entryPrice = ema9 * (1 + bufferPercentage);

		double atr = current.getTechnicalIndicator().getAtr();
		double atrMultiplier = 1.0;
		double stopLossByATR = entryPrice - (atr * atrMultiplier);
		double stopLossByPrevLow = prev.getLowPrice();
		double initialStopLoss = Math.min(stopLossByATR, stopLossByPrevLow);

		if (initialStopLoss >= entryPrice)
			return null;

		double riskMultiplier, target1Multiplier, target2Multiplier;
		if (score >= 40) {
			riskMultiplier = 1.5;
			target1Multiplier = 3.5;
			target2Multiplier = 7.0;
		} else if (score >= 30) {
			riskMultiplier = 1.25;
			target1Multiplier = 3.0;
			target2Multiplier = 6.0;
		} else if (score >= 20) {
			riskMultiplier = 1.0;
			target1Multiplier = 2.5;
			target2Multiplier = 5.0;
		} else if (score >= 10) {
			riskMultiplier = 0.75;
			target1Multiplier = 2.0;
			target2Multiplier = 4.0;
		} else {
			riskMultiplier = 0.5;
			target1Multiplier = 1.5;
			target2Multiplier = 3.0;
		}

		double adjustedRiskPerTrade = baseRiskPerTrade * riskMultiplier;
		double riskPerShare = entryPrice - initialStopLoss;
		if (riskPerShare <= 0)
			return null;

		double riskAmount = capital * adjustedRiskPerTrade;
		int positionSize = (int) Math.floor(riskAmount / riskPerShare);
		positionSize = Math.max(1, positionSize);

		// Ensure we don’t exceed available capital
		int maxSharesByCapital = (int) Math.floor(capital / entryPrice);
		positionSize = Math.min(positionSize, maxSharesByCapital);

		if (positionSize <= 0)
			return null;

		double target1 = entryPrice + (riskPerShare * target1Multiplier);
		double target2 = entryPrice + (riskPerShare * target2Multiplier);

		// Set all calculated values
		trade.setEntryPrice(TradingUtil.roundToTwoDecimalPlaces(entryPrice));
		trade.setStopLoss(TradingUtil.roundToTwoDecimalPlaces(initialStopLoss));
		trade.setTarget1(TradingUtil.roundToTwoDecimalPlaces(target1));
		trade.setTarget2(TradingUtil.roundToTwoDecimalPlaces(target2));
		trade.setPositionSize(positionSize);
		trade.setTradeRank(score);
		trade.setDeliveryPercent(current.getDeliveryPercent());

		return trade;
	}

	public TradeExecution calculateTrailingStopLoss(TradeExecution trade, List<StockDailyPrice> data) {
		if (data == null || data.isEmpty())
			return trade;

		StockDailyPrice current = data.get(data.size() - 1);
		StockDailyPrice prev = data.get(data.size() - 2);
		StockDailyPrice prevPrev = data.get(data.size() - 3);
		double atr = current.getTechnicalIndicator().getAtr();
		double entryPrice = trade.getEntryPrice();

		double trailingStopLoss = trade.getStopLoss();
		double currentPrice = current.getClosePrice();

		// 1. Activation: Price must move at least 2.5× ATR in profit
		double activationThreshold = entryPrice + (2 * atr);
		if (current.getHighPrice() < activationThreshold)
			return trade;

		// 2. RSI momentum filter: ensure uptrend continuation
		double currentRSI = current.getTechnicalIndicator().getRsi();
		double prevRSI = prev.getTechnicalIndicator().getRsi();
		double prevPrevRSI = prevPrev.getTechnicalIndicator().getRsi();
		if (currentRSI < prevRSI || prevRSI < prevPrevRSI) // weakening momentum
			return trade;

		// 3. Candle quality filter (skip weak/indecision bars)
		double body = Math.abs(current.getClosePrice() - current.getOpenPrice());
		double range = current.getHighPrice() - current.getLowPrice();
		boolean isIndecision = body < 0.3 * range;
		boolean isInsideBar = current.getHighPrice() <= prev.getHighPrice()
				&& current.getLowPrice() >= prev.getLowPrice();
		if (isIndecision || isInsideBar)
			return trade;

		// 4. Tiered Trailing SL Logic
		double move = current.getHighPrice() - entryPrice;
		double newSL;

		if (move >= 6 * atr) {
			newSL = Math.max(prev.getLowPrice(), currentPrice - (1.2 * atr)); // very aggressive
		} else if (move >= 4 * atr) {
			newSL = Math.max(prev.getLowPrice(), currentPrice - (1.8 * atr)); // semi-aggressive
		} else {
			newSL = Math.max(prev.getLowPrice(), currentPrice - (2.3 * atr)); // conservative
		}

		// 5. Breakeven SL to avoid losses
		double breakevenSL = entryPrice + (0.75 * atr);
		double updatedSL = Math.max(newSL, breakevenSL);

		// 6. Only tighten the SL (never widen)
		if (updatedSL > trailingStopLoss) {
			trade.setStopLoss(TradingUtil.roundToTwoDecimalPlaces(updatedSL));
		}
		trade.setUpdatedAt(LocalDateTime.now());

		return trade;
	}

	public void processTrade(Position position) {

		if (position.getSide() == 1) {
			String symbol = position.getSymbol();
			int tradedQty = position.getQty();
			double tradedPrice = position.getNetAvg();

			handleTradeEntry(symbol, tradedQty, tradedPrice);
			return;
		}

		String symbol = position.getSymbol();
		int tradedQty = position.getQty();
		double tradedPrice = position.getSellAvg();

		handleExit(symbol, tradedQty, tradedPrice);
	}

	@Transactional
	public void handleTradeEntry(String symbol, int tradedQty, double tradedPrice) {

		Optional<TradeSetup> pendingEntry = tradeSetupRepo.findBySymbolQtyAndStatus(symbol, tradedQty,
				TradeStatus.PENDING_ENTRY);

		if (pendingEntry.isEmpty()) {
			System.out.printf("No matching TradeSeup found for entry: %s, qty=%d%n", symbol, tradedQty);
			return;
		}
		TradeSetup setup = pendingEntry.get();

		TradeExecution execution = new TradeExecution();

		execution.setStockSymbol(setup.getStockSymbol());
		execution.setTradeSetup(setup);
		execution.setPositionSize(setup.getPositionSize());
		execution.setEntryExecuted(true);
		execution.setTarget1(setup.getTarget1());
		execution.setTarget2(setup.getTarget2());
		execution.setStopLoss(setup.getStopLoss());
		execution.setCreatedAt(LocalDateTime.now());
		execution.setEntryPrice(TradingUtil.roundToTwoDecimalPlaces(tradedPrice));
		execution.setEntryDate(LocalDate.now());
		execution.setOrderExecutedTime(LocalDateTime.now());
		execution.setTradeStatus(TradeStatus.ENTERED);
		execution.setRemarks("Entry executed at: " + LocalDateTime.now());
		execution.setSyncTime(LocalDateTime.now());
		tradeExecutionRepo.save(execution);

		setup.setTradeEntered(true);
		setup.setTradeStatus(TradeStatus.ENTERED);
		setup.setSyncTime(LocalDateTime.now());
		tradeSetupRepo.save(setup);

		try {
			tradeManagerService.gttSellOrderAfterEntry(execution);

		} catch (Exception e) {
			System.err.println("Failed to place exit GTT orders for " + symbol + ": " + e.getMessage());
		}

	}

	public void handleExit(String symbol, int tradedQty, double tradedPrice) {

		Set<TradeStatus> eligibleForSaleStatus = Set.of(TradeStatus.GTT_SELL_ORDERS_PLACED, TradeStatus.TARGET1_HIT,
				TradeStatus.GTT_ORDER_MODIFIED_AFTER_T1);

		Optional<TradeExecution> executedTrade = tradeExecutionRepo.findBySymbolAndStatus(symbol,
				eligibleForSaleStatus);
		if (executedTrade.isEmpty()) {
			System.out.printf("No matching TradeExecution found for entry: %s, qty=%d%n", symbol, tradedQty);
			return;
		}

		TradeExecution exec = executedTrade.get();

		int remainingQty = exec.getPositionSize() - (exec.getTotalExitedQty() == null ? 0 : exec.getTotalExitedQty());
		if (remainingQty <= 0) {
			System.out.println("Already fully exited for " + symbol);
			return;
		}

		if (!exec.isStopLossHit() && tradedPrice <= Math.floor(exec.getStopLoss())) {
			handleStopLossHit(exec, tradedQty, tradedPrice);
		} else if (!exec.isTarget1Hit() && tradedPrice >= Math.floor(exec.getTarget1())) {
			handleTarget1Hit(exec, tradedQty, tradedPrice);
		} else if (!exec.isTarget2Hit() && tradedPrice >= Math.floor(exec.getTarget2())) {
			handleTarget2Hit(exec, tradedQty, tradedPrice);
		}
	}

	@Transactional
	private void handleStopLossHit(TradeExecution exec, int qty, double price) {
		exec.setStopLossHit(true);
		exec.setExitedQtyTSL(qty);
		exec.setExitPriceTSL(TradingUtil.roundToTwoDecimalPlaces(price));
		exec.setExitDateTSL(LocalDate.now());
		exec.setRemarks(exec.isTarget1Hit() ? "Trailing Stoploss Hit" : "Stop Loss hit");
		exec.setExitReason(exec.isTarget1Hit() ? TradeExitReason.TSL : TradeExitReason.STOP_LOSS);
		exec.setTotalExitedQty((exec.getTotalExitedQty() == null ? 0 : exec.getTotalExitedQty()) + qty);
		exec.setFullyExited(true);

		double totalInvestment = exec.getPositionSize() * exec.getEntryPrice();
		double totalExitAmount = 0.0;

		if (exec.isTarget1Hit()) {
			double totalAmountAtTarget1 = exec.getExitPriceTarget1() * exec.getExitedQtyTarget1();
			double latestAmountAtSL = qty * price;
			totalExitAmount = totalAmountAtTarget1 + latestAmountAtSL;
		} else {
			totalExitAmount = qty * price;
		}

		exec.setTotalProfitLoss(TradingUtil.roundToTwoDecimalPlaces(totalExitAmount - totalInvestment));

		if (exec.getTotalExitedQty() >= exec.getPositionSize()) {
			exec.setTradeStatus(TradeStatus.CLOSED);
		}

		tradeExecutionRepo.save(exec);
		System.out.printf("Exit recorded for %s | Qty=%d | Price=%.2f%n", exec.getStockSymbol(), qty, price);
		// cancel target orders if SL is hit
		if (!exec.isTarget1Hit() && exec.getGttExitOrderT1Id() != null) {
			fyersApiService.cancelGTTOrder(exec.getGttExitOrderT1Id());
			exec.setSyncTime(LocalDateTime.now());
		}
		if (exec.getGttExitOrderT2Id() != null) {
			fyersApiService.cancelGTTOrder(exec.getGttExitOrderT2Id());
			exec.setSyncTime(LocalDateTime.now());
		}
		tradeExecutionRepo.save(exec);
	}

	@Transactional
	private void handleTarget1Hit(TradeExecution exec, int qty, double price) {
		exec.setTarget1Hit(true);
		exec.setExitedQtyTarget1(qty);
		exec.setExitPriceTarget1(TradingUtil.roundToTwoDecimalPlaces(price));
		exec.setExitDateTarget1(LocalDate.now());
		exec.setRemarks("Target 1 hit");
		exec.setTradeStatus(TradeStatus.TARGET1_HIT);
		exec.setTotalExitedQty(qty);
		tradeExecutionRepo.save(exec);

		processAfterTarget1(exec);

	}

	@Transactional
	public void handleTarget2Hit(TradeExecution exec, int qty, double exitPrice) {
		exec.setTarget2Hit(true);
		exec.setExitPriceTarget2(TradingUtil.roundToTwoDecimalPlaces(exitPrice));
		exec.setExitDateTarget2(LocalDate.now());
		exec.setFullyExited(true);
		exec.setTradeStatus(TradeStatus.CLOSED);
		exec.setRemarks("Trade fully exited at Target 2");
		exec.setTotalExitedQty(exec.getTotalExitedQty() + qty);
		double totalInvestment = exec.getPositionSize() * exec.getEntryPrice();
		double totalExitAmount = (exec.getExitedQtyTarget1() * exec.getExitPriceTarget1()) + (qty * exitPrice);
		exec.setTotalProfitLoss(TradingUtil.roundToTwoDecimalPlaces(totalExitAmount - totalInvestment));
		tradeExecutionRepo.save(exec);

		// Cancel any pending orders (SL/T1) since trade is closed
		fyersApiService.cancelGTTOrder(exec.getGttExitOrderTSLId());
		exec.setSyncTime(LocalDateTime.now());
		tradeExecutionRepo.save(exec);

	}

	public void modifyGTTSellOrdersForUpdatedTSL() {

		List<TradeExecution> enteredExecution = tradeExecutionRepo
				.findByEntryExecutedTrueAndTradeStatus(TradeStatus.GTT_SELL_ORDERS_PLACED);
		
		List<TradeExecution> target1HittedExecution = tradeExecutionRepo
				.findByEntryExecutedTrueAndTradeStatus(TradeStatus.GTT_ORDER_MODIFIED_AFTER_T1);

		for (TradeExecution exec : enteredExecution) {
			try {
				String id = tradeManagerService.placeOrModifyStopLossGTTOrder(exec, exec.getPositionSize());
				if (id != null && !id.equals("")) {
					exec.setSyncTime(LocalDateTime.now());
					exec.setRemarks(exec.getRemarks().concat(" updated sl" + exec.getStopLoss()));
					tradeExecutionRepo.save(exec);
				}
			} catch (Exception e) {

			}

		}

		for (TradeExecution exec : target1HittedExecution) {
			try {
				String id = tradeManagerService.placeOrModifyStopLossGTTOrder(exec,
						exec.getPositionSize() - exec.getExitedQtyTarget1());
				if (id != null && !id.equals("")) {
					exec.setUpdatedAt(LocalDateTime.now());
					exec.setRemarks(exec.getRemarks().concat(" updated sl" + exec.getStopLoss()));
					tradeExecutionRepo.save(exec);
				}
			} catch (Exception e) {
			}

		}

	}

	public void processPendingEntriesAgainstHoldings() {
		// 1. Fetch pending trades from DB
		List<TradeSetup> pendingTrades = tradeSetupRepo.findByStatus(TradeStatus.PENDING_ENTRY);

		if (pendingTrades.isEmpty()) {
			System.out.println("No pending trades to process.");
			return;
		}

		// 2. Fetch holdings from Fyers
		HoldingsResponse holdingsResponse = fyersApiService.getHoldings();
		if (holdingsResponse == null || holdingsResponse.getHoldings() == null) {
			System.err.println("No holdings fetched from Fyers.");
			return;
		}

		List<Holding> fyersHoldings = holdingsResponse.getHoldings();

		// 3. Compare and process
		for (TradeSetup trade : pendingTrades) {
			fyersHoldings.stream().filter(h -> h.getSymbol().equalsIgnoreCase(trade.getStockSymbol())
					&& h.getQuantity() == trade.getPositionSize()).findFirst().ifPresent(holding -> {
						// ✅ Process further if matched
						handleTradeEntry(holding.getSymbol(), holding.getQuantity(), holding.getCostPrice()

				);

					});
		}

	}

	public void checkAndProcessUnexecutedTradesForHigh() {
		List<TradeSetup> setups = tradeSetupRepo.findByStatus(TradeStatus.PENDING_ENTRY);
		List<TradeSetup> setupList = new ArrayList<>();

		for (TradeSetup setup : setups) {
			List<StockDailyPrice> data = stockDailyPriceRepository.findAllBWDate(setup.getStockSymbol(),
					setup.getTradeFoundDate(), LocalDate.now().plusDays(1));

			Optional<StockDailyPrice> tradeFoundRecord = stockDailyPriceRepository
					.findByTradeDate(setup.getStockSymbol(), setup.getTradeFoundDate());

			if (tradeFoundRecord.isEmpty() || data.isEmpty()) {
				System.out.println("No data found for " + setup.getStockSymbol() + " on " + setup.getTradeFoundDate());
				continue;
			}

			double basePrice = tradeFoundRecord.get().getHighPrice();

			double maxHighPrice = Math.max(basePrice,
					data.stream().mapToDouble(StockDailyPrice::getHighPrice).max().orElse(basePrice));

			if (maxHighPrice > basePrice * 1.10) {
				rejectTradeSetup(setup, "Rejected due to price >10% of entry price");

			} else {
				setup.setInitialHigh(basePrice);
				setupList.add(setup);
				System.out.println("Stock " + setup.getStockSymbol() + " did NOT cross 10% above base. Base="
						+ basePrice + ", MaxHigh=" + maxHighPrice);
			}

			tradeSetupRepo.saveAll(setupList);
		}
	}

	public void setInitialHighTradeSetup() {
		List<TradeSetup> setups = tradeSetupRepo.findAll();
		for (TradeSetup setup : setups) {
			Optional<StockDailyPrice> tradeFoundRecord = stockDailyPriceRepository
					.findByTradeDate(setup.getStockSymbol(), setup.getTradeFoundDate());

			if (tradeFoundRecord.isEmpty()) {
				System.out.println("No data found for " + setup.getStockSymbol() + " on " + setup.getTradeFoundDate());
				continue;
			}
			double basePrice = tradeFoundRecord.get().getHighPrice();
			setup.setInitialHigh(basePrice);
			tradeSetupRepo.save(setup);
		}
	}

	@Transactional
	public void rejectTradeSetup(TradeSetup setup, String notes) {
		setup.setNotes(notes);
		setup.setIsActive(false);
		setup.setTradeStatus(TradeStatus.REJECTED);
		setup.setLastEvaluatedDate(LocalDate.now());

		if (setup.getGttEntryOrderId() != null && !setup.getGttEntryOrderId().equals("")) {
			boolean isOrderCancelled = fyersApiService.cancelGTTOrder(setup.getGttEntryOrderId());
			if (isOrderCancelled) {
				setup.setNotes(notes.concat("  Order Cancelled SuccessFuly"));
				tradeSetupRepo.save(setup);

			}
		}

	}

	public void processAfterTarget1ForAll() {
		List<TradeExecution> tradeList = tradeExecutionRepo.findByTradeStatus(TradeStatus.TARGET1_HIT);
		for (TradeExecution trade : tradeList) {
			processAfterTarget1(trade);

		}

	}

	@Transactional
	public void processAfterTarget1(TradeExecution exec) {

		int modifiedQuantity = exec.getPositionSize() - exec.getExitedQtyTarget1();

		String tslGTTOrderId = tradeManagerService.placeOrModifyStopLossGTTOrder(exec, modifiedQuantity);
		String t2GTTOrderId = tradeManagerService.placeOrModifyTarget2GTTOrder(exec, modifiedQuantity);
		exec.setGttExitOrderT2Id(t2GTTOrderId);
		exec.setGttExitOrderTSLId(tslGTTOrderId);
		exec.setSyncTime(LocalDateTime.now());
		exec.setTradeStatus(TradeStatus.GTT_ORDER_MODIFIED_AFTER_T1);
		tradeExecutionRepo.save(exec);

	}

	public List<Holding> fetchHoldingTradesNotInDB() {

		List<TradeExecution> activeTrades = tradeExecutionRepo.findAllActive();

		if (activeTrades.isEmpty()) {
			System.out.println("No active trades in DB.");
		}

		HoldingsResponse holdingsResponse = fyersApiService.getHoldings();
		List<Holding> fyersHoldings = holdingsResponse.getHoldings();

		// 🧠 Adjust each trade’s effective position by subtracting exited quantities
		List<Holding> unmatchedHoldings = fyersHoldings.stream()
				.filter(holding -> activeTrades.stream().noneMatch(trade -> {
					int totalPosition = trade.getPositionSize();

					// handle partial exits (T1/T2 etc.)
					int exitedQty = 0;
					if (trade.isTarget1Hit())
						exitedQty += trade.getExitedQtyTarget1();

					int activePosition = totalPosition - exitedQty;

					// Compare symbol + active position with holdings
					return trade.getStockSymbol().equalsIgnoreCase(holding.getSymbol())
							&& activePosition == holding.getQuantity();
				})).collect(Collectors.toList());

		if (unmatchedHoldings.isEmpty()) {
			System.out.println("✅ All holdings are tracked in active trades.");
		} else {
			System.out.println("⚠️ Unmatched holdings: " + unmatchedHoldings);
		}
		return unmatchedHoldings;

	}

	public List<String> updatePositions() {
		PositionResponse positionResponse = fyersApiService.getPositions();
		List<NetPosition> netPositionList = positionResponse.getNetPositions();

		List<String> updatedSymbols = new ArrayList<>();

		for (NetPosition netPosition : netPositionList) {
			try {
				if (netPosition.getSide() == 1) {
					handleTradeEntry(netPosition.getSymbol(), netPosition.getQty(), netPosition.getBuyAvg());
					updatedSymbols.add(netPosition.getSymbol());
				} else if (netPosition.getSide() == -1) {
					handleExit(netPosition.getSymbol(), netPosition.getSellQty(), netPosition.getSellAvg());
					updatedSymbols.add(netPosition.getSymbol());
				}
			} catch (Exception e) {
				System.err.println("Error updating position for symbol: " + netPosition.getSymbol());
				e.printStackTrace();
			}
		}

		return updatedSymbols;
	}

}
