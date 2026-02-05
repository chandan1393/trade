package com.fyers.fyerstrading.schedular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.service.IndexOIService;
import com.fyers.fyerstrading.utility.MarketTimeUtil;

@Component
@ConditionalOnProperty(name = "oi.index.enabled", havingValue = "true")
public class IndexOIScheduler {

    @Autowired
    private IndexOIService indexOIService;

    @Autowired
    private ConfigurableApplicationContext context;

    @Scheduled(fixedDelayString = "${oi.index.frequency.ms}")
    public void run() {

        if (!context.isActive()) return;
        if (!MarketTimeUtil.isMarketOpen()) return;

        indexOIService.fetchAndStore();
    }
}
