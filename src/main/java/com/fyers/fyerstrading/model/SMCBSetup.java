package com.fyers.fyerstrading.model;

import java.time.LocalDate;

import com.fyers.fyerstrading.entity.StockDailyPrice;

public class SMCBSetup {
	private LocalDate tradeDate;
	private double ema9, ema20, ema50, rsi;
	private double volume, deliveryPercent;
	private double closePrice, highPrice, recentHigh;
	private int score;

	public SMCBSetup(LocalDate tradeDate, double ema9, double ema20, double ema50, double rsi, double volume,
			double deliveryPercent, double closePrice, double highPrice, double recentHigh, int score) {
		this.tradeDate = tradeDate;
		this.ema9 = ema9;
		this.ema20 = ema20;
		this.ema50 = ema50;
		this.rsi = rsi;
		this.volume = volume;
		this.deliveryPercent = deliveryPercent;
		this.closePrice = closePrice;
		this.highPrice = highPrice;
		this.recentHigh = recentHigh;
		this.score = score;
	}

	// Getters
	public LocalDate getTradeDate() {
		return tradeDate;
	}

	public double getEma9() {
		return ema9;
	}

	public double getEma20() {
		return ema20;
	}

	public double getEma50() {
		return ema50;
	}

	public double getRsi() {
		return rsi;
	}

	public double getVolume() {
		return volume;
	}

	public double getDeliveryPercent() {
		return deliveryPercent;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public double getHighPrice() {
		return highPrice;
	}

	public double getRecentHigh() {
		return recentHigh;
	}

	public int getScore() {
		return score;
	}
}