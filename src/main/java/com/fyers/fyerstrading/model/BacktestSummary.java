package com.fyers.fyerstrading.model;

public class BacktestSummary {
    private long totalTrades;
    private long wins;
    private long losses;
    private double totalReturn;
    private double avgPnl;
    private double winRate;
    
    
    
    
    
	public BacktestSummary(long totalTrades, long wins, long losses, double totalReturn, double avgPnl,
			double winRate) {
		super();
		this.totalTrades = totalTrades;
		this.wins = wins;
		this.losses = losses;
		this.totalReturn = totalReturn;
		this.avgPnl = avgPnl;
		this.winRate = winRate;
	}
	public long getTotalTrades() {
		return totalTrades;
	}
	public void setTotalTrades(long totalTrades) {
		this.totalTrades = totalTrades;
	}
	public long getWins() {
		return wins;
	}
	public void setWins(long wins) {
		this.wins = wins;
	}
	public long getLosses() {
		return losses;
	}
	public void setLosses(long losses) {
		this.losses = losses;
	}
	public double getTotalReturn() {
		return totalReturn;
	}
	public void setTotalReturn(double totalReturn) {
		this.totalReturn = totalReturn;
	}
	public double getAvgPnl() {
		return avgPnl;
	}
	public void setAvgPnl(double avgPnl) {
		this.avgPnl = avgPnl;
	}
	public double getWinRate() {
		return winRate;
	}
	public void setWinRate(double winRate) {
		this.winRate = winRate;
	}
    
    
    
    
}
