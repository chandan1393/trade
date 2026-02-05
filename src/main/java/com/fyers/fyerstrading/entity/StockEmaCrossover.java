package com.fyers.fyerstrading.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "stock_ema_crossover")
public class StockEmaCrossover {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String stockSymbol;

    @Column(nullable = false)
    private LocalDate tradeDate;

    @Column(nullable = false)
    private double currentEma9;

    @Column(nullable = false)
    private double currentEma21;
    
    @Column(nullable = false)
    private double previousEma9;

    @Column(nullable = false)
    private double previousEma21;

    @Column(nullable = false)
    private double atr;
    
    @Column(nullable = false)
    private double volume;
    
    @Column(nullable = false)
    private double avgVolume;
    
    @Column(nullable = false)
    private double rsi;
    
    @Column(nullable = false)
    private double closePrice;
    
    @Column(nullable = false)
    private double highPrice;
    
    @Column(nullable = false)
    private double lowPrice;
    
    @Column(nullable = false)
    private double openPrice;
    
    @Column(nullable = false)
    private double stockScore;
    
    @Column(nullable = false)
    private String crossoverType; // "Bullish" (9 EMA crosses above 21 EMA), "Bearish" (9 EMA crosses below 21 EMA)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

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

	public String getCrossoverType() {
		return crossoverType;
	}

	public void setCrossoverType(String crossoverType) {
		this.crossoverType = crossoverType;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public double getAtr() {
		return atr;
	}

	public void setAtr(double atr) {
		this.atr = atr;
	}

	public double getAvgVolume() {
		return avgVolume;
	}

	public void setAvgVolume(double avgVolume) {
		this.avgVolume = avgVolume;
	}

	public double getRsi() {
		return rsi;
	}

	public void setRsi(double rsi) {
		this.rsi = rsi;
	}

	public double getCurrentEma9() {
		return currentEma9;
	}

	public void setCurrentEma9(double currentEma9) {
		this.currentEma9 = currentEma9;
	}

	public double getCurrentEma21() {
		return currentEma21;
	}

	public void setCurrentEma21(double currentEma21) {
		this.currentEma21 = currentEma21;
	}

	public double getPreviousEma9() {
		return previousEma9;
	}

	public void setPreviousEma9(double previousEma9) {
		this.previousEma9 = previousEma9;
	}

	public double getPreviousEma21() {
		return previousEma21;
	}

	public void setPreviousEma21(double previousEma21) {
		this.previousEma21 = previousEma21;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getStockScore() {
		return stockScore;
	}

	public void setStockScore(double stockScore) {
		this.stockScore = stockScore;
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

	public double getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(double lowPrice) {
		this.lowPrice = lowPrice;
	}

	public double getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
	}

    
    
    
}