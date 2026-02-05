package com.fyers.fyerstrading.service.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.config.Nifty5MinMapper;
import com.fyers.fyerstrading.config.NiftyDailyMapper;
import com.fyers.fyerstrading.config.Stock5MinMapper;
import com.fyers.fyerstrading.config.StockDailyMapper;
import com.fyers.fyerstrading.entity.FNO5MinCandle;
import com.fyers.fyerstrading.entity.Nifty5MinCandle;
import com.fyers.fyerstrading.entity.NiftyDailyCandle;
import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.model.MasterCandle;
import com.fyers.fyerstrading.model.StockQuote;
import com.fyers.fyerstrading.repo.FNO5MinCandleRepository;
import com.fyers.fyerstrading.repo.NiftyDailyCandleRepo;
import com.fyers.fyerstrading.repo.NiftyFiveMinCandleRepository;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.service.CandleUtil;
import com.fyers.fyerstrading.service.FyersApiService;

@Service
public class CandleFetcher {

	@Autowired
	NiftyFiveMinCandleRepository nifty5mRepo;
	@Autowired
	NiftyDailyCandleRepo niftyDailyRepo;
	@Autowired
	private StockDailyPriceRepository stockDailyRepo;
	@Autowired
	private FNO5MinCandleRepository stock5mRepo;

	@Autowired
	Stock5MinMapper stock5mMapper;
	@Autowired
	StockDailyMapper stockDailyMapper;
	@Autowired
	Nifty5MinMapper nifty5mMapper;
	@Autowired
	NiftyDailyMapper niftyDailyMapper;
	@Autowired
	private FyersApiService fyersApiService;

	public double getLTP(String symbol) {
		try {
			StockQuote quote = fyersApiService.getStockQuotes(symbol);
			return quote.getLp();
		} catch (Exception e) {
			System.err.println("Error fetching underlying LTP: " + e.getMessage());
		}
		return 0.0;
	}

	public List<MasterCandle> getLastNCandles(String symbol, String tf, int limit) {

		if (symbol.equals("NIFTY") || symbol.equals("BANKNIFTY")) {
			switch (tf) {
			case "5m":
				return nifty5mRepo.findTopNByTimestamp(limit).stream().map(nifty5mMapper::toMasterCandle).toList();

			case "1d":
				return niftyDailyRepo.findTopNByTradeDate(limit).stream().map(niftyDailyMapper::toMasterCandle)
						.toList();
			}
		}

		switch (tf) {
		case "5m":
			return stock5mRepo.findTopNByTimestamp(symbol, limit).stream().map(stock5mMapper::toMasterCandle).toList();

		case "1d":
			return stockDailyRepo.findTopNByTradeDate(symbol, limit).stream().map(stockDailyMapper::toMasterCandle)
					.toList();
		}

		throw new RuntimeException("Invalid timeframe");
	}

	public List<MasterCandle> getRecordsBeforeDate(String symbol, String tf, LocalDateTime dateTime, int limit) {

		if (symbol.equals("NIFTY") || symbol.equals("BANKNIFTY")) {
			switch (tf) {
			case "5m":
				nifty5mRepo.findTopNByTimestamp(limit).stream().map(nifty5mMapper::toMasterCandle).toList();

			case "1d":
				return niftyDailyRepo.findRecordsBeforeDate(dateTime.toLocalDate(), limit).stream()
						.map(niftyDailyMapper::toMasterCandle).toList();

			}
		}

		switch (tf) {
		case "5m":
			return stock5mRepo.findTopNByTimestamp(symbol, limit).stream().map(stock5mMapper::toMasterCandle).toList();

		case "1d":
			return stockDailyRepo.findRecordsBeforeDate(symbol, dateTime.toLocalDate(), limit).stream()
					.map(stockDailyMapper::toMasterCandle).toList();
		}

		throw new RuntimeException("Invalid timeframe");
	}

