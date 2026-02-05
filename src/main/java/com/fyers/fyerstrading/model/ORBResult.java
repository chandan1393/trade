package com.fyers.fyerstrading.model;

import java.time.LocalDate;

public class ORBResult {
    public LocalDate date;
    public int trades;
    public int wins;
    public int losses;
    public double totalPnL;
    public double grossProfit;
    public double grossLoss;
    public double maxDrawdown;
    public double winRate;
    public double avgWin;
    public double avgLoss;

    public ORBResult(LocalDate date, int trades, int wins, int losses, double totalPnL,
                     double grossProfit, double grossLoss, double maxDrawdown,
                     double winRate, double avgWin, double avgLoss) {
        this.date = date;
        this.trades = trades;
        this.wins = wins;
        this.losses = losses;
        this.totalPnL = totalPnL;
        this.grossProfit = grossProfit;
        this.grossLoss = grossLoss;
        this.maxDrawdown = maxDrawdown;
        this.winRate = winRate;
        this.avgWin = avgWin;
        this.avgLoss = avgLoss;
    }

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public int getTrades() {
		return trades;
	}

	public void setTrades(int trades) {
		this.trades = trades;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getLosses() {
		return losses;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}

	public double getTotalPnL() {
		return totalPnL;
	}

	public void setTotalPnL(double totalPnL) {
		this.totalPnL = totalPnL;
	}

	public double getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(double grossProfit) {
		this.grossProfit = grossProfit;
	}

	public double getGrossLoss() {
		return grossLoss;
	}

	public void setGrossLoss(double grossLoss) {
		this.grossLoss = grossLoss;
	}

	public double getMaxDrawdown() {
		return maxDrawdown;
	}

	public void setMaxDrawdown(double maxDrawdown) {
		this.maxDrawdown = maxDrawdown;
	}

	public double getWinRate() {
		return winRate;
	}

	public void setWinRate(double winRate) {
		this.winRate = winRate;
	}

	public double getAvgWin() {
		return avgWin;
	}

	public void setAvgWin(double avgWin) {
		this.avgWin = avgWin;
	}

	public double getAvgLoss() {
		return avgLoss;
	}

	public void setAvgLoss(double avgLoss) {
		this.avgLoss = avgLoss;
	}
    
    
    
    
}
