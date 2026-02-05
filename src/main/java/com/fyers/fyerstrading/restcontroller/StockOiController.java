package com.fyers.fyerstrading.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.dto.StockOiSnapshot;
import com.fyers.fyerstrading.service.StockOIService;

@RestController
@RequestMapping("/api/stock-oi")
public class StockOiController {

	@Autowired
    private  StockOIService service;

  

    @GetMapping("/snapshot")
    public StockOiSnapshot getSnapshot(@RequestParam String symbol) {
        return service.getSnapshot(symbol);
    }
}
