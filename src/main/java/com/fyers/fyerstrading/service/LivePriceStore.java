package com.fyers.fyerstrading.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class LivePriceStore {

    // symbol → LTP
    private final Map<String, Double> priceMap = new ConcurrentHashMap<>();

    /**
     * Update LTP from WebSocket
     */
    public void updatePrice(String symbol, double ltp) {
        if (symbol == null || ltp <= 0) return;
        priceMap.put(symbol, ltp);
    }

    /**
     * Get latest price for a symbol
     */
    public double getPrice(String symbol) {
        return priceMap.getOrDefault(symbol, 0.0);
    }

    /**
     * Snapshot for REST API (read-only copy)
     * IMPORTANT: do NOT expose internal map
     */
    public Map<String, Double> snapshot() {
        return new HashMap<>(priceMap);
    }

    /**
     * Optional: clear prices (market close / reconnect)
     */
    public void clear() {
        priceMap.clear();
    }
}
