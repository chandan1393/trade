package com.fyers.fyerstrading.service.swing.priceVolume;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fyers.fyerstrading.entity.NiftyDailyCandle;
import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.entity.StockTechnicalIndicator;
import com.fyers.fyerstrading.entity.TradeHistoryReport;
import com.fyers.fyerstrading.model.RankedTradeCandidate;
import com.fyers.fyerstrading.model.TradeExecution;
import com.fyers.fyerstrading.repo.NiftyDailyCandleRepo;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.repo.TradeHistoryReportRepository;
import com.fyers.fyerstrading.utility.TradingUtil;

@Service
public class BacktestServiceForVolumeAndPriceBreakOut {

	@Autowired
	NiftyDailyCandleRepo niftyRepo;

	private static final Logger logger = LoggerFactory.getLogger(BacktestServiceForVolumeAndPriceBreakOut.class);

	private final StockMasterRepository stockMasterRepository;
	private final StockDailyPriceRepository stockDataRepository;
	private final TradeHistoryReportRepository tradeHistoryRepository;
	private final StrategyForVolumePriceBreakOut strategyForVolumePriceBreakOut;

	public BacktestServiceForVolumeAndPriceBreakOut(StockMasterRepository stockMasterRepository,
			StockDailyPriceRepository stockDataRepository, TradeHistoryReportRepository tradeHistoryRepository,
			StrategyForVolumePriceBreakOut strategyForVolumePriceBreakOut) {
		this.stockMasterRepository = stockMasterRepository;
		this.stockDataRepository = stockDataRepository;
		this.tradeHistoryRepository = tradeHistoryRepository;
		this.strategyForVolumePriceBreakOut = strategyForVolumePriceBreakOut;
	}

	@Transactional
	public void backtestCategory() {
		logger.error("backtestCategory started");
		double initialCapital = 10000;
		double riskPerTrade = 0.05;
		try {
			List<NiftyDailyCandle> niftyCandles = niftyRepo.findAllAfterDate(LocalDate.of(2000, 1, 1));

			List<String> symbols = stockMasterRepository.findAll().stream().map(StockMaster::getSymbol)
					.collect(Collectors.toList());

			// List<String> symbols =
			// stockMasterRepository.findStocksByIndexSymbol("NSE:NIFTYLARGEMID250-INDEX").stream().map(StockMaster::getSymbol)
			// .collect(Collectors.toList());

			List<TradeHistoryReport> allTradeReports = Collections.synchronizedList(new ArrayList<>());

			symbols.parallelStream().forEach(symbol -> {
				try {
					List<TradeHistoryReport> trades = backtestStock(symbol, niftyCandles, initialCapital, riskPerTrade);
					allTradeReports.addAll(trades);
				} catch (Exception e) {
					logger.error("Error processing stock {}: {}", symbol, e.getMessage());
				}
			});

			tradeHistoryRepository.saveAll(allTradeReports);

		} catch (Exception e) {
			logger.error("Error in backtesting category: {}", e.getMessage());
		}
	}

