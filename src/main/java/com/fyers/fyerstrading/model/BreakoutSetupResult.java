package com.fyers.fyerstrading.model;

import java.time.LocalDate;

public class BreakoutSetupResult {
    private String stockName;
    private LocalDate weekStart;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
    private double avgVolume;
    private double bodyPercent;
    private double upperWickPercent;
    private boolean yearHighBreakout;

    

    public String getStockName() {
		return stockName;
	}



	public void setStockName(String stockName) {
		this.stockName = stockName;
	}



	public LocalDate getWeekStart() {
		return weekStart;
	}



	public void setWeekStart(LocalDate weekStart) {
		this.weekStart = weekStart;
	}



	public double getHigh() {
		return high;
	}



	public void setHigh(double high) {
		this.high = high;
	}



	public double getVolume() {
		return volume;
	}



	public void setVolume(double volume) {
		this.volume = volume;
	}



	public double getAvgVolume() {
		return avgVolume;
	}



	public void setAvgVolume(double avgVolume) {
		this.avgVolume = avgVolume;
	}



	public double getBodyPercent() {
		return bodyPercent;
	}



	public void setBodyPercent(double bodyPercent) {
		this.bodyPercent = bodyPercent;
	}



	public double getUpperWickPercent() {
		return upperWickPercent;
	}



	public void setUpperWickPercent(double upperWickPercent) {
		this.upperWickPercent = upperWickPercent;
	}



	public boolean isYearHighBreakout() {
		return yearHighBreakout;
	}



	public void setYearHighBreakout(boolean yearHighBreakout) {
		this.yearHighBreakout = yearHighBreakout;
	}



	public double getOpen() {
		return open;
	}



	public void setOpen(double open) {
		this.open = open;
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



	public BreakoutSetupResult(String stockName, LocalDate weekStart, double high,double low,double open,double close,
                                double volume, double avgVolume, double bodyPercent,
                                double upperWickPercent, boolean yearHighBreakout) {
        this.stockName = stockName;
        this.weekStart = weekStart;
        this.high = high;
        this.low=low;
        this.open=open;
        this.close=close;
        this.volume = volume;
        this.avgVolume = avgVolume;
        this.bodyPercent = bodyPercent;
        this.upperWickPercent = upperWickPercent;
        this.yearHighBreakout = yearHighBreakout;
    }
}

