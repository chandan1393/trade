package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.FNO5MinCandle;
import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.model.ORBResult;
import com.fyers.fyerstrading.model.TradeEntryForIntradayStocks;
import com.fyers.fyerstrading.model.TradeOutcome;
import com.fyers.fyerstrading.repo.FNO5MinCandleRepository;
import com.fyers.fyerstrading.service.common.CandleFetcher;

@Service
public class BackTestHourlyStartegy {

	private final CandleFetcher priceService;
	private final StockOIService oiDataService;
	private final FNO5MinCandleRepository fno5MinCandleRepository;

	public BackTestHourlyStartegy(CandleFetcher priceService, StockOIService oiDataService,
			FNO5MinCandleRepository fno5MinCandleRepository) {
		this.priceService = priceService;
		this.oiDataService = oiDataService;
		this.fno5MinCandleRepository = fno5MinCandleRepository;
	}

	public void backtestFirstHourBreakout(List<String> symbols, LocalDate date) {
		double totalPnL = 0.0;
		int totalTrades = 0, wins = 0, losses = 0, partials = 0;
		double riskPerTrade = 500.0;

		for (String symbol : symbols) {
			try {
				if (symbol.equals("NSE:UPL-EQ")) {

					System.out.println("NSE:UPL-EQ");
				}
				List<FNO5MinCandle> candles = fno5MinCandleRepository
						.findAllBWDate(symbol,
								LocalDateTime.of(date, LocalTime.of(9, 15)),
								LocalDateTime.of(date, LocalTime.of(15, 30)));

				if (candles == null || candles.isEmpty())
					continue;

				// 1️⃣ First-hour and post-range candles
				List<FNO5MinCandle> firstHour = candles.stream()
						.filter(c -> !c.getTimestamp().toLocalTime().isAfter(LocalTime.of(10, 14)))
						.collect(Collectors.toList());
				if (firstHour.size() < 12)
					continue;

				double firstHourHigh = firstHour.stream().mapToDouble(FNO5MinCandle::getHigh).max().orElse(0);
				double firstHourLow = firstHour.stream().mapToDouble(FNO5MinCandle::getLow).min().orElse(0);
				double avgVol = firstHour.stream().mapToDouble(FNO5MinCandle::getVolume).average().orElse(0);

				List<FNO5MinCandle> afterRange = candles.stream()
						.filter(c -> !c.getTimestamp().toLocalTime().isBefore(LocalTime.of(10, 15))
								&& c.getTimestamp().toLocalTime().isBefore(LocalTime.of(14, 0)))
						.collect(Collectors.toList());

				List<FNO5MinCandle> afterRange15Min = CandleUtil.toHigherTF(afterRange, 15);

				for (int i = 0; i < afterRange15Min.size(); i++) {
					FNO5MinCandle c = afterRange15Min.get(i);

					// ✅ Step 1: Setup validation
					if (!isValidSetup(symbol, c, firstHourHigh, firstHourLow, avgVol, date))
						continue;

					// ✅ Step 2: Determine breakout or breakdown
					BreakoutDirection dir = detectBreakoutDirection(c, firstHourHigh, firstHourLow);
					if (dir == null)
						continue;

					// ✅ Step 3: Check entry execution
					TradeEntryForIntradayStocks entry = getEntryExecution(symbol, dir, firstHourHigh, firstHourLow,
							afterRange, c, i);
					if (entry == null)
						continue;

					// ✅ Step 4: Evaluate outcome
					TradeOutcome outcome = evaluateOutcome(symbol, dir, entry, afterRange, riskPerTrade);

					totalTrades++;
					totalPnL += outcome.getPnl();
					if (outcome.getPnl() > 0)
						wins++;
					else
						losses++;
					if (outcome.isPartialBooked())
						partials++;

					System.out.printf(
							"%s | %s | Entry=%.2f | SL=%.2f | Trail=%.2f | Qty=%d | EntryTime=%s | Exit=%.2f | ExitTime=%s | PnL=%.2f | %s%s%n",
							symbol, dir, entry.getEntry(), entry.getSl(), outcome.getTrailDistance(), outcome.getQty(),
							entry.getEntryTime().toLocalTime(), outcome.getExitPrice(),
							outcome.getExitTime() != null ? outcome.getExitTime().toLocalTime() : "--",
							outcome.getPnl(), outcome.isPartialBooked() ? "🟡 Partial " : "",
							outcome.getPnl() > 0 ? "✅" : "❌");

					break;
				}

			} catch (Exception e) {
				System.err.println("Error in " + symbol + ": " + e.getMessage());
			}
		}

		System.out.printf("\n📊 TOTAL TRADES=%d | Wins=%d | Losses=%d | Partial=%d | NetPnL=%.2f%n", totalTrades, wins,
				losses, partials, totalPnL);
	}

