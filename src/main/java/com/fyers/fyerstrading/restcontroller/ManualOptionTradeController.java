package com.fyers.fyerstrading.restcontroller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.model.ManualOrderRequest;
import com.fyers.fyerstrading.model.NetPosition;
import com.fyers.fyerstrading.model.PositionResponse;
import com.fyers.fyerstrading.service.FyersApiService;
import com.fyers.fyerstrading.service.ManualOptionTradeManagerService;

@RestController
@RequestMapping("/api/options")
public class ManualOptionTradeController {

	private final ManualOptionTradeManagerService tradeService;
	private final FyersApiService fyersApiService;

	public ManualOptionTradeController(ManualOptionTradeManagerService tradeService,FyersApiService fyersApiService) {
		this.tradeService = tradeService;
		this.fyersApiService=fyersApiService;
	}

	/*
	 * @PostMapping("/place") public ResponseEntity<?> place(@RequestBody
	 * ManualOrderRequest req) { ManualOptionTrade t =
	 * tradeService.placeManualOrder(req); return ResponseEntity.ok(t); }
	 */

	/*
	 * @PostMapping("/placeFromDb/{symbol}") public ResponseEntity<?>
	 * placeFromDb(@PathVariable String symbol) { ManualOptionTrade t =
	 * tradeService.placeOrderFromDb(symbol); return ResponseEntity.ok(t); }
	 */

	@PostMapping("/saveManualOptionTargets")
	public ResponseEntity<?> saveTargets(@RequestBody ManualOrderRequest req) {
		if (req.symbol == null)
			return ResponseEntity.badRequest().body("symbol is required");

		if (req.stopLoss == null || req.target == null)
			return ResponseEntity.badRequest().body("stopLoss and target are required");

		tradeService.saveTargetAndSl(req.symbol, req.stopLoss, req.target);

		return ResponseEntity.ok("Targets saved & OCO placed/updated for " + req.symbol);
	}

	@PostMapping("/cancelOco/{symbol}")
	public ResponseEntity<?> cancelOco(@PathVariable String symbol) {
		boolean ok = tradeService.cancelOco(symbol);
		return ResponseEntity.ok(Map.of("cancelled", ok));
	}

	@GetMapping("/active")
	public ResponseEntity<?> active() {
		return ResponseEntity.ok(tradeService.getAllActiveTrades());
	}

	// endpoint to receive live ticks (from websocket) - call frequently
	@PostMapping("/tick")
	public ResponseEntity<?> tick(@RequestBody Map<String, Object> tick) {
		String symbol = (String) tick.get("symbol");
		double ltp = ((Number) tick.get("ltp")).doubleValue();
		tradeService.handleLiveTick(symbol, ltp);
		return ResponseEntity.ok("tick processed");
	}

	// sync positions (calls fyers + process)
	@PostMapping("/syncPositions")
	public ResponseEntity<?> syncPositions() {
		PositionResponse positionResponse = fyersApiService.getPositions();
		List<NetPosition> netPositionList = positionResponse.getNetPositions();
		tradeService.processFyersPositions(netPositionList);
		return ResponseEntity.ok("processed");
	}
}
