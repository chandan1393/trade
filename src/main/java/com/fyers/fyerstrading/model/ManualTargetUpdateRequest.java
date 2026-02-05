package com.fyers.fyerstrading.model;

public class ManualTargetUpdateRequest {

	private String symbol;
	private Double sl;
	private Double t1;
	private Double t2;
	private Double t3;

	// Getters & Setters
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Double getSl() {
		return sl;
	}

	public void setSl(Double sl) {
		this.sl = sl;
	}

	public Double getT1() {
		return t1;
	}

	public void setT1(Double t1) {
		this.t1 = t1;
	}

	public Double getT2() {
		return t2;
	}

	public void setT2(Double t2) {
		this.t2 = t2;
	}

	public Double getT3() {
		return t3;
	}

	public void setT3(Double t3) {
		this.t3 = t3;
	}

}
