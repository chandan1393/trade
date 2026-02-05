package com.fyers.fyerstrading.model;

import java.time.LocalDateTime;

public class TradeMasterSignal {
    public enum Direction { LONG, SHORT }
    public String symbol;          // underlying symbol
    public String optionSymbol;    // chosen option CE/PE symbol
    public Direction direction;
    public double underlyingAtSignal;
    public double optionEntryPrice; // premium at execution (or expected)
    public double optionSL;         // premium level
    public double optionTarget;     // premium level
    public double optionTarget2;
    public double qty;              // lots/quantity
    public String reason;
    public LocalDateTime timestamp;

    @Override
    public String toString() {
        return timestamp + " " + direction + " " + optionSymbol + " entry=" + optionEntryPrice
                + " SL=" + optionSL + " TG=" + optionTarget + " | " + reason;
    }

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getOptionSymbol() {
		return optionSymbol;
	}

	public void setOptionSymbol(String optionSymbol) {
		this.optionSymbol = optionSymbol;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public double getUnderlyingAtSignal() {
		return underlyingAtSignal;
	}

	public void setUnderlyingAtSignal(double underlyingAtSignal) {
		this.underlyingAtSignal = underlyingAtSignal;
	}

	public double getOptionEntryPrice() {
		return optionEntryPrice;
	}

	public void setOptionEntryPrice(double optionEntryPrice) {
		this.optionEntryPrice = optionEntryPrice;
	}

	public double getOptionSL() {
		return optionSL;
	}

	public void setOptionSL(double optionSL) {
		this.optionSL = optionSL;
	}

	public double getOptionTarget() {
		return optionTarget;
	}

	public void setOptionTarget(double optionTarget) {
		this.optionTarget = optionTarget;
	}

	public double getQty() {
		return qty;
	}

	public void setQty(double qty) {
		this.qty = qty;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public double getOptionTarget2() {
		return optionTarget2;
	}

	public void setOptionTarget2(double optionTarget2) {
		this.optionTarget2 = optionTarget2;
	}
    
    
    
    
    
}