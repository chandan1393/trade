package com.fyers.fyerstrading.model;

import java.time.LocalDateTime;

public class OIInfo {
	private String symbol;
	private Double strikePrice;

	private Integer callOI;
	private Integer putOI;

	private Double callLtp;
	private Double putLtp;

	private double callOiChange; // percent
	private double putOiChange; // percent

	private LocalDateTime timestamp;

	// --- getters / setters ---
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Double getStrikePrice() {
		return strikePrice;
	}

	public void setStrikePrice(Double strikePrice) {
		this.strikePrice = strikePrice;
	}

	public Integer getCallOI() {
		return callOI;
	}

	public void setCallOI(Integer callOI) {
		this.callOI = callOI;
	}

	public Integer getPutOI() {
		return putOI;
	}

	public void setPutOI(Integer putOI) {
		this.putOI = putOI;
	}

	public Double getCallLtp() {
		return callLtp;
	}

	public void setCallLtp(Double callLtp) {
		this.callLtp = callLtp;
	}

	public Double getPutLtp() {
		return putLtp;
	}

	public void setPutLtp(Double putLtp) {
		this.putLtp = putLtp;
	}

	public double getCallOiChange() {
		return callOiChange;
	}

	public void setCallOiChange(double callOiChange) {
		this.callOiChange = callOiChange;
	}

	public double getPutOiChange() {
		return putOiChange;
	}

	public void setPutOiChange(double putOiChange) {
		this.putOiChange = putOiChange;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
