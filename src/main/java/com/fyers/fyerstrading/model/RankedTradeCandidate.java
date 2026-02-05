package com.fyers.fyerstrading.model;

import java.time.LocalDate;

import com.fyers.fyerstrading.entity.StockDailyPrice;




public class RankedTradeCandidate {
    private String symbol;
    private LocalDate tradeDate;
    private double rankScore;
    private StockDailyPrice previous;
    private StockDailyPrice current;
	public RankedTradeCandidate(String symbol, LocalDate tradeDate, double rankScore, StockDailyPrice previous,
			StockDailyPrice current) {
		super();
		this.symbol = symbol;
		this.tradeDate = tradeDate;
		this.rankScore = rankScore;
		this.previous = previous;
		this.current = current;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public LocalDate getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate = tradeDate;
	}
	public double getRankScore() {
		return rankScore;
	}
	public void setRankScore(double rankScore) {
		this.rankScore = rankScore;
	}
	public StockDailyPrice getPrevious() {
		return previous;
	}
	public void setPrevious(StockDailyPrice previous) {
		this.previous = previous;
	}
	public StockDailyPrice getCurrent() {
		return current;
	}
	public void setCurrent(StockDailyPrice current) {
		this.current = current;
	}

    
    
    
    
    
    
    
}

