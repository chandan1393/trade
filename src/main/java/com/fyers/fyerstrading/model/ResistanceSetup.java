package com.fyers.fyerstrading.model;

import java.time.LocalDate;

public class ResistanceSetup {

	private String symbol;
	private double resistance;
	private LocalDate resistanceDate;
	private double lastClose;
	private double diffPercent;
	private boolean nearResistance;
	private String reason;
	private String date; // last candle date string

	// getters / setters
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public double getResistance() {
		return resistance;
	}

	public void setResistance(double resistance) {
		this.resistance = resistance;
	}

	public LocalDate getResistanceDate() {
		return resistanceDate;
	}

	public void setResistanceDate(LocalDate resistanceDate) {
		this.resistanceDate = resistanceDate;
	}

	public double getLastClose() {
		return lastClose;
	}

	public void setLastClose(double lastClose) {
		this.lastClose = lastClose;
	}

	public double getDiffPercent() {
		return diffPercent;
	}

	public void setDiffPercent(double diffPercent) {
		this.diffPercent = diffPercent;
	}

	public boolean isNearResistance() {
		return nearResistance;
	}

	public void setNearResistance(boolean nearResistance) {
		this.nearResistance = nearResistance;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}