	private boolean isValidSetup(String symbol, FNO5MinCandle c, double firstHourHigh, double firstHourLow,
			double avgVol, LocalDate date) {
		boolean breakoutUp = c.getClose() > firstHourHigh;
		boolean breakoutDown = c.getClose() < firstHourLow;
		if (!breakoutUp && !breakoutDown)
			return false;

// ✅ Ensure breakout candle actually intersects the first-hour range
		if (breakoutUp && c.getLow() > firstHourHigh)
			return false; // Entire candle is above first-hour high → not a true breakout
		if (breakoutDown && c.getHigh() < firstHourLow)
			return false; // Entire candle is below first-hour low → not a true breakdown

// ✅ Candle structure and strength
		double body = Math.abs(c.getClose() - c.getOpen());
		double range = c.getHigh() - c.getLow();
		if (range == 0 || body / range < 0.5)
			return false;

		double closePos = (c.getClose() - c.getLow()) / range;
		if (breakoutUp && closePos < 0.6)
			return false;
		if (breakoutDown && (1 - closePos) < 0.6)
			return false;

// ✅ Ensure significant portion of body is beyond breakout level
		if (breakoutUp) {
			double bodyTop = Math.max(c.getOpen(), c.getClose());
			double bodyBottom = Math.min(c.getOpen(), c.getClose());
			double bodyAboveHigh = bodyTop - Math.max(firstHourHigh, bodyBottom);
			if (bodyAboveHigh < 0.4 * body)
				return false; // less than 40% of body above breakout zone
		} else if (breakoutDown) {
			double bodyTop = Math.max(c.getOpen(), c.getClose());
			double bodyBottom = Math.min(c.getOpen(), c.getClose());
			double bodyBelowLow = Math.min(firstHourLow, bodyTop) - bodyBottom;
			if (bodyBelowLow < 0.4 * body)
				return false; // less than 40% of body below breakdown zone
		}

// ✅ Volume confirmation
		if (c.getVolume() < avgVol * 1.3)
			return false;

// ✅ Indicator checks
		double ema = priceService.calculateEMAFor15MinStocks(symbol, 9, c.getTimestamp());
		double rsi = priceService.calculateRSIFor15Min(symbol, 14, c.getTimestamp());
		double vwap = priceService.calculateVWAP(symbol, date, c.getTimestamp());
		double atr = priceService.calculateATR15Min(symbol, 14, c.getTimestamp());

		if (Double.isNaN(ema) || Double.isNaN(rsi) || Double.isNaN(vwap) || Double.isNaN(atr))
			return false;

// ✅ Trend alignment
		boolean validTrend = (breakoutUp && c.getClose() > ema && c.getClose() > vwap && rsi >= 55 && rsi <= 80)
				|| (breakoutDown && c.getClose() < ema && c.getClose() < vwap && rsi <= 45 && rsi >= 20);

		return validTrend;
	}

	private BreakoutDirection detectBreakoutDirection(FNO5MinCandle c, double firstHourHigh, double firstHourLow) {
		if (c.getClose() > firstHourHigh)
			return BreakoutDirection.UP;
		if (c.getClose() < firstHourLow)
			return BreakoutDirection.DOWN;
		return null;
	}

