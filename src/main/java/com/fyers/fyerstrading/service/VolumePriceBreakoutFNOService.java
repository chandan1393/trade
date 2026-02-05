package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.FNO5MinCandle;
import com.fyers.fyerstrading.entity.NiftyDailyCandle;
import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.entity.StockTechnicalIndicator;
import com.fyers.fyerstrading.entity.TradeSetupForFNO;
import com.fyers.fyerstrading.entity.TradeStatus;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.repo.TradeExecutionRepository;
import com.fyers.fyerstrading.repo.TradeSetupFNORepository;
import com.fyers.fyerstrading.service.swing.priceVolume.StrategyForVolumePriceBreakOut;
import com.fyers.fyerstrading.service.swing.priceVolume.TradeManagerService;
import com.fyers.fyerstrading.utility.TradingUtil;

@Service
public class VolumePriceBreakoutFNOService {

	private static final Logger logger = LoggerFactory.getLogger(VolumePriceBreakOutService.class);
	private static final double INITIAL_CAPITAL = 10000;
	private static final double RISK_PER_TRADE = 0.05;

	@Autowired
	private StockMasterRepository stockMasterRepository;

	@Autowired
	private StockDailyPriceRepository stockDailyPriceRepository;

	@Autowired
	private TradeSetupFNORepository tradeSetupFnoRepo;

	@Autowired
	private StrategyForVolumePriceBreakOut strategyForVolumePriceBreakOut;

	public void findTradeSetupsForVolumePrice() {
		LocalDate latestTradeFoundDate = tradeSetupFnoRepo.findLatestTradeDate();
		LocalDate today = LocalDate.now();

		// if nothing in DB, fall back to daysBack
		LocalDate startDate = (latestTradeFoundDate != null) ? latestTradeFoundDate.plusDays(1) : today.minusDays(100);

		for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {
			findTradeSetupsForVolumePrice(date);
		}
	}

	private void findTradeSetupsForVolumePrice(LocalDate date) {

		logger.info("Finding F&O trade setups for date: {}", date);

		List<StockMaster> masters = stockMasterRepository.findByIsInFnoTrue();

		long countForCurrentDate = stockDailyPriceRepository.countByTradeDate(date);
		if (countForCurrentDate < 1)
			return;

		List<TradeSetupForFNO> setups = masters.stream() // ❌ no parallel
				.map(stock -> evaluateTradeSetupForFNOStocks(stock, date, null)).filter(Objects::nonNull)
				.collect(Collectors.toList());

		// Deduplicate by symbol
		Map<String, TradeSetupForFNO> unique = setups.stream()
				.collect(Collectors.toMap(TradeSetupForFNO::getStockSymbol, s -> s, (a, b) -> a, LinkedHashMap::new));

		if (!unique.isEmpty()) {
			tradeSetupFnoRepo.saveAll(unique.values());
			logger.info("Saved {} F&O setups for {}", unique.size(), date);
		} else {
			logger.info("No F&O setups for {}", date);
		}
	}

	private TradeSetupForFNO evaluateTradeSetupForFNOStocks(StockMaster master, LocalDate date,
			List<NiftyDailyCandle> niftyCandles) {

		try {
			List<StockDailyPrice> data = stockDailyPriceRepository.findBTWDateWithIndicators(master.getSymbol(),
					date.minusDays(100), date.plusDays(1));

			if (data == null || data.size() < 63)
				return null;

			StockDailyPrice current = data.get(data.size() - 1);
			StockDailyPrice prev = data.get(data.size() - 2);

			if (!current.getTradeDate().isEqual(date))
				return null;

			List<StockDailyPrice> recent = getRecentDataWindow(data, 63);

			if (!strategyForVolumePriceBreakOut.isValidSetupForFNO(recent))
				return null;

			int score = calculateFnoSetupRank(recent, recent.get(recent.size() - 1));

			TradeSetupForFNO setup = prepareInitialEntrySetup(current, prev, INITIAL_CAPITAL, RISK_PER_TRADE, score,
					master.getSymbol());

			if (setup == null)
				return null;

			logger.info("F&O setup {} score={}", master.getSymbol(), score);
			return setup;

		} catch (Exception ex) {
			logger.error("F&O setup error {} {}", master.getSymbol(), date, ex);
			return null;
		}
	}

	private List<StockDailyPrice> getRecentDataWindow(List<StockDailyPrice> data, int days) {
		return data.subList(data.size() - days, data.size());
	}

	public TradeSetupForFNO prepareInitialEntrySetup(StockDailyPrice current, StockDailyPrice prev, Double capital,
			Double baseRiskPerTrade, int score, String symbol) {

		TradeSetupForFNO trade = new TradeSetupForFNO();

		trade.setStockSymbol(symbol);
		trade.setTradeFoundDate(current.getTradeDate());
		trade.setTradeStatus(TradeStatus.SETUP_FOUND);
		trade.setTradeEntered(false);
		trade.setIsActive(true);
		trade.setTradeRank(score);
		trade.setNotes("FNO setup | score=" + score);
		trade.setDeliveryPercent(current.getDeliveryPercent() != null ? current.getDeliveryPercent() : 0);
		return buildEntryDetails(trade, current, prev, baseRiskPerTrade, score);
	}

