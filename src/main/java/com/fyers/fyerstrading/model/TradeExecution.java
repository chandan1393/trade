package com.fyers.fyerstrading.model;

import java.time.LocalDate;
import java.time.LocalDate;

public class TradeExecution {
	 private String stockSymbol;
	    private LocalDate entryDate;
	    private Double entryPrice;
	    private Double stopLoss;
	    private Double target1;
	    private LocalDate target1Date;
	    private LocalDate target2Date;
	    private Double target2;
	    private Integer positionSize;
	    private LocalDate exitDate;
	    private Double exitPrice;
	    private String exitReason;
	    private Double profitLoss;
	    private boolean tradeEntered;
	    private boolean partialExit;
	    private LocalDate tradeFoundDate;
	    private Double trailingStopLoss;
	    private Double allocatedCapital;
	    private int tradeRank;
	    private boolean isSecondBreakOut;
	    
	    
	    public TradeExecution() {
			super();
			// TODO Auto-generated constructor stub
		}

		public TradeExecution(String stockSymbol, LocalDate entryDate, Double entryPrice, Double stopLoss, Double target1, 
	    		Double target2,Integer positionSize,boolean tradeExecuted,LocalDate tradeFoundDate) {
	        this.stockSymbol = stockSymbol;
	        this.entryDate = entryDate;
	        this.entryPrice = entryPrice;
	        this.stopLoss = stopLoss;
	        this.target1 = target1;
	        this.target2 = target2;
	        this.positionSize = positionSize;
	        this.tradeEntered = tradeExecuted;
	        this.tradeFoundDate=tradeFoundDate;
	    }

	    public void setExitDetails(LocalDate exitDate, Double exitPrice, String exitReason,Double profitLoss) {
	        this.exitDate = exitDate;
	        this.exitPrice = exitPrice;
	        this.exitReason = exitReason;
	        this.profitLoss=profitLoss;
	    }

		public String getStockSymbol() {
			return stockSymbol;
		}

		public void setStockSymbol(String stockSymbol) {
			this.stockSymbol = stockSymbol;
		}

		
		public Double getEntryPrice() {
			return entryPrice;
		}

		public void setEntryPrice(Double entryPrice) {
			this.entryPrice = entryPrice;
		}

		public Double getStopLoss() {
			return stopLoss;
		}

		public void setStopLoss(Double stopLoss) {
			this.stopLoss = stopLoss;
		}

		public Double getTarget1() {
			return target1;
		}

		public void setTarget1(Double target1) {
			this.target1 = target1;
		}

		public LocalDate getTarget1Date() {
			return target1Date;
		}

		public void setTarget1Date(LocalDate target1Date) {
			this.target1Date = target1Date;
		}

		public LocalDate getTarget2Date() {
			return target2Date;
		}

		public void setTarget2Date(LocalDate target2Date) {
			this.target2Date = target2Date;
		}

		public Double getTarget2() {
			return target2;
		}

		public void setTarget2(Double target2) {
			this.target2 = target2;
		}

		public Integer getPositionSize() {
			return positionSize;
		}

		public void setPositionSize(Integer positionSize) {
			this.positionSize = positionSize;
		}

		public LocalDate getExitDate() {
			return exitDate;
		}

		public void setExitDate(LocalDate exitDate) {
			this.exitDate = exitDate;
		}

		public Double getExitPrice() {
			return exitPrice;
		}

		public void setExitPrice(Double exitPrice) {
			this.exitPrice = exitPrice;
		}

		public String getExitReason() {
			return exitReason;
		}

		public void setExitReason(String exitReason) {
			this.exitReason = exitReason;
		}

		public Double getProfitLoss() {
			return profitLoss;
		}

		public void setProfitLoss(Double profitLoss) {
			this.profitLoss = profitLoss;
		}

		public boolean isTradeEntered() {
			return tradeEntered;
		}

		public void setTradeEntered(boolean tradeEntered) {
			this.tradeEntered = tradeEntered;
		}

		public boolean isPartialExit() {
			return partialExit;
		}

		public void setPartialExit(boolean partialExit) {
			this.partialExit = partialExit;
		}

		public LocalDate getTradeFoundDate() {
			return tradeFoundDate;
		}

		public void setTradeFoundDate(LocalDate tradeFoundDate) {
			this.tradeFoundDate = tradeFoundDate;
		}

		public Double getTrailingStopLoss() {
			return trailingStopLoss;
		}

		public void setTrailingStopLoss(Double trailingStopLoss) {
			this.trailingStopLoss = trailingStopLoss;
		}

		public LocalDate getEntryDate() {
			return entryDate;
		}

		public void setEntryDate(LocalDate entryDate) {
			this.entryDate = entryDate;
		}

		public Double getAllocatedCapital() {
			return allocatedCapital;
		}

		public void setAllocatedCapital(Double allocatedCapital) {
			this.allocatedCapital = allocatedCapital;
		}

		public int getTradeRank() {
			return tradeRank;
		}

		public void setTradeRank(int tradeRank) {
			this.tradeRank = tradeRank;
		}

		public boolean isSecondBreakOut() {
			return isSecondBreakOut;
		}

		public void setSecondBreakOut(boolean isSecondBreakOut) {
			this.isSecondBreakOut = isSecondBreakOut;
		}
	    
	    
	    

}
