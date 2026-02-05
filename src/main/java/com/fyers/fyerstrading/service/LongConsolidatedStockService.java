package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fyers.fyerstrading.entity.ResistanceSetupEntity;
import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.model.BreakoutAnalysisResult;
import com.fyers.fyerstrading.model.ResistanceSetup;
import com.fyers.fyerstrading.repo.ResistanceSetupRepository;
import com.fyers.fyerstrading.service.common.CandleFetcher;

@Service
public class LongConsolidatedStockService {

	@Autowired
	private ResistanceSetupRepository repo;

	@Autowired
	private CandleFetcher priceService;

	// Configurable thresholds
	private static final int SWING_LOOKBACK = 180; // how many candles to scan for swing highs
	private static final int HYBRID_35_WINDOW = 35; // the 35-day highest-high confirmation
	private static final double NEAR_RES_PCT = 3.0; // proximity percent to flag near-resistance
	private static final double FALL_INVALIDATE_PCT = 10.0; // if price fell > this % from old high -> new structure

	@Transactional
	public void scanStock(String symbol, List<StockDailyPrice> candles, LocalDate setupFoundDate) {
		ResistanceSetup setup = findHybridResistance(candles, symbol);

		System.out.println(String.format("%s | RES: %.2f | CLOSE: %.2f | DATE: %s | RES_DATE: %s | FLAG: %s",
				setup.getSymbol(), setup.getResistance(), setup.getLastClose(), setup.getDate(),
				setup.getResistanceDate(), setup.isNearResistance()));

		// upsert logic (repo.findBySymbol returns null if not exist—adjust based on
		// your repo method)
		ResistanceSetupEntity entity = repo.findBySymbol(symbol);
		if (entity == null)
			entity = new ResistanceSetupEntity();

		entity.setSymbol(symbol);
		entity.setResistance(setup.getResistance());
		entity.setResistanceDate(setup.getResistanceDate());
		entity.setLastClose(setup.getLastClose());
		entity.setNearResistance(setup.isNearResistance());
		entity.setDiffPercent(setup.getDiffPercent());
		entity.setDetectedDate(setupFoundDate);
		entity.setReason(setup.getReason());

		repo.save(entity);
	}

	/**
	 * Hybrid resistance finder: 1) Detect swing highs from recent history
	 * (SWING_LOOKBACK) 2) Pick most recent valid swing-high (unbroken and not
	 * extremely old) 3) Confirm that it's also the highest high in last 35 candles
	 * (HYBRID check) 4) Provide proximity flag (last close within NEAR_RES_PCT of
	 * resistance)
	 */
	public ResistanceSetup findHybridResistance(List<StockDailyPrice> candles, String symbol) {
		ResistanceSetup setup = new ResistanceSetup();
		setup.setSymbol(symbol);

		if (candles == null || candles.size() < 40) {
			setup.setReason("Not enough candles");
			setup.setNearResistance(false);
			return setup;
		}

		int size = candles.size();

		// 1) Build list of swing highs in the lookback window
		int lookStart = Math.max(0, size - SWING_LOOKBACK);
		int lookEnd = size - 1;
		List<Integer> swingHighIndexes = findSwingHighIndexes(candles, lookStart, lookEnd);

		if (swingHighIndexes.isEmpty()) {
			setup.setReason("No swing highs detected");
			setup.setNearResistance(false);
			return setup;
		}

		// 2) Choose latest valid swing high (most recent unbroken swing-high)
		Integer selectedSwingIndex = chooseLatestValidSwingHigh(candles, swingHighIndexes);

		if (selectedSwingIndex == null) {
			setup.setReason("No valid swing high found");
			setup.setNearResistance(false);
			return setup;
		}

		double candidateResistance = candles.get(selectedSwingIndex).getHighPrice();

		// 3) Hybrid check: ensure candidate is also highest in last HYBRID_35_WINDOW
		// candles
		// (we use last 35 candles ending at most-recent candle)
		int hybridWindowStart = Math.max(0, size - HYBRID_35_WINDOW);
		double maxHighIn35 = findMaxHigh(candles, hybridWindowStart, size - 1);

		// If candidate is not the highest in last 35, we still allow it only if it's
		// close to that highest.
		// This is hybrid tolerance: candidate must be >= 0.995 * maxHighIn35
		// (adjustable)
		double candidateThreshold = maxHighIn35 * 0.995;

		if (candidateResistance < candidateThreshold) {
			// Candidate is not strong enough for hybrid confirmation -> try to fallback to
			// the 35-day highest
			candidateResistance = maxHighIn35;
			// find index of that maxHighIn35
			selectedSwingIndex = findIndexOfHigh(candles, hybridWindowStart, size - 1, maxHighIn35);
		}

		// 4) Proximity flag based on last close
		StockDailyPrice last = candles.get(size - 1);
		double lastClose = last.getClosePrice();
		double diffPercent = ((candidateResistance - lastClose) / candidateResistance) * 100.0;
		boolean near = diffPercent >= 0 && diffPercent <= NEAR_RES_PCT;

		setup.setResistance(candidateResistance);
		setup.setResistanceDate(candles.get(selectedSwingIndex).getTradeDate());
		setup.setLastClose(lastClose);
		setup.setNearResistance(near);
		setup.setDiffPercent(diffPercent);
		setup.setDate(last.getTradeDate().toString());
		setup.setReason(near ? "Price near hybrid resistance" : "Price not near hybrid resistance");

		return setup;
	}

