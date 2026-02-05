
package com.fyers.fyerstrading.strategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.config.Stock5MinMapper;
import com.fyers.fyerstrading.entity.FNO5MinCandle;
import com.fyers.fyerstrading.model.MasterCandle;
import com.fyers.fyerstrading.repo.FNO5MinCandleRepository;
import com.fyers.fyerstrading.service.common.CandleFetcher;
import com.fyers.fyerstrading.service.common.IndicatorCalculator;

@Service
public class RSIReversalStrategy {

	@Autowired
	private FNO5MinCandleRepository candleRepo;

	@Autowired
	CandleFetcher candleFetcher;
	
	@Autowired
	IndicatorCalculator indicatorCalculator;
	
	@Autowired
	Stock5MinMapper mapper;

	private static final double FIXED_RISK = 2000.0;
	private static final double ATR_SL_MULTIPLIER = 1.0;
	private static final double ATR_TARGET_MULTIPLIER = 1.5;
	private static final double TRAIL_STEP = 0.5;

	public void backtestRSIReversal(List<String> symbols, LocalDate date) {
		double totalPnL = 0.0;
		System.out.println("========== RSI Reversal Backtest for " + date + " ==========");

		for (String symbol : symbols) {
			try {
				double pnl = analyzeSymbol(symbol, date);
				totalPnL += pnl;
			} catch (Exception e) {
				System.err.println("Error in RSI Reversal for " + symbol + ": " + e.getMessage());
			}
		}

		System.out.printf("\nTOTAL PnL on %s: ₹%.2f\n", date, totalPnL);
	}

	private double analyzeSymbol(String symbol, LocalDate date) {
		// ✅ Get last 3 TRADING DAYS before target date
		List<java.sql.Date> sqlDates = candleRepo.findDistinctDatesBySymbolBefore(symbol, date.atStartOfDay());

		List<LocalDate> availableDates = sqlDates.stream().map(java.sql.Date::toLocalDate).distinct().sorted()
				.collect(Collectors.toList());

		// Add the current date at the end (latest)
		availableDates.add(date);
		if (availableDates.size() < 3) {
			System.err.println("Not enough past data for " + symbol + " before " + date);
			return 0.0;
		}

		// Pick the last 3 trading days
		List<LocalDate> lastDays = availableDates.subList(Math.max(0, availableDates.size() - 3),
				availableDates.size());
		LocalDate fromDate = lastDays.get(0);

		List<FNO5MinCandle> candles5Min = candleRepo.findAllBWDate(symbol,
				LocalDateTime.of(fromDate, LocalTime.of(9, 15)), LocalDateTime.of(date, LocalTime.of(15, 30)));

		

		// 🔹 Convert to 1-hour candles
		List<FNO5MinCandle> candles1H = convertToHourly(symbol, candles5Min);
		if (candles1H.size() < 20)
			return 0.0;

		double totalPnL = 0.0;

		// 🔹 Identify hourly candles belonging to current test date
		List<FNO5MinCandle> currentDayCandles = candles1H.stream()
				.filter(c -> c.getTimestamp().toLocalDate().equals(date)).collect(Collectors.toList());

		// 🔹 Scan each hourly candle for BUY setup only
		for (FNO5MinCandle curr : currentDayCandles) {
			int index = candles1H.indexOf(curr);
			if (index < 14)
				continue; // skip until enough history

			List<FNO5MinCandle> subList = candles1H.subList(0, index + 1);
			
			List<MasterCandle> sub = subList.stream().map(mapper::toMasterCandle).toList();
			
			double rsi = indicatorCalculator.rsi(sub, 14);
			double atr = indicatorCalculator.atr(sub, 14);

			// ✅ BUY setup only: RSI < 32 and bullish candle
			if (rsi < 32 && curr.getClose() > curr.getOpen()) {
				totalPnL += simulateTrade(symbol, date, curr, atr, rsi, true, candles1H, index);
			}
		}

		return totalPnL;
	}

