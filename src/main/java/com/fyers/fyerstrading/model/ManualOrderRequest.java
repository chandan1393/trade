package com.fyers.fyerstrading.model;

public class ManualOrderRequest {
    public String symbol;
    public int qty;
    public double entryPrice;
    public Double stopLoss;
    public Double target;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public double getEntryPrice() {
		return entryPrice;
	}
	public void setEntryPrice(double entryPrice) {
		this.entryPrice = entryPrice;
	}
	public Double getStopLoss() {
		return stopLoss;
	}
	public void setStopLoss(Double stopLoss) {
		this.stopLoss = stopLoss;
	}
	public Double getTarget() {
		return target;
	}
	public void setTarget(Double target) {
		this.target = target;
	}
    
    
    
    
}