	// ---------------------- helper utilities -----------------------

	/**
	 * Find indexes of swing-highs within window [from, to] A swing-high is defined
	 * as high[i] > high[i-1] && high[i] > high[i+1]
	 */
	private List<Integer> findSwingHighIndexes(List<StockDailyPrice> candles, int from, int to) {
		List<Integer> idx = new ArrayList<>();
		// ensure we do not go out of bounds
		for (int i = Math.max(from + 1, 1); i <= Math.min(to - 1, candles.size() - 2); i++) {
			double cur = candles.get(i).getHighPrice();
			double prev = candles.get(i - 1).getHighPrice();
			double next = candles.get(i + 1).getHighPrice();
			if (cur > prev && cur > next) {
				idx.add(i);
			}
		}
		return idx;
	}

	/**
	 * Choose the most recent swing-high that has not been breached (unbroken).
	 * "Unbroken" = there is no candle after that index with a close above that
	 * high. If the highest swing was long ago but price fell meaningfully from it,
	 * we prefer a later swing.
	 */
	private Integer chooseLatestValidSwingHigh(List<StockDailyPrice> candles, List<Integer> swingIndexes) {
		Collections.reverse(swingIndexes); // most recent first

		int size = candles.size();

		for (Integer swingIdx : swingIndexes) {
			double swingHigh = candles.get(swingIdx).getHighPrice();

			// Check if any close after swingIdx closed above swingHigh -> then it's broken
			boolean broken = false;
			for (int j = swingIdx + 1; j < size; j++) {
				if (candles.get(j).getClosePrice() > swingHigh) {
					broken = true;
					break;
				}
			}
			if (broken) {
				continue; // skip broken swings
			}

			// Check recency and structural validity:
			// If swing is too old relative to lookback, check if price fell >
			// FALL_INVALIDATE_PCT
			StockDailyPrice last = candles.get(size - 1);
			double lastClose = last.getClosePrice();
			double fallPercent = ((swingHigh - lastClose) / swingHigh) * 100.0;
			if (fallPercent >= FALL_INVALIDATE_PCT) {
				// treat as obsolete and skip
				continue;
			}

			// This swing is recent/unbroken and not obsolete → choose it
			return swingIdx;
		}

		// none found
		return null;
	}

	private double findMaxHigh(List<StockDailyPrice> candles, int from, int to) {
		double m = 0;
		for (int i = from; i <= to; i++) {
			m = Math.max(m, candles.get(i).getHighPrice());
		}
		return m;
	}

	private int findIndexOfHigh(List<StockDailyPrice> candles, int from, int to, double highValue) {
		for (int i = to; i >= from; i--) { // search reverse to get latest occurrence
			if (Double.compare(candles.get(i).getHighPrice(), highValue) == 0)
				return i;
		}
		return Math.max(from, to); // fallback to end
	}

