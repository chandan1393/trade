package com.fyers.fyerstrading.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "active_trades")
public class ActiveTrades {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String stockSymbol;
	private double entryPrice;
	private double exitPrice;
	private double stopLoss;
	private double targetPrice;
	private LocalDateTime entryTime;
	private LocalDateTime exitTime;
	private boolean isProfit; // True if target was hit, false if stop loss hit
	private double profitLoss; // Amount of profit or loss
	private boolean isClosed;
	private double atr;
	
	public ActiveTrades() {
		
	}

	public ActiveTrades(String stockSymbol, double entryPrice, double exitPrice, double stopLoss,
			double targetPrice, LocalDateTime entryTime, LocalDateTime exitTime, boolean isProfit, double profitLoss,boolean isClosed,double atr) {
		super();
		this.stockSymbol = stockSymbol;
		this.entryPrice = entryPrice;
		this.exitPrice = exitPrice;
		this.stopLoss = stopLoss;
		this.targetPrice = targetPrice;
		this.entryTime = entryTime;
		this.exitTime = exitTime;
		this.isProfit = isProfit;
		this.profitLoss = profitLoss;
		this.isClosed=isClosed;
		this.atr=atr;
	}


	// Getters & Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStockSymbol() {
		return stockSymbol;
	}

	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}

	public double getEntryPrice() {
		return entryPrice;
	}

	public void setEntryPrice(double entryPrice) {
		this.entryPrice = entryPrice;
	}

	public double getExitPrice() {
		return exitPrice;
	}

	public void setExitPrice(double exitPrice) {
		this.exitPrice = exitPrice;
	}

	public double getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}

	public double getTargetPrice() {
		return targetPrice;
	}

	public void setTargetPrice(double targetPrice) {
		this.targetPrice = targetPrice;
	}

	public LocalDateTime getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(LocalDateTime entryTime) {
		this.entryTime = entryTime;
	}

	public LocalDateTime getExitTime() {
		return exitTime;
	}

	public void setExitTime(LocalDateTime exitTime) {
		this.exitTime = exitTime;
	}

	public boolean isProfit() {
		return isProfit;
	}

	public void setProfit(boolean isProfit) {
		this.isProfit = isProfit;
	}

	public double getProfitLoss() {
		return profitLoss;
	}

	public void setProfitLoss(double profitLoss) {
		this.profitLoss = profitLoss;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public double getAtr() {
		return atr;
	}

	public void setAtr(double atr) {
		this.atr = atr;
	}
	
	
	
}
