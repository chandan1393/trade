package com.fyers.fyerstrading.model;

import java.time.LocalDate;

public class BacktestResult {

	private String symbol;
    private LocalDate breakoutCandleDate;
    private double breakoutDeliveryPercent;
    private double breakoutVolume;
    private int score;
    private boolean aggressive;

    private LocalDate entryDate;
    private double entry;
    private double stopLoss;
    private double target1;
    private double target2;

    private LocalDate exitDate;
    private double exitPrice;
    private String exitReason;
    private double pnlPercent;
    private double capitalUsed;
    private double quantity;
    private double profitLoss;
    private double quantitySoldAtTarget1;
    private double quantitySoldAtTarget2;
    private double quantityRemaining;
    private double averageExitPrice; // weighted average of all exits


	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public LocalDate getBreakoutCandleDate() {
		return breakoutCandleDate;
	}

	public void setBreakoutCandleDate(LocalDate breakoutCandleDate) {
		this.breakoutCandleDate = breakoutCandleDate;
	}

	public double getBreakoutDeliveryPercent() {
		return breakoutDeliveryPercent;
	}

	public void setBreakoutDeliveryPercent(double breakoutDeliveryPercent) {
		this.breakoutDeliveryPercent = breakoutDeliveryPercent;
	}

	public double getBreakoutVolume() {
		return breakoutVolume;
	}

	public void setBreakoutVolume(double breakoutVolume) {
		this.breakoutVolume = breakoutVolume;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isAggressive() {
		return aggressive;
	}

	public void setAggressive(boolean aggressive) {
		this.aggressive = aggressive;
	}

	public LocalDate getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(LocalDate entryDate) {
		this.entryDate = entryDate;
	}

	public double getEntry() {
		return entry;
	}

	public void setEntry(double entry) {
		this.entry = entry;
	}

	public double getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}

	public double getTarget1() {
		return target1;
	}

	public void setTarget1(double target1) {
		this.target1 = target1;
	}

	public double getTarget2() {
		return target2;
	}

	public void setTarget2(double target2) {
		this.target2 = target2;
	}

	public LocalDate getExitDate() {
		return exitDate;
	}

	public void setExitDate(LocalDate exitDate) {
		this.exitDate = exitDate;
	}

	public double getExitPrice() {
		return exitPrice;
	}

	public void setExitPrice(double exitPrice) {
		this.exitPrice = exitPrice;
	}

	public String getExitReason() {
		return exitReason;
	}

	public void setExitReason(String exitReason) {
		this.exitReason = exitReason;
	}

	public double getPnlPercent() {
		return pnlPercent;
	}

	public void setPnlPercent(double pnlPercent) {
		this.pnlPercent = pnlPercent;
	}

	public double getCapitalUsed() {
		return capitalUsed;
	}

	public void setCapitalUsed(double capitalUsed) {
		this.capitalUsed = capitalUsed;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getProfitLoss() {
		return profitLoss;
	}

	public void setProfitLoss(double profitLoss) {
		this.profitLoss = profitLoss;
	}

	public double getQuantitySoldAtTarget1() {
		return quantitySoldAtTarget1;
	}

	public void setQuantitySoldAtTarget1(double quantitySoldAtTarget1) {
		this.quantitySoldAtTarget1 = quantitySoldAtTarget1;
	}

	public double getQuantitySoldAtTarget2() {
		return quantitySoldAtTarget2;
	}

	public void setQuantitySoldAtTarget2(double quantitySoldAtTarget2) {
		this.quantitySoldAtTarget2 = quantitySoldAtTarget2;
	}

	public double getQuantityRemaining() {
		return quantityRemaining;
	}

	public void setQuantityRemaining(double quantityRemaining) {
		this.quantityRemaining = quantityRemaining;
	}

	public double getAverageExitPrice() {
		return averageExitPrice;
	}

	public void setAverageExitPrice(double averageExitPrice) {
		this.averageExitPrice = averageExitPrice;
	}

	

    
}
