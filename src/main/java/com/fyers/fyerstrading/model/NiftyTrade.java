package com.fyers.fyerstrading.model;

import java.time.LocalDateTime;

public class NiftyTrade {
	private String side; // BUY or SELL
	private LocalDateTime entryTime;
	private double entryPrice;
	private double stopLoss;
	private double target;
	private double tsl;
	private double exitPrice;
	private String result;
	private LocalDateTime exitTime;

	public NiftyTrade() {
		
	}

	public NiftyTrade(String side, LocalDateTime entryTime, double entryPrice, double stopLoss, double target) {
		this.side = side;
		this.entryTime = entryTime;
		this.entryPrice = entryPrice;
		this.stopLoss = stopLoss;
		this.target = target;
	}

	public String getSide() {
		return side;
	}

	public LocalDateTime getEntryTime() {
		return entryTime;
	}

	public double getEntryPrice() {
		return entryPrice;
	}

	public double getStopLoss() {
		return stopLoss;
	}

	public double getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return "Side: " + side + ", Entry: " + entryPrice + ", SL: " + stopLoss + ", Target: " + target;
	}

	public double getTsl() {
		return tsl;
	}

	public void setTsl(double tsl) {
		this.tsl = tsl;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public void setEntryTime(LocalDateTime entryTime) {
		this.entryTime = entryTime;
	}

	public void setEntryPrice(double entryPrice) {
		this.entryPrice = entryPrice;
	}

	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}

	public void setTarget(double target) {
		this.target = target;
	}

	public double getExitPrice() {
		return exitPrice;
	}

	public void setExitPrice(double exitPrice) {
		this.exitPrice = exitPrice;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public LocalDateTime getExitTime() {
		return exitTime;
	}

	public void setExitTime(LocalDateTime exitTime) {
		this.exitTime = exitTime;
	}
	
	
	
}