	public List<MasterCandle> getCandlesBWDates(String symbol, String tf, LocalDateTime startDate,
			LocalDateTime endDate) {

		if (symbol.equals("NIFTY") || symbol.equals("BANKNIFTY")) {
			switch (tf) {
			case "5m":
				nifty5mRepo.findAllBWDate(startDate, endDate).stream().map(nifty5mMapper::toMasterCandle).toList();

			case "1d":
				return niftyDailyRepo.findAllBWDate(startDate.toLocalDate(), endDate.toLocalDate()).stream()
						.map(niftyDailyMapper::toMasterCandle).toList();
			}
		}

		switch (tf) {
		case "5m":
			return stock5mRepo.findAllBWDate(symbol, startDate, endDate).stream().map(stock5mMapper::toMasterCandle)
					.toList();

		case "1d":
			return stockDailyRepo.findAllBWDate(symbol, startDate.toLocalDate(), endDate.toLocalDate()).stream()
					.map(stockDailyMapper::toMasterCandle).toList();
		}

		throw new RuntimeException("Invalid timeframe");
	}

	public MasterCandle getRecordOnDate(String symbol, String tf, LocalDateTime timestamp) {

		if (symbol.equals("NIFTY") || symbol.equals("BANKNIFTY")) {
			switch (tf) {
			case "5m":
				Nifty5MinCandle nifty5MinCandle = nifty5mRepo.findRecordOnTime(timestamp);
				return nifty5mMapper.toMasterCandle(nifty5MinCandle);

			case "1d":
				NiftyDailyCandle niftyDailyCandle = niftyDailyRepo.findRecordOnDate(timestamp.toLocalDate());
				return niftyDailyMapper.toMasterCandle(niftyDailyCandle);
			}
		}

		switch (tf) {
		case "5m":
			FNO5MinCandle fno5MinCandle = stock5mRepo.findRecordOnTime(symbol, timestamp);
			return stock5mMapper.toMasterCandle(fno5MinCandle);

		case "1d":
			StockDailyPrice stockDailyPrice = stockDailyRepo.findRecordOnDate(symbol, timestamp.toLocalDate());
			return stockDailyMapper.toMasterCandle(stockDailyPrice);
		}

		throw new RuntimeException("Invalid timeframe");
	}

	public List<StockDailyPrice> getRecordsBeforDate(String symbol, LocalDate date, int limit) {

		return stockDailyRepo.findRecordsBeforeDate(symbol, date, limit);
	}

	public List<Nifty5MinCandle> getLastNCandlesNifty5Min(int n) {
		return nifty5mRepo.findTopNByTimestamp(n);

	}

	public List<NiftyDailyCandle> getLastNCandlesNiftyDaily(int n) {
		return niftyDailyRepo.findTopNByTradeDate(n);
	}

	public List<NiftyDailyCandle> findRecentBeforeDate(LocalDate date, int noOfRecords) {
		List<NiftyDailyCandle> candles = niftyDailyRepo.findRecentBeforeDate(date, PageRequest.of(0, noOfRecords));

		// return sorted oldest -> newest
		return candles.stream().sorted(Comparator.comparing(NiftyDailyCandle::getTradeDate))
				.collect(Collectors.toList());
	}

	public NiftyDailyCandle getNiftyDailyCandel(LocalDate date) {
		NiftyDailyCandle candle = niftyDailyRepo.findRecordOnDate(date);
		return candle;
	}

	public List<NiftyDailyCandle> getNiftyDailyCandelBWDate(LocalDate startDate, LocalDate endDate) {
		List<NiftyDailyCandle> candles = niftyDailyRepo.findAllBWDate(startDate, endDate);

		return candles;
	}

	public List<Nifty5MinCandle> getNifty5MinCandelBWDate(LocalDateTime startTime, LocalDateTime endTime) {
		List<Nifty5MinCandle> candles = nifty5mRepo.findAllBetweenTimestamps(startTime, endTime);
		return candles;
	}

	public StockDailyPrice getStockPriceByTradeDate(String symbol, LocalDate date) {
		List<StockDailyPrice> candles = stockDailyRepo.findAllBWDate(symbol, date.minusDays(5), date.plusDays(1));
		StockDailyPrice dailyPrice = candles.get(candles.size() - 1);
		return dailyPrice;
	}

	public List<FNO5MinCandle> getLastNCandlesStocks5Min(String symbol, int n) {
		return stock5mRepo.findTopNByTimestamp(symbol, n);

	}