	private double simulateTrade(String symbol, LocalDate date, FNO5MinCandle entryCandle, double atr, double rsi,
			boolean isBuy, List<FNO5MinCandle> allCandles, int entryIndex) {

		double entry = entryCandle.getClose();
		double stop = entry - (ATR_SL_MULTIPLIER * atr);
		double target = entry + (ATR_TARGET_MULTIPLIER * atr);
		double trailTrigger = entry;
		double quantity = Math.floor(FIXED_RISK / Math.abs(entry - stop));

		LocalDateTime entryTime = entryCandle.getTimestamp();
		LocalDateTime exitTime = null;
		double exitPrice = entry;
		String exitReason = "EOD";
		double pnl = 0.0;

		for (int j = entryIndex + 1; j < allCandles.size(); j++) {
			FNO5MinCandle c = allCandles.get(j);
			double high = c.getHigh();
			double low = c.getLow();

			// ✅ Long trade logic
			if (low <= stop) {
				exitPrice = stop;
				exitReason = "SL Hit";
				exitTime = c.getTimestamp();
				break;
			}
			if (high >= target) {
				exitPrice = target;
				exitReason = "Target Hit";
				exitTime = c.getTimestamp();
				break;
			}

			// 🔹 Trailing stop
			if (high > trailTrigger + (TRAIL_STEP * atr)) {
				stop += (TRAIL_STEP * atr);
				trailTrigger = high;
			}
		}

		if (exitTime == null) {
			FNO5MinCandle last = allCandles.get(allCandles.size() - 1);
			exitTime = last.getTimestamp();
			exitPrice = last.getClose();
		}

		pnl = (exitPrice - entry) * quantity;

		System.out.printf(
				"%-10s | %s | BUY | RSI=%.2f | Entry=%.2f | Exit=%.2f | SL=%.2f | Target=%.2f | Qty=%.0f | PnL=%.2f | %s | %s%n",
				symbol, date, rsi, entry, exitPrice, stop, target, quantity, pnl, exitReason, entryTime.toLocalTime());

		return pnl;
	}

	
	public List<FNO5MinCandle> convertToHourly(String symbol, List<FNO5MinCandle> candles5Min) {
	    List<FNO5MinCandle> hourlyCandles = new ArrayList<>();
	    if (candles5Min == null || candles5Min.isEmpty()) return hourlyCandles;

	    // Sort by timestamp
	    candles5Min.sort(Comparator.comparing(FNO5MinCandle::getTimestamp));

	    // Group candles by trading day
	    Map<LocalDate, List<FNO5MinCandle>> candlesByDate = candles5Min.stream()
	            .collect(Collectors.groupingBy(c -> c.getTimestamp().toLocalDate(),
	                    TreeMap::new, Collectors.toList()));

	    // NSE time segments (hourly candles end on :10 of the next hour)
	    LocalTime[][] segments = {
	            {LocalTime.of(9, 15), LocalTime.of(10, 10)},
	            {LocalTime.of(10, 15), LocalTime.of(11, 10)},
	            {LocalTime.of(11, 15), LocalTime.of(12, 10)},
	            {LocalTime.of(12, 15), LocalTime.of(13, 10)},
	            {LocalTime.of(13, 15), LocalTime.of(14, 10)},
	            {LocalTime.of(14, 15), LocalTime.of(15, 10)},
	            {LocalTime.of(15, 15), LocalTime.of(15, 30)} // partial
	    };

	    // Iterate each trading day
	    for (Map.Entry<LocalDate, List<FNO5MinCandle>> entry : candlesByDate.entrySet()) {
	        LocalDate date = entry.getKey();
	        List<FNO5MinCandle> dayCandles = entry.getValue();

	        for (LocalTime[] seg : segments) {
	            LocalDateTime segStart = LocalDateTime.of(date, seg[0]);
	            LocalDateTime segEnd = LocalDateTime.of(date, seg[1]);

	            // Include candles >= start and <= end
	            List<FNO5MinCandle> segmentCandles = dayCandles.stream()
	                    .filter(c -> !c.getTimestamp().isBefore(segStart) && !c.getTimestamp().isAfter(segEnd))
	                    .collect(Collectors.toList());

	            if (!segmentCandles.isEmpty()) {
	                hourlyCandles.add(mergeCandles(symbol, segmentCandles, segStart));
	            }
	        }
	    }

	    return hourlyCandles;
	}


	
	
	public FNO5MinCandle mergeCandles(String symbol, List<FNO5MinCandle> candles, LocalDateTime segmentStart) {
	    if (candles == null || candles.isEmpty()) return null;

	    candles.sort(Comparator.comparing(FNO5MinCandle::getTimestamp));

	    FNO5MinCandle first = candles.get(0);
	    FNO5MinCandle last = candles.get(candles.size() - 1);

	    double open = first.getOpen();
	    double close = last.getClose();
	    double high = candles.stream().mapToDouble(FNO5MinCandle::getHigh).max().orElse(open);
	    double low = candles.stream().mapToDouble(FNO5MinCandle::getLow).min().orElse(open);
	    double volume = candles.stream().mapToDouble(FNO5MinCandle::getVolume).sum();

	    FNO5MinCandle merged = new FNO5MinCandle();
	    merged.setSymbol(symbol);
	    merged.setTimestamp(segmentStart);
	    merged.setOpen(open);
	    merged.setHigh(high);
	    merged.setLow(low);
	    merged.setClose(close);
	    merged.setVolume(volume);

	    return merged;
	}


	

}
