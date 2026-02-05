package com.fyers.fyerstrading.service;

import org.springframework.stereotype.Component;

@Component
public interface OrderService {

	// place limit buy of option, return order id
    String placeBuyLimit(String optionSymbol, int qty, double limitPrice);

    // place market buy (if needed)
    String placeBuyMarket(String optionSymbol, int qty);

    // place OCO (if broker supports) or external monitoring
    void placeOco(String orderId, double stopPrice, double targetPrice);

    // cancel
    void cancelOrder(String orderId);
    
}
