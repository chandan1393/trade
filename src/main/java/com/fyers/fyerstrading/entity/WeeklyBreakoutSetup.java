package com.fyers.fyerstrading.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class WeeklyBreakoutSetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;

    private LocalDate weekStarting;

    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;

    private double avgVolume;
    private double bodyPercent;
    private double upperWickPercent;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public LocalDate getWeekStarting() {
		return weekStarting;
	}
	public void setWeekStarting(LocalDate weekStarting) {
		this.weekStarting = weekStarting;
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


    
}