	public List<BreakoutAnalysisResult> analyzeBreakouts() {

		List<ResistanceSetupEntity> setups = repo.findAll();
		List<BreakoutAnalysisResult> results = new ArrayList<>();

		for (ResistanceSetupEntity setup : setups) {

			List<StockDailyPrice> candles = priceService.geStocktDailyCandles(setup.getSymbol(),
					setup.getDetectedDate(), LocalDate.now());

			BreakoutAnalysisResult breakoutAnalysisResult = analyzeSingleSetup(setup, candles);
			results.add(breakoutAnalysisResult);
		}

		return results;
	}

	// Helper result builder (adjust fields as per your DTO)
	private BreakoutAnalysisResult analyzeSingleSetup(ResistanceSetupEntity setup, List<StockDailyPrice> candles) {
		String symbol = setup.getSymbol();
		LocalDate detectedDate = setup.getDetectedDate();
		LocalDate registenceDate = setup.getResistanceDate();
		double resistance = setup.getResistance();

		// Defensive: ensure candles are sorted ascending by date
		// (assume priceService returns sorted)

		// Initialize
		boolean firstBreakoutFound = false;
		LocalDate firstBreakoutDate = null;
		double maxHighTillFirstBreakout = Double.NEGATIVE_INFINITY;

		Double pullbackLow = null;

		LocalDate secondBreakoutDate = null;
		double maxHighAfterSecondBreakout = 0.0;

		// track index of first breakout to ensure ranges
		int firstBreakoutIndex = -1;

		for (int idx = 0; idx < candles.size(); idx++) {
			StockDailyPrice c = candles.get(idx);
			double high = c.getHighPrice();
			double low = c.getLowPrice();
			LocalDate date = c.getTradeDate();

			// only process candles on/after detectedDate
			if (date.isBefore(detectedDate))
				continue;

			if (!firstBreakoutFound) {
				// update max high BEFORE breakout
				maxHighTillFirstBreakout = Math.max(maxHighTillFirstBreakout, high);

				// check breakout: first candle with high > resistance
				if (high > resistance) {
					firstBreakoutFound = true;
					firstBreakoutDate = date;
					firstBreakoutIndex = idx;
					// ensure maxHighTillFirstBreakout does not include this breakout candle:
					// if it does, subtract it out by recomputing from earlier candles (defensive)
					// Recompute only if maxHighTillFirstBreakout >= resistance (data inconsistency)
					if (maxHighTillFirstBreakout >= resistance) {
						double recomputedMax = Double.NEGATIVE_INFINITY;
						for (int j = 0; j < idx; j++) {
							StockDailyPrice prev = candles.get(j);
							if (!prev.getTradeDate().isBefore(detectedDate)) { // include only from detectedDate
								recomputedMax = Math.max(recomputedMax, prev.getHighPrice());
							}
						}
						if (recomputedMax == Double.NEGATIVE_INFINITY) {
							maxHighTillFirstBreakout = 0.0;
						} else {
							maxHighTillFirstBreakout = recomputedMax;
						}
					}
					// move on to tracking pullback/second breakout
					continue;
				}
			} else {
				// AFTER first breakout
				// track pullback low near/below resistance (1% tolerance) if any
				if (low <= resistance * 1.01) {
					if (pullbackLow == null)
						pullbackLow = low;
					else
						pullbackLow = Math.min(pullbackLow, low);
				}

				// detect second breakout: price exceeds the max high from the first leg
				// only consider second breakout after at least one day has passed since
				// firstBreakoutIndex
				if (firstBreakoutIndex >= 0 && idx > firstBreakoutIndex) {
					if (high > maxHighTillFirstBreakout) {
						secondBreakoutDate = date;
						maxHighAfterSecondBreakout = high;
						// continue scanning to update maxHighAfterSecondBreakout in subsequent candles
						// but we can also break here if you only want first occurrence
						// break;
					}
				}

				// update maxHighAfterSecondBreakout if second already detected
				if (secondBreakoutDate != null) {
					maxHighAfterSecondBreakout = Math.max(maxHighAfterSecondBreakout, high);
				}
			}
		}

		// Defensive defaults
		if (maxHighTillFirstBreakout == Double.NEGATIVE_INFINITY)
			maxHighTillFirstBreakout = 0.0;

		BreakoutAnalysisResult result = new BreakoutAnalysisResult(symbol, resistance, registenceDate, firstBreakoutDate,
				maxHighTillFirstBreakout, pullbackLow, secondBreakoutDate, maxHighAfterSecondBreakout);

		return result;
	}

}