	private TradeSetupForFNO buildEntryDetails(TradeSetupForFNO trade, StockDailyPrice current, StockDailyPrice prev,
			Double baseRiskPerTrade, int score) {

		double ema9 = current.getTechnicalIndicator().getEma9();
		double atr = current.getTechnicalIndicator().getAtr();

		// --- Entry ---
		double entryPrice = ema9 * 1.005;

		// --- Stop loss ---
		double slByATR = entryPrice - atr;
		double slByPrev = prev.getLowPrice();
		double stopLoss = Math.min(slByATR, slByPrev);

		if (stopLoss >= entryPrice)
			return null;

		double risk = entryPrice - stopLoss;

		// --- Target multipliers (OPTION-FRIENDLY) ---
		double t1Mult, t2Mult;

		if (score >= 40) {
			t1Mult = 2.5;
			t2Mult = 4.5;
		} else if (score >= 30) {
			t1Mult = 2.0;
			t2Mult = 4.0;
		} else if (score >= 20) {
			t1Mult = 1.8;
			t2Mult = 3.5;
		} else {
			t1Mult = 1.5;
			t2Mult = 3.0;
		}

		double target1 = entryPrice + risk * t1Mult;
		double target2 = entryPrice + risk * t2Mult;

		trade.setEntryPrice(TradingUtil.roundToTwoDecimalPlaces(entryPrice));
		trade.setStopLoss(TradingUtil.roundToTwoDecimalPlaces(stopLoss));
		trade.setTarget1(TradingUtil.roundToTwoDecimalPlaces(target1));
		trade.setTarget2(TradingUtil.roundToTwoDecimalPlaces(target2));

		// 🔥 IMPORTANT: Placeholder – real sizing in option engine
		trade.setPositionSize(1);

		return trade;
	}

	public boolean shouldEnterOption(TradeSetupForFNO setup, FNO5MinCandle candle, // current 5-min candle (just closed)
			double dailyEma9, double dailyAtr, int daysSinceSetup) {

		// 1️⃣ Setup validity window (do not chase)
		if (daysSinceSetup > 3) {
			return false;
		}

		double ltp = candle.getClose();
		double low = candle.getLow();
		double open = candle.getOpen();
		double close = candle.getClose();

		// 2️⃣ Price must come near DAILY EMA9 (zone, not exact)
		double buffer = 0.3 * dailyAtr;
		boolean nearDailyEma9 = Math.abs(low - dailyEma9) <= buffer || Math.abs(ltp - dailyEma9) <= buffer;

		if (!nearDailyEma9) {
			return false;
		}

		// 3️⃣ Bounce confirmation on 5-min candle
		boolean strongBounce = close > open;

		if (!strongBounce) {
			return false;
		}

		// 4️⃣ Optional safety: do not buy breakdown
		if (close < dailyEma9) {
			return false;
		}

		return true; // ✅ BUY OPTION
	}

	public static int calculateFnoSetupRank(List<StockDailyPrice> history, StockDailyPrice current) {

		int score = 0;

		StockTechnicalIndicator ti = current.getTechnicalIndicator();

		double atr = ti.getAtr();
		double atrAvg = history.stream().limit(history.size() - 1).skip(Math.max(0, history.size() - 21))
				.mapToDouble(d -> d.getTechnicalIndicator().getAtr()).average().orElse(atr);

		double range = current.getHighPrice() - current.getLowPrice();
		double ema9 = ti.getEma9();
		double ema21 = ti.getEma21();
		double ema50 = ti.getEma50();
		double rsi = ti.getRsi();

		// ------------------------------------------------
		// 1️⃣ VOLATILITY EXPANSION (MOST IMPORTANT)
		// ------------------------------------------------
		if (atr > atrAvg * 1.6)
			score += 30;
		else if (atr > atrAvg * 1.3)
			score += 20;
		else if (atr > atrAvg * 1.1)
			score += 10;

		// ------------------------------------------------
		// 2️⃣ STRONG DAILY RANGE
		// ------------------------------------------------
		if (range > atr * 1.2)
			score += 20;
		else if (range > atr)
			score += 10;

		// ------------------------------------------------
		// 3️⃣ TREND STRUCTURE (EMA STACK)
		// ------------------------------------------------
		if (ema9 > ema21 && ema21 > ema50)
			score += 20;
		else if (ema9 > ema21)
			score += 10;

		// ------------------------------------------------
		// 4️⃣ MOMENTUM ZONE (RSI)
		// ------------------------------------------------
		if (rsi >= 55 && rsi <= 70)
			score += 15;
		else if (rsi > 50)
			score += 5;

		// ------------------------------------------------
		// 5️⃣ VOLUME CONFIRMATION (LOW WEIGHT)
		// ------------------------------------------------
		double avgVol = history.stream().skip(Math.max(0, history.size() - 21)).mapToDouble(StockDailyPrice::getVolume)
				.average().orElse(0);

		if (avgVol > 0) {
			double volRatio = current.getVolume() / avgVol;
			if (volRatio > 2.5)
				score += 15;
			else if (volRatio > 1.8)
				score += 10;
			else if (volRatio > 1.3)
				score += 5;
		}

		return Math.min(score, 100); // cap at 100
	}

}