	public StockDailyPrice getStockByTradeDate(String symbol, LocalDate date) {
		List<StockDailyPrice> candles = stockDailyRepo.findAllBWDate(symbol, date.minusDays(5), date.plusDays(1));
		StockDailyPrice dailyPrice = candles.get(candles.size() - 1);
		return dailyPrice;
	}

	public List<StockDailyPrice> geStocktDailyCandles(String symbol, LocalDate startDate, LocalDate endDate) {
		List<StockDailyPrice> candles = stockDailyRepo.findAllBWDate(symbol, startDate, endDate);
		return candles;
	}

	public List<FNO5MinCandle> get5MinStockCandlesBetween(String symbol, LocalDate date, LocalTime startTime,
			LocalTime endTime) {
		LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
		LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

		return stock5mRepo.findAllBWDate(symbol, startDateTime, endDateTime);
	}

	public double calculateEMAFor5MinNifty(String symbol, int period, LocalDateTime uptoTime) {
		// Fetch last (period + 5) candles before given time
		List<Nifty5MinCandle> candles = nifty5mRepo.findLastNCandlesBefore(uptoTime, period + 5);
		if (candles == null || candles.size() < period) {
			return Double.NaN;
		}

		// Sort ascending by time
		candles.sort((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));

		double multiplier = 2.0 / (period + 1);
		double ema = 0.0;

		// Start with SMA as seed
		double sma = candles.stream().limit(period).mapToDouble(Nifty5MinCandle::getClose).average().orElse(Double.NaN);

		ema = sma;
		for (int i = period; i < candles.size(); i++) {
			double close = candles.get(i).getClose();
			ema = (close - ema) * multiplier + ema;
		}
		return ema;
	}

	public double calculateEMAFor5MinStocks(String symbol, int period, LocalDateTime uptoTime) {
		// Fetch last (period + 5) candles before given time
		List<FNO5MinCandle> candles = stock5mRepo.findLastNCandlesBefore(symbol, uptoTime, period + 5);
		if (candles == null || candles.size() < period) {
			return Double.NaN;
		}

		// Sort ascending by time
		candles.sort((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));

		double multiplier = 2.0 / (period + 1);
		double ema = 0.0;

		// Start with SMA as seed
		double sma = candles.stream().limit(period).mapToDouble(FNO5MinCandle::getClose).average().orElse(Double.NaN);

		ema = sma;
		for (int i = period; i < candles.size(); i++) {
			double close = candles.get(i).getClose();
			ema = (close - ema) * multiplier + ema;
		}
		return ema;
	}

	public double calculateATRForSymbol(String symbol, int period, LocalDateTime uptoTime) {
		// Fetch last (period + 1) candles
		List<FNO5MinCandle> candles = stock5mRepo.findLastNCandlesBefore(symbol, uptoTime, period + 1);
		if (candles == null || candles.size() <= 1)
			return Double.NaN;

		candles.sort((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));

		double atrSum = 0.0;
		for (int i = 1; i < candles.size(); i++) {
			FNO5MinCandle curr = candles.get(i);
			FNO5MinCandle prev = candles.get(i - 1);

			double tr = Math.max(curr.getHigh() - curr.getLow(),
					Math.max(Math.abs(curr.getHigh() - prev.getClose()), Math.abs(curr.getLow() - prev.getClose())));
			atrSum += tr;
		}

		return atrSum / period;
	}

	public double calculateRSIFor5Min(String symbol, int period, LocalDateTime timestamp) {
		// Fetch last (period + 1) candles before given timestamp
		List<FNO5MinCandle> candles = stock5mRepo.findLastNCandlesBefore(symbol, timestamp, period + 1);

		if (candles == null || candles.size() < period + 1) {
			return Double.NaN;
		}

		// Ensure sorted ascending
		candles.sort(Comparator.comparing(FNO5MinCandle::getTimestamp));

		double gainSum = 0.0;
		double lossSum = 0.0;

		for (int i = 1; i < candles.size(); i++) {
			double change = candles.get(i).getClose() - candles.get(i - 1).getClose();
			if (change > 0)
				gainSum += change;
			else
				lossSum += Math.abs(change);
		}

		double avgGain = gainSum / period;
		double avgLoss = lossSum / period;

		if (avgLoss == 0)
			return 100.0; // full strength
		double rs = avgGain / avgLoss;

		return 100.0 - (100.0 / (1.0 + rs));
	}

