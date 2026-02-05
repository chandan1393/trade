package com.fyers.fyerstrading.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "best_trade_setup")
public class BestTradeSetup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String stockSymbol;
	private LocalDate tradeDate; // The date when setup was found
	private double ema9;
	private double ema21;
	private double atr;
	private double rsi;
	private double closePrice;
	private double highPrice;
	private double volume;
	private double avgVolume;
	private double entryPrice;
	private double exitPrice;
	private double stopLossPrice;
	private boolean tradeExecuted = false; // Flag to indicate if trade is executed

	public BestTradeSetup() {
	}

	public BestTradeSetup(String stockSymbol, LocalDate tradeDate, double ema9, double ema21, double atr, double rsi,
			double closePrice, double highPrice, double volume, double avgVolume,double entryPrice,double exitPrice,double stopLossPrice) {
		this.stockSymbol = stockSymbol;
		this.tradeDate = tradeDate;
		this.ema9 = ema9;
		this.ema21 = ema21;
		this.atr = atr;
		this.rsi = rsi;
		this.closePrice = closePrice;
		this.highPrice = highPrice;
		this.volume = volume;
		this.avgVolume = avgVolume;
		this.entryPrice=entryPrice;
		this.exitPrice=exitPrice;
		this.stopLossPrice=stopLossPrice;
		this.tradeExecuted = false; // Default value
	}

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

	public LocalDate getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate = tradeDate;
	}

	public double getEma9() {
		return ema9;
	}

	public void setEma9(double ema9) {
		this.ema9 = ema9;
	}

	public double getEma21() {
		return ema21;
	}

	public void setEma21(double ema21) {
		this.ema21 = ema21;
	}

	public double getAtr() {
		return atr;
	}

	public void setAtr(double atr) {
		this.atr = atr;
	}

	public double getRsi() {
		return rsi;
	}

	public void setRsi(double rsi) {
		this.rsi = rsi;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}

	public double getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
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

	public boolean isTradeExecuted() {
		return tradeExecuted;
	}

	public void setTradeExecuted(boolean tradeExecuted) {
		this.tradeExecuted = tradeExecuted;
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

	public double getStopLossPrice() {
		return stopLossPrice;
	}

	public void setStopLossPrice(double stopLossPrice) {
		this.stopLossPrice = stopLossPrice;
	}

}
