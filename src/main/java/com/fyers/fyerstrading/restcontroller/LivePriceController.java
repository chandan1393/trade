package com.fyers.fyerstrading.restcontroller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.service.LivePriceStore;

@RestController
@RequestMapping("/api")
public class LivePriceController {

    @Autowired
    private LivePriceStore livePriceStore;

    @GetMapping("/ltp")
    public Map<String, Double> getAllLtps() {
        return livePriceStore.snapshot();
    }
}