	// --- improved entry execution ---
	private TradeEntryForIntradayStocks getEntryExecution(String symbol, BreakoutDirection dir, double firstHourHigh,
			double firstHourLow, List<FNO5MinCandle> afterRange5, FNO5MinCandle breakoutCandle, int index) {
		boolean verbose=true;
		double bufferPct = 0.0015;
		double entry = dir == BreakoutDirection.UP ? firstHourHigh * (1 + bufferPct) : firstHourLow * (1 - bufferPct);

		int start = Math.max(0, index - 3);
		// NOTE: afterRange5 here is 5-min candles (as you pass it in)
		List<FNO5MinCandle> prev = afterRange5.subList(start, Math.min(afterRange5.size(), index + 1));
		double sl = dir == BreakoutDirection.UP
				? prev.stream().mapToDouble(FNO5MinCandle::getLow).min().orElse(breakoutCandle.getLow())
				: prev.stream().mapToDouble(FNO5MinCandle::getHigh).max().orElse(breakoutCandle.getHigh());

		if ((dir == BreakoutDirection.UP && entry <= sl) || (dir == BreakoutDirection.DOWN && entry >= sl)) {
			if (verbose)
				System.out.printf("Invalid setup - entry vs SL mismatch: entry=%.2f sl=%.2f%n", entry, sl);
			return null;
		}

		// collect next up to 2 x 5-min candles after the breakout candle timestamp
		LocalDateTime breakoutTime = breakoutCandle.getTimestamp();
		List<FNO5MinCandle> post = afterRange5.stream()
		        .filter(x -> x.getTimestamp().isAfter(breakoutTime.plusMinutes(14))) // candle’s next interval starts after closure
		        .limit(2)
		        .collect(Collectors.toList());

		if (post.isEmpty()) {
			if (verbose)
				System.out.printf("No post-breakout 5-min candles for entry check%n");
			return null;
		}

		// Apply your rule:
		// - For breakout (long): next candle low <= entry AND candle closes bullish
		// (close >= open) OR close >= entry
		// - For breakdown (short): next candle high >= entry AND candle closes bearish
		// (close <= open) OR close <= entry
		for (FNO5MinCandle cand : post) {
			if (dir == BreakoutDirection.UP) {
				boolean contains = cand.getLow() <= entry && entry <= cand.getHigh();
				boolean lowTouched = cand.getLow() <= entry;
				boolean closesBullish = cand.getClose() >= cand.getOpen();
				boolean closesAtOrAboveEntry = cand.getClose() >= entry;
				if ((lowTouched && (closesBullish || closesAtOrAboveEntry)) || (contains && closesBullish)) {
					// found executed entry
					// enforce entry cutoff: must be before 12:00
					if (!cand.getTimestamp().toLocalTime().isBefore(LocalTime.of(12, 0))) {
						if (verbose)
							System.out.printf("Skipped - entry at %s after cutoff%n",
									cand.getTimestamp().toLocalTime());
						return null;
					}
					return new TradeEntryForIntradayStocks(entry, sl, cand.getTimestamp());
				}
			} else { // DOWN
				boolean contains = cand.getLow() <= entry && entry <= cand.getHigh();
				boolean highTouched = cand.getHigh() >= entry;
				boolean closesBearish = cand.getClose() <= cand.getOpen();
				boolean closesAtOrBelowEntry = cand.getClose() <= entry;
				if ((highTouched && (closesBearish || closesAtOrBelowEntry)) || (contains && closesBearish)) {
					if (!cand.getTimestamp().toLocalTime().isBefore(LocalTime.of(12, 0))) {
						if (verbose)
							System.out.printf("Skipped - entry at %s after cutoff%n",
									cand.getTimestamp().toLocalTime());
						return null;
					}
					return new TradeEntryForIntradayStocks(entry, sl, cand.getTimestamp());
				}
			}
		}

		// If not triggered in next 1-2 candles, consider missed
		if (verbose)
			System.out.printf("Entry not triggered in next 1-2 candles%n");
		return null;
	}

