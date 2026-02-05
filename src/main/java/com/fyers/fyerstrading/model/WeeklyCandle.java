package com.fyers.fyerstrading.model;

import java.time.LocalDate;

public class WeeklyCandle {
    private String symbol;
    private LocalDate weekStarting;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}
    
	public LocalDate getWeekStarting() {
        return weekStarting;
    }

    public void setWeekStarting(LocalDate weekStarting) {
        this.weekStarting = weekStarting;
    }
    
    
}
