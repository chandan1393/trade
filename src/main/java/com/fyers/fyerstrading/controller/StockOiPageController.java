package com.fyers.fyerstrading.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.repo.StockMasterRepository;

@Controller
@RequestMapping("/oi")
public class StockOiPageController {

	@Autowired
	private StockMasterRepository masterRepo;
	
	
	@GetMapping("/stock-oi")
	public String openStockOiPage(Model model) {
		List<StockMaster> list = masterRepo.findByIsInFnoTrue();
		model.addAttribute("list", list);
		return "oi-stock-dashboard"; // this must match stock-oi.html
	}
}