	public List<TradeHistoryReport> backtestStock(String symbol, List<NiftyDailyCandle> niftyCandles,
			double initialCapital, double riskPerTrade) {
		List<StockDailyPrice> data = stockDataRepository.findAllWithIndicatorsAfterDate(symbol,
				LocalDate.of(2000, 1, 1));
		if (data.size() < 200)
			return Collections.emptyList();

		List<TradeHistoryReport> tradeList = new ArrayList<>();
		boolean isInTrade = false;
		StockDailyPrice pendingEntry = null;
		int holdingDays = 0;
		final int MAX_HOLDING_DAYS = 30;
		TradeExecution trade = null;
		for (int i = 63; i < data.size(); i++) {
			StockDailyPrice prev = data.get(i - 1);
			StockDailyPrice current = data.get(i);

			if (pendingEntry != null && !isInTrade) {
				// Check if entry condition met
				if (current.getLowPrice() <= trade.getEntryPrice()) {
					trade.setEntryDate(current.getTradeDate());
					isInTrade = true;
					pendingEntry = null;
					holdingDays = 0;
				} else {
					// Check if the price moved too far above entry, indicating missed opportunity
					double atr = current.getTechnicalIndicator().getAtr();
					if ((current.getClosePrice() - trade.getEntryPrice()) > 3 * atr) {
						pendingEntry = null;
						// trade = null;
					} else {
						// Still in valid zone, re-calculate updated entry parameters
						trade = calculateEntryCondition(trade, current, prev, initialCapital, riskPerTrade, symbol,
								trade.getTradeRank(), trade.isSecondBreakOut());
					}
				}
			}

			if (pendingEntry == null && !isInTrade) {
				boolean isValid = strategyForVolumePriceBreakOut.isValidSetup(data.subList(i - 62, i + 1),
						niftyCandles);
				if (isValid) {
					int score = TradingUtil.calculateSetupRank(data.subList(i - 62, i + 1), current);
					boolean isSecondBreakOut = isSecondBreakoutAttempt(data.subList(i - 62, i + 1));
					trade = calculateEntryCondition(new TradeExecution(), current, prev, initialCapital, riskPerTrade,
							symbol, score, isSecondBreakOut);
					pendingEntry = current;

				}
			}

			if (isInTrade) {
				trade = checkForExitTrade(current, prev, data.get(i - 2), trade);
				holdingDays++;

				if (trade.getExitDate() != null || holdingDays >= MAX_HOLDING_DAYS) {
					if (holdingDays >= MAX_HOLDING_DAYS) {
						trade.setExitDate(current.getTradeDate());
						trade.setExitPrice(current.getClosePrice());
						trade.setExitReason("Max holding period reached");
						trade.setProfitLoss((trade.getExitPrice() - trade.getEntryPrice()) * trade.getPositionSize());
					}

					double pnl = trade.getProfitLoss();
					tradeList.add(new TradeHistoryReport(symbol, trade.getEntryDate().toString(), trade.getEntryPrice(),
							trade.getExitDate().toString(), trade.getExitPrice(), pnl > 0 ? "Profit" : "Loss",
							trade.getExitReason(), pnl, trade.getPositionSize(), "",
							trade.getTradeFoundDate().toString(), trade.getTradeRank(), trade.isSecondBreakOut()));

					isInTrade = false;
					trade = null;
					holdingDays = 0;
				}
			}

		}

		return tradeList;
	}

	public boolean isSecondBreakoutAttempt(List<StockDailyPrice> data) {
		if (data.size() < 35)
			return false; // Need at least 35 candles: 20 for resistance + 10 for breakout/pullback +
							// current

		// Step 1: Identify resistance from 20 candles before the last 5
		int resistanceEndIndex = data.size() - 5;
		int resistanceStartIndex = resistanceEndIndex - 20;
		List<StockDailyPrice> resistanceLookback = data.subList(resistanceStartIndex, resistanceEndIndex);
		double resistance = resistanceLookback.stream().mapToDouble(StockDailyPrice::getHighPrice).max().orElse(0);

		// Step 2: Check breakout and pullback in the last 10 candles before current
		boolean firstBreakout = false;
		boolean pullback = false;
		for (int i = data.size() - 11; i < data.size() - 1; i++) {
			double close = data.get(i).getClosePrice();
			if (!firstBreakout && close > resistance) {
				firstBreakout = true;
			} else if (firstBreakout && close < resistance) {
				pullback = true;
			}
		}

		// Step 3: Check if current candle is again breaking out
		double currentClose = data.get(data.size() - 1).getClosePrice();
		return firstBreakout && pullback && currentClose >= resistance * 0.99;
	}

	

