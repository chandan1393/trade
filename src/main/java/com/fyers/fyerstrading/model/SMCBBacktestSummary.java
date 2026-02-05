package com.fyers.fyerstrading.model;

import java.time.LocalDate;

public class SMCBBacktestSummary {
	private int totalTrades;
	private long wins;
	private long losses;
	private double winRate;
	private double avgR;

	public SMCBBacktestSummary(int totalTrades, long wins, long losses, double winRate, double avgR) {
		this.totalTrades = totalTrades;
		this.wins = wins;
		this.losses = losses;
		this.winRate = winRate;
		this.avgR = avgR;
	}

	public int getTotalTrades() {
		return totalTrades;
	}

	public long getWins() {
		return wins;
	}

	public long getLosses() {
		return losses;
	}

	public double getWinRate() {
		return winRate;
	}

	public double getAvgR() {
		return avgR;
	}
}