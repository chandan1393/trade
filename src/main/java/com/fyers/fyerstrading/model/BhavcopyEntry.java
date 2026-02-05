package com.fyers.fyerstrading.model;

import java.time.LocalDate;

public class BhavcopyEntry {
    private String symbol;
    private String series;
    private LocalDate tradeDate;
    private double prevClose;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double lastPrice;
    private double closePrice;
    private double avgPrice;
    private long totalTradedQty;
    private double turnoverInLacs;
    private int numberOfTrades;
    private long deliveryQty;
    private int deliveryPercent;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public LocalDate getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate = tradeDate;
	}
	public double getPrevClose() {
		return prevClose;
	}
	public void setPrevClose(double prevClose) {
		this.prevClose = prevClose;
	}
	public double getOpenPrice() {
		return openPrice;
	}
	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
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
	public double getLastPrice() {
		return lastPrice;
	}
	public void setLastPrice(double lastPrice) {
		this.lastPrice = lastPrice;
	}
	public double getClosePrice() {
		return closePrice;
	}
	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}
	public double getAvgPrice() {
		return avgPrice;
	}
	public void setAvgPrice(double avgPrice) {
		this.avgPrice = avgPrice;
	}
	public long getTotalTradedQty() {
		return totalTradedQty;
	}
	public void setTotalTradedQty(long totalTradedQty) {
		this.totalTradedQty = totalTradedQty;
	}
	public double getTurnoverInLacs() {
		return turnoverInLacs;
	}
	public void setTurnoverInLacs(double turnoverInLacs) {
		this.turnoverInLacs = turnoverInLacs;
	}
	public int getNumberOfTrades() {
		return numberOfTrades;
	}
	public void setNumberOfTrades(int numberOfTrades) {
		this.numberOfTrades = numberOfTrades;
	}
	public long getDeliveryQty() {
		return deliveryQty;
	}
	public void setDeliveryQty(long deliveryQty) {
		this.deliveryQty = deliveryQty;
	}
	public int getDeliveryPercent() {
		return deliveryPercent;
	}
	public void setDeliveryPercent(int deliveryPercent) {
		this.deliveryPercent = deliveryPercent;
	}

    
}