	public double calculateVWAP(String symbol, LocalDate date, LocalDateTime timestamp) {
		LocalDateTime start = LocalDateTime.of(date, LocalTime.of(9, 15));

		List<FNO5MinCandle> candles = stock5mRepo.findAllBWDate(symbol, start, timestamp);

		if (candles == null || candles.isEmpty()) {
			return Double.NaN;
		}

		double pvSum = 0.0; // price * volume sum
		double volSum = 0.0;

		for (FNO5MinCandle c : candles) {
			double typicalPrice = (c.getHigh() + c.getLow() + c.getClose()) / 3.0;
			pvSum += typicalPrice * c.getVolume();
			volSum += c.getVolume();
		}

		return volSum == 0 ? Double.NaN : pvSum / volSum;
	}

	public double calculateATRFromCandles(List<FNO5MinCandle> candles, int period, int upToIndex) {
		if (candles == null || candles.size() < period + 1 || upToIndex < period)
			return Double.NaN;

		// Take the subset of candles ending at 'upToIndex'
		List<FNO5MinCandle> subset = candles.subList(upToIndex - period, upToIndex + 1);

		double sumTR = 0.0;
		for (int i = 1; i < subset.size(); i++) {
			FNO5MinCandle curr = subset.get(i);
			FNO5MinCandle prev = subset.get(i - 1);

			double tr1 = curr.getHigh() - curr.getLow();
			double tr2 = Math.abs(curr.getHigh() - prev.getClose());
			double tr3 = Math.abs(curr.getLow() - prev.getClose());

			sumTR += Math.max(tr1, Math.max(tr2, tr3));
		}

		return sumTR / period;
	}

	public int getNiftyDirection(LocalDate date) {
		List<NiftyDailyCandle> candles = niftyDailyRepo.findTopNByOrderByTradeDateDesc(date, PageRequest.of(0, 20));

		if (candles.size() < 20)
			return 0;

		double lastClose = candles.get(0).getClose();
		Collections.reverse(candles);
		double ema20 = calculateEMA(candles, 20);

		if (lastClose > ema20)
			return 1; // Bullish bias
		else if (lastClose < ema20)
			return -1; // Bearish bias
		else
			return 0; // Neutral
	}

	public double calculateEMA(List<NiftyDailyCandle> candles, int period) {
		double multiplier = 2.0 / (period + 1);
		double ema = candles.get(0).getClose();
		for (int i = 1; i < candles.size(); i++) {
			ema = (candles.get(i).getClose() - ema) * multiplier + ema;
		}
		return ema;
	}

	public int getDailyTrend(String symbol, LocalDate date) {
		List<StockDailyPrice> candles = stockDailyRepo.findTopNByOrderByTradeDateDesc(symbol, date,
				PageRequest.of(0, 5));

		StockDailyPrice lastCandel = candles.get(0);
		double ema20 = lastCandel.getTechnicalIndicator().getEma20();
		double ema50 = lastCandel.getTechnicalIndicator().getEma50();
		double lastClose = lastCandel.getClosePrice();

		// Primary trend
		int trend = 0;
		if (lastClose > ema20)
			trend = 1;
		else if (lastClose < ema20)
			trend = -1;

		// Strength confirmation
		if (trend == 1 && ema20 > ema50)
			return 1; // Strong bullish
		if (trend == -1 && ema20 < ema50)
			return -1; // Strong bearish
		return 0; // Sideways / neutral
	}

	public List<StockDailyPrice> getNoOfRecordsForStockDailyPriceList(String symbol, LocalDate date, int noOfRecords) {

		List<StockDailyPrice> candles = stockDailyRepo.findTopNByOrderByTradeDateDesc(symbol, date,
				PageRequest.of(0, noOfRecords));
		Collections.reverse(candles);
		return candles;

	}