	// --- improved evaluateOutcome ---
	private TradeOutcome evaluateOutcome(String symbol, BreakoutDirection dir, TradeEntryForIntradayStocks entry,
			List<FNO5MinCandle> afterRange5, double riskPerTrade) {
boolean verbose=true;
		double riskPerShare = Math.abs(entry.getEntry() - entry.getSl());
		if (riskPerShare < 0.1)
			return new TradeOutcome(0, false, 0, entry.getEntry(), null, 0);

		int qty = (int) Math.max(1, Math.floor(riskPerTrade / riskPerShare));

		// ATR (may be NaN) — compute from priceService; fallback to range-based
		// distance
		double atr = priceService.calculateATR15Min(symbol, 14, entry.getEntryTime());
		double fallback = Math.abs(entry.getEntry() - entry.getSl());
		double trailDistance = 0.0;
		if (!Double.isNaN(atr) && atr > 0) {
			trailDistance = Math.max(atr * 1.5, fallback * 0.6);
		} else {
			// if ATR missing or tiny, use pivot-distance fallback
			trailDistance = Math.max(fallback * 0.6, Math.max(0.2, fallback)); // ensure at least a small trail
		}

		boolean partialBooked = false;
		double highest = entry.getEntry(), lowest = entry.getEntry(), pnl = 0.0;
		double exitPrice = entry.getEntry();
		LocalDateTime exitTime = null;

		// make execTime effectively final for filtering
		final LocalDateTime execTime = entry.getEntryTime();

		// runCandles: all 5-min candles from execTime onwards
		List<FNO5MinCandle> runCandles = afterRange5.stream().filter(x -> !x.getTimestamp().isBefore(execTime))
				.collect(Collectors.toList());

		if (runCandles.isEmpty()) {
			if (verbose)
				System.out.printf("No run candles after entry time; forcing last available exit%n");
			return buildForcedExitOutcome(symbol, dir, entry.getEntry(), qty, partialBooked, afterRange5,
					trailDistance);
		}

		for (FNO5MinCandle next : runCandles) {
			// 1) Stop-loss (check first)
			boolean slHit = dir == BreakoutDirection.UP ? next.getLow() <= entry.getSl()
					: next.getHigh() >= entry.getSl();
			if (slHit) {
				exitPrice = entry.getSl();
				exitTime = next.getTimestamp();
				pnl = (dir == BreakoutDirection.UP ? exitPrice - entry.getEntry() : entry.getEntry() - exitPrice) * qty;
				if (verbose)
					System.out.printf("%s SL HIT at %s price=%.2f pnl=%.2f%n", symbol, exitTime.toLocalTime(),
							exitPrice, pnl);
				return new TradeOutcome(pnl, partialBooked, qty, exitPrice, exitTime, trailDistance);
			}

			// 2) Update high/low
			if (dir == BreakoutDirection.UP)
				highest = Math.max(highest, next.getHigh());
			else
				lowest = Math.min(lowest, next.getLow());

			// 3) Partial at 1R (book exactly 50%)
			double oneRPrice = dir == BreakoutDirection.UP ? entry.getEntry() + riskPerShare
					: entry.getEntry() - riskPerShare;
			if (!partialBooked) {
				boolean hit1R = dir == BreakoutDirection.UP ? next.getHigh() >= oneRPrice : next.getLow() <= oneRPrice;
				if (hit1R) {
					// realized PnL for half position
					pnl += (dir == BreakoutDirection.UP ? (oneRPrice - entry.getEntry())
							: (entry.getEntry() - oneRPrice)) * (qty * 0.5);
					partialBooked = true;
					if (verbose)
						System.out.printf("%s Partial 1R booked at %s%n", symbol, next.getTimestamp().toLocalTime());
				}
			}

			// 4) Pivot-based trailing recalculated on aggregated 15-min blocks formed by
			// runSoFar
			// We'll aggregate runCandles up to this point to 15-min to compute pivot
			// trailing
			// (build a sublist of run candles up to current index)
			// For performance you can reuse aggregation, but here for clarity we recalc
			int uptoIndex = runCandles.indexOf(next) + 1;
			List<FNO5MinCandle> soFar = runCandles.subList(0, uptoIndex);
			List<FNO5MinCandle> agg15 = CandleUtil.toHigherTF(soFar, 15);

			if (agg15.size() >= 3) {
				int last = agg15.size() - 1;
				int from = Math.max(0, last - 2);
				List<FNO5MinCandle> last3 = agg15.subList(from, last + 1);
				double newPivot = dir == BreakoutDirection.UP
						? last3.stream().mapToDouble(FNO5MinCandle::getLow).min().orElse(entry.getSl())
						: last3.stream().mapToDouble(FNO5MinCandle::getHigh).max().orElse(entry.getSl());

				// move SL toward new pivot (but only in direction of protecting profits)
				if (dir == BreakoutDirection.UP && newPivot > entry.getSl()) {
					entry.setSl(newPivot); // move SL up
					if (verbose)
						System.out.printf("%s Trail SL moved up to %.2f at %s%n", symbol, newPivot,
								next.getTimestamp().toLocalTime());
				} else if (dir == BreakoutDirection.DOWN && newPivot < entry.getSl()) {
					entry.setSl(newPivot);
					if (verbose)
						System.out.printf("%s Trail SL moved down to %.2f at %s%n", symbol, newPivot,
								next.getTimestamp().toLocalTime());
				}
			}

			// 5) After trailing update, check trail hit on same bar
			boolean trailHit = dir == BreakoutDirection.UP ? next.getLow() <= entry.getSl()
					: next.getHigh() >= entry.getSl();
			if (trailHit) {
				exitPrice = entry.getSl();
				exitTime = next.getTimestamp();
				// remaining half exit PnL
				pnl += (dir == BreakoutDirection.UP ? exitPrice - entry.getEntry() : entry.getEntry() - exitPrice)
						* (qty * 0.5);
				if (verbose)
					System.out.printf("%s Trail HIT at %s price=%.2f pnl=%.2f%n", symbol, exitTime.toLocalTime(),
							exitPrice, pnl);
				return new TradeOutcome(pnl, partialBooked, qty, exitPrice, exitTime, trailDistance);
			}

			// 6) EOD forced exit
			if (next.getTimestamp().toLocalTime().isAfter(LocalTime.of(15, 15))) {
				exitPrice = next.getClose();
				exitTime = next.getTimestamp();
				pnl += (dir == BreakoutDirection.UP ? exitPrice - entry.getEntry() : entry.getEntry() - exitPrice) * qty
						* (partialBooked ? 0.5 : 1.0);
				if (verbose)
					System.out.printf("%s EOD forced exit at %s price=%.2f pnl=%.2f%n", symbol, exitTime.toLocalTime(),
							exitPrice, pnl);
				return new TradeOutcome(pnl, partialBooked, qty, exitPrice, exitTime, trailDistance);
			}
		}

		// If reached here, no exit hit — force close at last run candle
		return buildForcedExitOutcome(symbol, dir, entry.getEntry(), qty, partialBooked, runCandles, trailDistance);
	}

	private TradeOutcome buildForcedExitOutcome(String symbol, BreakoutDirection dir, double entryPrice, int qty,
			boolean partialBooked, List<FNO5MinCandle> runCandles, double trailDistance) {
		if (runCandles == null || runCandles.isEmpty()) {
// no data — return zero pnl
			return new TradeOutcome(0.0, partialBooked, qty, entryPrice, null, trailDistance);
		}
		FNO5MinCandle last = runCandles.get(runCandles.size() - 1);
		double exitPrice = last.getClose();
		LocalDateTime exitTime = last.getTimestamp();
		double remainderFactor = partialBooked ? 0.5 : 1.0;
		double pnl = (dir == BreakoutDirection.UP ? exitPrice - entryPrice : entryPrice - exitPrice) * qty
				* remainderFactor;
		return new TradeOutcome(pnl, partialBooked, qty, exitPrice, exitTime, trailDistance);
	}

	private enum BreakoutDirection {
		UP, DOWN, NONE
	}
}
