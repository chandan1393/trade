package com.fyers.fyerstrading.model;

import java.time.LocalDate;

public class BacktestResultForResistenceBreakout {

	private String symbol;

	private LocalDate entryDate;
	private LocalDate exitDate;

	private double entryPrice;
	private double exitPrice;

	private double pnl;

	public BacktestResultForResistenceBreakout() {
	}

	public BacktestResultForResistenceBreakout(String symbol, LocalDate entryDate, LocalDate exitDate, double pnl) {
		this.symbol = symbol;
		this.entryDate = entryDate;
		this.exitDate = exitDate;
		this.pnl = pnl;
	}

	// Getters & Setters
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
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

	public double getPnl() {
		return pnl;
	}

	public void setPnl(double pnl) {
		this.pnl = pnl;
	}

}
