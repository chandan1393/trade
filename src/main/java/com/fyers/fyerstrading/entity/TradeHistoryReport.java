package com.fyers.fyerstrading.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "trade_history_report")
public class TradeHistoryReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stockSymbol;
    private String entryDate;
    private double entryPrice;
    private String exitDate;
    private double exitPrice;
    private String tradeResult;
    private String exitReason;
    private double profitLoss; 
    private double quantity;
    private String indexType;
    private String tradeSetupDate;
    private int tradeRank;
    private boolean isSecondBreakOut;
    
    
    public TradeHistoryReport() {
		super();
		// TODO Auto-generated constructor stub
	}

	// Constructor
	public TradeHistoryReport(String stockSymbol, String entryDate, double entryPrice, String exitDate,
			double exitPrice, String tradeResult, String exitReason, double profitLoss, double quantity,
			String indexType, String tradeSetupDate, int tradeRank,boolean isSecondBreakout) {
		this.stockSymbol = stockSymbol;
		this.entryDate = entryDate;
		this.entryPrice = entryPrice;
		this.exitDate = exitDate;
		this.exitPrice = exitPrice;
		this.tradeResult = tradeResult;
		this.exitReason = exitReason;
		this.profitLoss = profitLoss;
		this.quantity = quantity;
		this.indexType = indexType;
		this.tradeSetupDate = tradeSetupDate;
		this.tradeRank = tradeRank;
		this.isSecondBreakOut=isSecondBreakout;

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

	

	public double getEntryPrice() {
		return entryPrice;
	}

	public void setEntryPrice(double entryPrice) {
		this.entryPrice = entryPrice;
	}

	
	public String getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(String entryDate) {
		this.entryDate = entryDate;
	}

	public String getExitDate() {
		return exitDate;
	}

	public void setExitDate(String exitDate) {
		this.exitDate = exitDate;
	}

	public double getExitPrice() {
		return exitPrice;
	}

	public void setExitPrice(double exitPrice) {
		this.exitPrice = exitPrice;
	}

	public String getTradeResult() {
		return tradeResult;
	}

	public void setTradeResult(String tradeResult) {
		this.tradeResult = tradeResult;
	}

	public String getExitReason() {
		return exitReason;
	}

	public void setExitReason(String exitReason) {
		this.exitReason = exitReason;
	}

	public double getProfitLoss() {
		return profitLoss;
	}

	public void setProfitLoss(double profitLoss) {
		this.profitLoss = profitLoss;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public String getTradeSetupDate() {
		return tradeSetupDate;
	}

	public void setTradeSetupDate(String tradeSetupDate) {
		this.tradeSetupDate = tradeSetupDate;
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

