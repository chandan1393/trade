package com.fyers.fyerstrading.model;

import java.time.LocalDate;

public class BaselineResult {

	private String symbol;
	private LocalDate setupDate;
	private String outcome; // WIN / LOSS / NEUTRAL
	private double rMultiple;

	public BaselineResult(String symbol, LocalDate setupDate, String outcome, double rMultiple) {
		this.symbol = symbol;
		this.setupDate = setupDate;
		this.outcome = outcome;
		this.rMultiple = rMultiple;
	}

	public String getSymbol() {
		return symbol;
	}

	public LocalDate getSetupDate() {
		return setupDate;
	}

	public String getOutcome() {
		return outcome;
	}

	public double getRMultiple() {
		return rMultiple;
	}
}
