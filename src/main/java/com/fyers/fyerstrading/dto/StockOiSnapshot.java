package com.fyers.fyerstrading.dto;

import java.util.List;

import com.fyers.fyerstrading.entity.StockOIData;

public class StockOiSnapshot {

    private String symbol;
    private List<StockOIData> strikes;
    private double support;
    private double resistance;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public List<StockOIData> getStrikes() {
		return strikes;
	}
	public void setStrikes(List<StockOIData> strikes) {
		this.strikes = strikes;
	}
	public double getSupport() {
		return support;
	}
	public void setSupport(double support) {
		this.support = support;
	}
	public double getResistance() {
		return resistance;
	}
	public void setResistance(double resistance) {
		this.resistance = resistance;
	}

    
}
