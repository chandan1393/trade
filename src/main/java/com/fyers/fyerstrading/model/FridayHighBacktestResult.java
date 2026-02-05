package com.fyers.fyerstrading.model;

import java.time.LocalDate;

public class FridayHighBacktestResult {
	  private String symbol;
    private LocalDate fridayDate;
    private Double fridayClose;
    private Double nextWeekHigh;
    private Double nextWeekLow;
    private Double nextWeekClose;
    private Double returnPercent;
    private Double targetPrice;
    private Double stopLoss;
    private boolean hitTarget;
    private boolean hitStopLoss;
    
    private LocalDate entryDate;
    private Double entryPrice;
    
	public LocalDate getFridayDate() {
		return fridayDate;
	}
	public void setFridayDate(LocalDate fridayDate) {
		this.fridayDate = fridayDate;
	}
	public Double getFridayClose() {
		return fridayClose;
	}
	public void setFridayClose(Double fridayClose) {
		this.fridayClose = fridayClose;
	}
	public Double getNextWeekHigh() {
		return nextWeekHigh;
	}
	public void setNextWeekHigh(Double nextWeekHigh) {
		this.nextWeekHigh = nextWeekHigh;
	}
	public Double getNextWeekLow() {
		return nextWeekLow;
	}
	public void setNextWeekLow(Double nextWeekLow) {
		this.nextWeekLow = nextWeekLow;
	}
	public Double getNextWeekClose() {
		return nextWeekClose;
	}
	public void setNextWeekClose(Double nextWeekClose) {
		this.nextWeekClose = nextWeekClose;
	}
	public Double getReturnPercent() {
		return returnPercent;
	}
	public void setReturnPercent(Double returnPercent) {
		this.returnPercent = returnPercent;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Double getTargetPrice() {
		return targetPrice;
	}
	public void setTargetPrice(Double targetPrice) {
		this.targetPrice = targetPrice;
	}
	public Double getStopLoss() {
		return stopLoss;
	}
	public void setStopLoss(Double stopLoss) {
		this.stopLoss = stopLoss;
	}
	public boolean isHitTarget() {
		return hitTarget;
	}
	public void setHitTarget(boolean hitTarget) {
		this.hitTarget = hitTarget;
	}
	public boolean isHitStopLoss() {
		return hitStopLoss;
	}
	public void setHitStopLoss(boolean hitStopLoss) {
		this.hitStopLoss = hitStopLoss;
	}
	public LocalDate getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(LocalDate entryDate) {
		this.entryDate = entryDate;
	}
	public Double getEntryPrice() {
		return entryPrice;
	}
	public void setEntryPrice(Double entryPrice) {
		this.entryPrice = entryPrice;
	}
    
    
    
    

}