	public double calculateRSI(List<Nifty5MinCandle> candles, int period) {
		if (candles.size() <= period)
			return 50.0;
		double gain = 0, loss = 0;
		for (int i = candles.size() - period; i < candles.size(); i++) {
			double change = candles.get(i).getClose() - candles.get(i - 1).getClose();
			if (change > 0)
				gain += change;
			else
				loss -= change;
		}
		if (loss == 0)
			return 100;
		double rs = (gain / period) / (loss / period);
		return 100 - (100 / (1 + rs));
	}

	public double calculateATR(List<Nifty5MinCandle> candles, int period) {
		if (candles.size() < period)
			return 0;
		List<Double> trs = new ArrayList<>();
		for (int i = 1; i < candles.size(); i++) {
			Nifty5MinCandle prev = candles.get(i - 1);
			Nifty5MinCandle curr = candles.get(i);
			double highLow = curr.getHigh() - curr.getLow();
			double highClose = Math.abs(curr.getHigh() - prev.getClose());
			double lowClose = Math.abs(curr.getLow() - prev.getClose());
			trs.add(Math.max(highLow, Math.max(highClose, lowClose)));
		}
		return trs.stream().skip(trs.size() - period).mapToDouble(Double::doubleValue).average().orElse(0);
	}

	public double calculateNiftyDailyATR(List<NiftyDailyCandle> candles, int period) {
		if (candles.size() < period)
			return 0;
		List<Double> trs = new ArrayList<>();
		for (int i = 1; i < candles.size(); i++) {
			NiftyDailyCandle prev = candles.get(i - 1);
			NiftyDailyCandle curr = candles.get(i);
			double highLow = curr.getHigh() - curr.getLow();
			double highClose = Math.abs(curr.getHigh() - prev.getClose());
			double lowClose = Math.abs(curr.getLow() - prev.getClose());
			trs.add(Math.max(highLow, Math.max(highClose, lowClose)));
		}
		return trs.stream().skip(trs.size() - period).mapToDouble(Double::doubleValue).average().orElse(0);
	}

	public List<LocalDate> findLastNDatesBeforeForNifty5Min(LocalDate date, int n) {
		List<java.sql.Date> sqlDates = nifty5mRepo.findLastNDatesBefore(date, n);
		List<LocalDate> availableDates = sqlDates.stream().map(java.sql.Date::toLocalDate).distinct().sorted()
				.collect(Collectors.toList());
		return availableDates;

	}

	public double calculateEMAFor15MinStocks(String symbol, int period, LocalDateTime uptoTime) {
		// 1️⃣ Fetch last (period + 5) * 3 5-min candles before uptoTime
		// because 3 x 5-min = 1 x 15-min candle
		int required5MinCandles = (period + 5) * 3;
		List<FNO5MinCandle> candles5Min = stock5mRepo.findLastNCandlesBefore(symbol, uptoTime, required5MinCandles);

		if (candles5Min == null || candles5Min.size() < required5MinCandles) {
			return Double.NaN;
		}

		// 2️⃣ Convert 5-min candles to 15-min candles
		List<FNO5MinCandle> sorted5Min = candles5Min.stream().sorted(Comparator.comparing(FNO5MinCandle::getTimestamp))
				.collect(Collectors.toList());

		List<FNO5MinCandle> candles15Min = CandleUtil.toHigherTF(sorted5Min, 15);

		if (candles15Min.size() < period) {
			return Double.NaN;
		}

		// 3️⃣ Calculate EMA on 15-min candles
		double multiplier = 2.0 / (period + 1);

		// Start with SMA of first 'period' candles as seed
		double sma = candles15Min.stream().limit(period).mapToDouble(FNO5MinCandle::getClose).average()
				.orElse(Double.NaN);

		double ema = sma;

		// Apply EMA formula to remaining candles
		for (int i = period; i < candles15Min.size(); i++) {
			double close = candles15Min.get(i).getClose();
			ema = (close - ema) * multiplier + ema;
		}

		return ema;
	}

