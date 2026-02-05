package com.fyers.fyerstrading.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fyers.fyerstrading.dto.ExitPlanRequest;
import com.fyers.fyerstrading.entity.IndexOptionTradeLeg;
import com.fyers.fyerstrading.enu.InstrumentType;
import com.fyers.fyerstrading.model.OptionTrade;
import com.fyers.fyerstrading.model.StockOptionTradeLeg;
import com.fyers.fyerstrading.repo.IndexOptionTradeLegRepository;
import com.fyers.fyerstrading.repo.StockOptionTradeLegRepository;
import com.fyers.fyerstrading.service.LivePriceStore;
import com.fyers.fyerstrading.service.OptionTradeLifecycleManager;

@Controller
public class PositionController {

	@Autowired
	private OptionTradeLifecycleManager manager;

	@Autowired
	private StockOptionTradeLegRepository stockLegRepo;
	
	@Autowired
	private IndexOptionTradeLegRepository indexLegRepo;

	// ================= STOCK POSITIONS =================
	@Autowired
	private LivePriceStore livePriceStore;

	@GetMapping("/positions/stocks")
	public String stockPositions(Model model, HttpSession session) {

		if (session.getAttribute("USER") == null) {
			return "redirect:/ui/login";
		}

		List<OptionTrade> trades = manager.getAllOpenTrades().stream()
				.filter(t -> t.getInstrumentType() == InstrumentType.STOCK_OPTION).collect(Collectors.toList());

		List<Long> tradeIds = trades.stream().map(OptionTrade::getTradeId).collect(Collectors.toList());

		Map<Long, List<StockOptionTradeLeg>> legMap = stockLegRepo.findByTradeIdIn(tradeIds).stream()
				.collect(Collectors.groupingBy(StockOptionTradeLeg::getTradeId));

		for (OptionTrade trade : trades) {
			trade.setStockLegs(legMap.getOrDefault(trade.getTradeId(), new ArrayList<>()));
		}

		model.addAttribute("trades", trades);
		model.addAttribute("tradingMode", "LIVE");

		return "positions-stocks";
	}

	// ================= INDEX POSITIONS =================
	@GetMapping("/positions/index")
	public String indexPositions(Model model, HttpSession session) {

		if (session.getAttribute("USER") == null) {
			return "redirect:/login";
		}

		List<OptionTrade> trades = manager.getAllOpenTrades().stream()
				.filter(t -> t.getInstrumentType() == InstrumentType.INDEX_OPTION).collect(Collectors.toList());

		// 🔥 Bulk fetch legs (avoid N+1)
		List<Long> tradeIds = trades.stream().map(OptionTrade::getTradeId).collect(Collectors.toList());

		Map<Long, List<IndexOptionTradeLeg>> legMap = indexLegRepo.findByTradeIdIn(tradeIds).stream()
				.collect(Collectors.groupingBy(IndexOptionTradeLeg::getTradeId));

		for (OptionTrade trade : trades) {
			trade.setIndexLegs(legMap.getOrDefault(trade.getTradeId(), new ArrayList<>()));
		}

		model.addAttribute("trades", trades);
		model.addAttribute("tradingMode", "LIVE");

		return "positions-index";
	}

	// ================= UPDATE EXIT PLAN =================
	@PostMapping("/positions/update")
	public String updateTargets(@RequestParam Long tradeId, @RequestParam double sl, @RequestParam double t1,
			@RequestParam double t2, @RequestParam double t3, @RequestParam String type // STOCK or INDEX
	) {

		ExitPlanRequest request = new ExitPlanRequest();
		request.setExpiry(null);
		request.setStructureSL(sl);
		request.setT1(t1);
		request.setT2(t2);
		request.setT3(t3);

		manager.updateExitPlan(tradeId, request);

		// Redirect back to correct screen
		if ("INDEX".equalsIgnoreCase(type)) {
			return "redirect:/positions/index";
		}
		return "redirect:/positions/stocks";
	}
}
