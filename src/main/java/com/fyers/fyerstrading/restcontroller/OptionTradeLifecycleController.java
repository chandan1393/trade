package com.fyers.fyerstrading.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.dto.ExitPlanRequest;
import com.fyers.fyerstrading.model.OptionTrade;
import com.fyers.fyerstrading.service.OptionTradeLifecycleManager;

@RestController
@RequestMapping("/api/optionTrades")
public class OptionTradeLifecycleController {

	@Autowired
	private OptionTradeLifecycleManager manager;

	@GetMapping("/syncPosition")
	public boolean syncPosition() {
		manager.syncFromBroker();
		return true;
	}

	@GetMapping("/open")
	public List<OptionTrade> getOpenTrades() {
		return manager.getAllOpenTrades();
	}

	
	@GetMapping("/openWithPendingTarget")
	public List<OptionTrade> getOpenTradesWithoutTarget() {
		return manager.getAllOpenTradesWithoutTarget();
	}
	
	
	@PutMapping("/{tradeId}/exit-plan")
	public OptionTrade updateExitPlan(@PathVariable Long tradeId, @RequestBody ExitPlanRequest request) {

		return manager.updateExitPlan(tradeId, request);
	}

}
