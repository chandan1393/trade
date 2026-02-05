package com.fyers.fyerstrading.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.OptionTradeExecution;
import com.fyers.fyerstrading.repo.OptionTradeExecutionRepository;

@Service
public class OptionTradeExecutionService {

	private final Map<String, OptionTradeExecution> openTrades = new ConcurrentHashMap<>();

	
    @Autowired
    private OptionTradeExecutionRepository repo;

    public void openTrade(String symbol, String action, double entryPrice, double sl, double target) {
        OptionTradeExecution trade = new OptionTradeExecution();
        trade.setSymbol(symbol);
        trade.setAction(action);
        trade.setEntryPrice(entryPrice);
        trade.setStopLoss(sl);
        trade.setTarget(target);
        trade.setEntryTime(LocalDateTime.now());

        repo.save(trade);
        openTrades.put(symbol, trade); // <== This is critical
    }

    public void processLivePrice(String symbol, double currentPrice) {
        List<OptionTradeExecution> openTrades = repo.findByExitTimeIsNull();

        for (OptionTradeExecution trade : openTrades) {
            if (!trade.getSymbol().equals(symbol)) continue;

            if (trade.getAction().equals("BUY")) {
                if (currentPrice <= trade.getStopLoss()) {
                    closeTrade(trade, currentPrice, "SL hit");
                } else if (currentPrice >= trade.getTarget()) {
                    closeTrade(trade, currentPrice, "Target hit");
                }
            } else {
                if (currentPrice >= trade.getStopLoss()) {
                    closeTrade(trade, currentPrice, "SL hit");
                } else if (currentPrice <= trade.getTarget()) {
                    closeTrade(trade, currentPrice, "Target hit");
                }
            }
        }
    }

    public void closeTrade(OptionTradeExecution trade, double exitPrice, String reason) {
        trade.setExitPrice(exitPrice);
        trade.setExitReason(reason);
        trade.setExitTime(LocalDateTime.now());
        repo.save(trade);
        openTrades.remove(trade.getSymbol()); // <== Clean up
    }
    
    
    
    public boolean hasOpenTrade(String symbol) {
        OptionTradeExecution trade = openTrades.get(symbol);
        return trade != null && trade.getExitTime() == null;
    }
}


