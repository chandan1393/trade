package com.fyers.fyerstrading.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fyers.fyerstrading.repo.OptionTradeRepository;
import com.fyers.fyerstrading.utility.MarketTimeUtil;

@Controller
public class DashboardController {

    @Autowired
    private OptionTradeRepository tradeRepo;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {

        if (session.getAttribute("USER") == null) {
            return "redirect:/login";
        }

        long openTrades = tradeRepo.countByStatus("OPEN");

        model.addAttribute("openTrades", openTrades);
        model.addAttribute("marketStatus", MarketTimeUtil.isMarketOpen() ? "OPEN" : "CLOSED");
        model.addAttribute("wsStatus", "CONNECTED"); // wire real status later

        return "dashboard";
    }
}