	private TradeExecution calculateEntryCondition(TradeExecution previousTrade, StockDailyPrice current,
			StockDailyPrice prev, double capital, double baseRiskPerTrade, String symbol, int score,
			boolean isSecondBreakOut) {

		// --- Step 1: Entry Price Calculation ---
		double bufferPercentage = 0.005;
		double ema9 = current.getTechnicalIndicator().getEma9();
		double entryPrice = ema9 * (1 + bufferPercentage);

		double atr = current.getTechnicalIndicator().getAtr();
		double atrMultiplier = 1.0;
		double defaultStopLoss = Math.min(entryPrice - (atr * atrMultiplier), prev.getLowPrice());

		double stopLoss = (previousTrade != null && previousTrade.getStopLoss() != null) ? previousTrade.getStopLoss()
				: defaultStopLoss;

		if (stopLoss >= entryPrice)
			return null; // Invalid setup

		double riskPerShare = entryPrice - stopLoss;

		// --- Step 2: Dynamic Target Multipliers Based on Score ---
		double target1Multiplier;
		double target2Multiplier;
		double riskMultiplier;

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

		double target1 = entryPrice + (riskPerShare * target1Multiplier);
		double target2 = entryPrice + (riskPerShare * target2Multiplier);

		// --- Step 3: Position Size ---
		double riskAmount = capital * adjustedRiskPerTrade;
		int positionSize = (int) Math.floor(riskAmount / riskPerShare);
		positionSize = Math.max(1, positionSize); // Ensure minimum position size of 1

		// --- Step 4: Prepare TradeExecution Object ---
		LocalDate tradeFoundDate = (previousTrade != null && previousTrade.getTradeFoundDate() != null)
				? previousTrade.getTradeFoundDate()
				: current.getTradeDate();

		TradeExecution execution = new TradeExecution(symbol, null, // entryDate set when triggered
				entryPrice, stopLoss, target1, target2, positionSize, false, tradeFoundDate);

		execution.setTradeRank(score);
		execution.setSecondBreakOut(isSecondBreakOut);

		return execution;
	}


	
	private TradeExecution checkForExitTrade(StockDailyPrice current, StockDailyPrice prev, StockDailyPrice prevPrev,
			TradeExecution trade) {
		double exitPrice = 0, profitLoss = 0;
		String exitReason = "";
		LocalDate exitDate;

		Double trailingStopLoss = trade.getTrailingStopLoss();
		double stopLoss = trade.getStopLoss();
		double target1 = trade.getTarget1();
		double target2 = trade.getTarget2();
		double entryPrice = trade.getEntryPrice();
		boolean isPartialExit = trade.isPartialExit();

		// Step 1: Check if Initial Stop-Loss is Hit
		if ((trailingStopLoss == null || trailingStopLoss == 0) && current.getLowPrice() <= stopLoss) {
			exitPrice = stopLoss;
			exitReason = "Initial Stop-Loss Hit";
		}

		// Step 4: Allow Partial Profit-Taking at Target 1
		if (!isPartialExit && current.getHighPrice() >= target1) {
			trade.setPartialExit(true);
		}

		// Step 5: Check if Trailing Stop-Loss is Hit
		if (trailingStopLoss != null && trailingStopLoss > 0 && current.getLowPrice() <= trailingStopLoss) {
			exitPrice = isPartialExit ? (target1 * 0.6 + trailingStopLoss * 0.4) // Weighted exit price
					: trailingStopLoss;
			exitReason = "Trailing Stop-Loss Hit";
		}

		// Step 6: Check if Target 2 is Reached (Full Exit)
		if (isPartialExit && current.getHighPrice() >= target2) {
			exitPrice = target2;
			exitReason = "Target 2 Achieved";
		}

		// Step 7: Finalize Trade Exit
		if (exitPrice > 0) {
			exitDate = current.getTradeDate();
			profitLoss = (exitPrice - entryPrice) * trade.getPositionSize();
			trade.setExitDetails(exitDate, exitPrice, exitReason, profitLoss);
			return trade;
		}

		return calculateTrailingStopLoss(current, prev, prevPrev, trade);
	}

	@Transactional
	private TradeExecution calculateTrailingStopLoss(StockDailyPrice current, StockDailyPrice prev,
			StockDailyPrice prevPrev, TradeExecution trade) {

		double atr = current.getTechnicalIndicator().getAtr();
		double entryPrice = trade.getEntryPrice();
		double trailingStopLoss = trade.getTrailingStopLoss() != null ? trade.getTrailingStopLoss()
				: trade.getStopLoss();
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
			trade.setTrailingStopLoss(updatedSL);
		}

		return trade;
	}

}