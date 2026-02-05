package com.fyers.fyerstrading.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fyers.fyerstrading.entity.IndexOIData;
import com.fyers.fyerstrading.repo.IndexOIRepository;
import com.fyers.fyerstrading.service.LivePriceStore;

@Controller
@RequestMapping("/oi")
public class NiftyOController {

	@Autowired
	private IndexOIRepository indexOIRepo;

	@Autowired
	private LivePriceStore livePriceStore;

	@GetMapping("/nifty")
	public String niftyOiDashboard(Model model) {

		String symbol = "NSE:NIFTY50-INDEX";

		LocalDate today = LocalDate.now();
		LocalDateTime from = today.atStartOfDay();
		LocalDateTime to = LocalDateTime.now();

		List<IndexOIData> rows = indexOIRepo.findBySymbolAndDateBetween(symbol, from, to);

		
		//double niftyLtp = livePriceStore.getPrice("NSE:NIFTY50-INDEX");
		double niftyLtp = 25088;
		double vix = rows.isEmpty() ? 0 : rows.get(0).getIndiaVixLtp();

		model.addAttribute("rows", rows);
		model.addAttribute("niftyLtp", niftyLtp);
		model.addAttribute("vix", vix);
		model.addAttribute("date", today);

		return "oi-nifty-dashboard";
	}
	
	
	@GetMapping("/nifty-oi")
    public String niftyOiPage() {
        return "nifty-oi"; // file name without .html
    }
}
