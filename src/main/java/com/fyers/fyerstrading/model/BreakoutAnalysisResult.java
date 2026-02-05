package com.fyers.fyerstrading.model;

import java.time.LocalDate;

public class BreakoutAnalysisResult {

	public String symbol;
	public double resistance;
	public LocalDate resistanceDate;;

	public LocalDate firstBreakoutDate;
	public double maxHighTillFirstBreakout;

	public Double pullbackLowAfterFirstBreakout; // new

	public LocalDate secondBreakoutDate;
	public double maxHighAfterSecondBreakout; // new

	public BreakoutAnalysisResult(String symbol, double resistance, LocalDate resistanceDate, LocalDate firstBreakoutDate,
			double maxHighTillFirstBreakout, Double pullbackLowAfterFirstBreakout, LocalDate secondBreakoutDate,
			double maxHighAfterSecondBreakout) {

		this.symbol = symbol;
		this.resistance = resistance;
		this.resistanceDate = resistanceDate;
		this.firstBreakoutDate = firstBreakoutDate;
		this.maxHighTillFirstBreakout = maxHighTillFirstBreakout;
		this.pullbackLowAfterFirstBreakout = pullbackLowAfterFirstBreakout;
		this.secondBreakoutDate = secondBreakoutDate;
		this.maxHighAfterSecondBreakout = maxHighAfterSecondBreakout;
	}
}