	public double calculateRSIFor15Min(String symbol, int period, LocalDateTime uptoTime) {
		// 1️⃣ Fetch enough 5-min candles to form (period + 1) 15-min candles
		int required5MinCandles = (period + 1) * 3; // 3 x 5-min = 1 x 15-min candle
		List<FNO5MinCandle> candles5Min = stock5mRepo.findLastNCandlesBefore(symbol, uptoTime, required5MinCandles);

		if (candles5Min == null || candles5Min.size() < required5MinCandles) {
			return Double.NaN;
		}

		// 2️⃣ Sort ascending
		candles5Min.sort(Comparator.comparing(FNO5MinCandle::getTimestamp));

		// 3️⃣ Aggregate 5-min to 15-min candles
		List<FNO5MinCandle> candles15Min = CandleUtil.toHigherTF(candles5Min, 15);

		if (candles15Min.size() < period + 1) {
			return Double.NaN;
		}

		// 4️⃣ Compute RSI on 15-min closes
		double gainSum = 0.0;
		double lossSum = 0.0;

		for (int i = candles15Min.size() - period - 1; i < candles15Min.size() - 1; i++) {
			double change = candles15Min.get(i + 1).getClose() - candles15Min.get(i).getClose();
			if (change > 0)
				gainSum += change;
			else
				lossSum += Math.abs(change);
		}

		double avgGain = gainSum / period;
		double avgLoss = lossSum / period;

		if (avgLoss == 0)
			return 100.0;
		double rs = avgGain / avgLoss;

		return 100.0 - (100.0 / (1.0 + rs));
	}

	public double calculateATR15Min(String symbol, int period, LocalDateTime uptoTime) {
		int required5MinCandles = (period + 1) * 3; // 3x because 15-min = 3 * 5-min
		List<FNO5MinCandle> candles5Min = new ArrayList<>();

		// Fetch candles from current day first
		LocalDateTime end = uptoTime;
		LocalDateTime start = LocalDateTime.of(uptoTime.toLocalDate(), LocalTime.of(9, 15));

		candles5Min.addAll(stock5mRepo.findAllBWDate(symbol, start, end));

		// If not enough, fetch previous days until we have enough
		int daysBack = 1;
		while (candles5Min.size() < required5MinCandles && daysBack <= 10) {
			LocalDate prevDate = uptoTime.toLocalDate().minusDays(daysBack);
			LocalDateTime prevStart = LocalDateTime.of(prevDate, LocalTime.of(9, 15));
			LocalDateTime prevEnd = LocalDateTime.of(prevDate, LocalTime.of(15, 30));

			List<FNO5MinCandle> prevDayCandles = stock5mRepo.findAllBWDate(symbol, prevStart, prevEnd);

			if (prevDayCandles != null && !prevDayCandles.isEmpty()) {
				// prepend older candles
				prevDayCandles.addAll(candles5Min);
				candles5Min = prevDayCandles;
			}

			daysBack++;
		}

		// Not enough data even after trying
		if (candles5Min.size() < required5MinCandles) {
			return Double.NaN;
		}

		// Convert to 15-min candles
		List<FNO5MinCandle> candles15Min = CandleUtil.toHigherTF(candles5Min, 15);
		if (candles15Min.size() <= period) {
			return Double.NaN;
		}

		// Sort & compute ATR
		candles15Min.sort(Comparator.comparing(FNO5MinCandle::getTimestamp));

		double atrSum = 0.0;
		for (int i = candles15Min.size() - period; i < candles15Min.size(); i++) {
			FNO5MinCandle curr = candles15Min.get(i);
			FNO5MinCandle prev = candles15Min.get(i - 1);

			double tr = Math.max(curr.getHigh() - curr.getLow(),
					Math.max(Math.abs(curr.getHigh() - prev.getClose()), Math.abs(curr.getLow() - prev.getClose())));
			atrSum += tr;
		}

		return atrSum / period;
	}

	public List<MasterCandle> getIntrdayCandles(String symbol, LocalDate date) {
		if (symbol.equals("NIFTY") || symbol.equals("BANKNIFTY")) {
			return nifty5mRepo.getCandlesByDate(date).stream().map(nifty5mMapper::toMasterCandle).toList();
		} else {
			return stock5mRepo.getCandlesByDate(symbol, date).stream().map(stock5mMapper::toMasterCandle).toList();

		}
	}
}
