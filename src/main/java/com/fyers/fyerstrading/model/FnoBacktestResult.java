package com.fyers.fyerstrading.model;

import java.time.LocalDate;

public class FnoBacktestResult {

	private String symbol;
	private LocalDate setupDate;
	private LocalDate entryDate;
	private LocalDate exitDate;

	private double entryPrice;
	private double exitPrice;

	private String exitReason; // SL, T1, T2, NO_ENTRY
	private double pnlPoints;
	private String noEntryReason;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public LocalDate getSetupDate() {
		return setupDate;
	}
	public void setSetupDate(LocalDate setupDate) {
		this.setupDate = setupDate;
	}
	public LocalDate getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(LocalDate entryDate) {
		this.entryDate = entryDate;
	}
	public LocalDate getExitDate() {
		return exitDate;
	}
	public void setExitDate(LocalDate exitDate) {
		this.exitDate = exitDate;
	}
	public double getEntryPrice() {
		return entryPrice;
	}
	public void setEntryPrice(double entryPrice) {
		this.entryPrice = entryPrice;
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
	public double getPnlPoints() {
		return pnlPoints;
	}
	public void setPnlPoints(double pnlPoints) {
		this.pnlPoints = pnlPoints;
	}
	public String getNoEntryReason() {
		return noEntryReason;
	}
	public void setNoEntryReason(String noEntryReason) {
		this.noEntryReason = noEntryReason;
	}
	
	
	
}
