package com.fyers.fyerstrading.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignalDecision {
    public enum Side { BUY_CE, BUY_PE, NO_TRADE }

    private Side side;
    private String reason;        // short explanation
    private Double strike;        // suggested strike
    private String optionSymbol;  // suggested option symbol (if known)
    private Double entryPrice;    // suggested entry (ask)
    private Double stopLoss;      // stop price for option
    private Double target;        // target price for option
    private int quantity;
	  
}
