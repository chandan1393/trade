package com.fyers.fyerstrading.restcontroller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.TradeSetupForFNO;
import com.fyers.fyerstrading.model.BaselineResult;
import com.fyers.fyerstrading.model.FnoBacktestResult;
import com.fyers.fyerstrading.model.MasterCandle;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.TradeSetupFNORepository;
import com.fyers.fyerstrading.service.common.CandleFetcher;

@RestController
public class BackTestVolumePriceFNO {

	private static final Logger log = LoggerFactory.getLogger(BackTestVolumePriceFNO.class);

	private static final int LOOKAHEAD_DAYS = 3;
	
	@Autowired
	private CandleFetcher candleFetcher;

	@Autowired
	private StockDailyPriceRepository dailyPriceRepository;
	@Autowired
	TradeSetupFNORepository fnoRepository;

	@GetMapping("/backTestForFNO")
	public void runBaselineBacktest() {

        List<TradeSetupForFNO> setups = fnoRepository.findAll();
        List<BaselineResult> results = new ArrayList<>();

        System.out.println("===== DAILY BASELINE BACKTEST =====");
        System.out.println("Total setups: " + setups.size());
        System.out.println("----------------------------------");

        for (TradeSetupForFNO setup : setups) {

            BaselineResult result =
                    backtestSingleSetup(setup);

            results.add(result);

            System.out.println(
                    setup.getStockSymbol() +
                    " | SetupDate=" + setup.getTradeFoundDate() +
                    " | Result=" + result.getOutcome() +
                    " | R=" + result.getRMultiple()
            );
        }

        printSummary(results);
    }

    // =====================================================
    // BACKTEST SINGLE SETUP
    // =====================================================
    private BaselineResult backtestSingleSetup(TradeSetupForFNO setup) {

        LocalDate startDate = setup.getTradeFoundDate().plusDays(1);
        LocalDate endDate   = startDate.plusDays(LOOKAHEAD_DAYS);

        List<StockDailyPrice> days =
        		dailyPriceRepository.findAllBWDate(
                        setup.getStockSymbol(),
                        startDate,
                        endDate);

        double entry = setup.getEntryPrice();
        double sl    = setup.getStopLoss();
        double t1    = setup.getTarget1();

        double risk = entry - sl;

        for (StockDailyPrice day : days) {

            // 1️⃣ STOP LOSS FIRST
            if (day.getLowPrice() <= sl) {
                return new BaselineResult(
                        setup.getStockSymbol(),
                        setup.getTradeFoundDate(),
                        "LOSS",
                        -1.0
                );
            }

            // 2️⃣ TARGET HIT
            if (day.getHighPrice() >= t1) {
                double rMultiple = (t1 - entry) / risk;
                return new BaselineResult(
                        setup.getStockSymbol(),
                        setup.getTradeFoundDate(),
                        "WIN",
                        round(rMultiple)
                );
            }
        }

        // 3️⃣ NEUTRAL (NO FOLLOW THROUGH)
        return new BaselineResult(
                setup.getStockSymbol(),
                setup.getTradeFoundDate(),
                "NEUTRAL",
                0.0
        );
    }

    // =====================================================
    // SUMMARY
    // =====================================================
    private void printSummary(List<BaselineResult> results) {

        long total   = results.size();
        long wins    = results.stream().filter(r -> r.getOutcome().equals("WIN")).count();
        long losses  = results.stream().filter(r -> r.getOutcome().equals("LOSS")).count();
        long neutral = results.stream().filter(r -> r.getOutcome().equals("NEUTRAL")).count();

        double avgR =
                results.stream()
                        .mapToDouble(BaselineResult::getRMultiple)
                        .average()
                        .orElse(0);

        System.out.println("========== SUMMARY ==========");
        System.out.println("Total setups : " + total);
        System.out.println("Wins         : " + wins);
        System.out.println("Losses       : " + losses);
        System.out.println("Neutral      : " + neutral);
        System.out.println("Win Rate     : " + round((wins * 100.0) / total) + "%");
        System.out.println("Avg R        : " + round(avgR));
        System.out.println("=============================");
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

}
