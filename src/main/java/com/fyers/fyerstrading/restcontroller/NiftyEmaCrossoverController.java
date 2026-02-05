package com.fyers.fyerstrading.restcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.service.NiftyEmaCrossoverService;

@RestController
@RequestMapping("/api/nifty/ema-crossover")
public class NiftyEmaCrossoverController {

    private final NiftyEmaCrossoverService crossoverService;

    public NiftyEmaCrossoverController(NiftyEmaCrossoverService crossoverService) {
        this.crossoverService = crossoverService;
    }

	/*
	 * @GetMapping("/check-latest") public ResponseEntity<String> checkLatest() {
	 * return ResponseEntity.ok(crossoverService.checkCrossoverWithBaseFilter(5,
	 * 0.2)); }
	 */

	/*
	 * @GetMapping("/last-signal") public ResponseEntity<String> lastSignal() {
	 * return ResponseEntity.ok(crossoverService.getLastSignal()); }
	 * 
	 * @GetMapping("/backtest") public ResponseEntity<List<EMABacktestSignal>>
	 * backtest() { return
	 * ResponseEntity.ok(crossoverService.backtestWithExitsIntraday(5, 0.2)); }
	 */

	/*
	 * @PostMapping("/exit-eod") public ResponseEntity<String> exitEod() { return
	 * ResponseEntity.ok(crossoverService.exitAtDayEnd()); }
	 */
}
