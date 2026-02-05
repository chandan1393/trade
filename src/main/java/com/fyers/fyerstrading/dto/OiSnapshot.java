package com.fyers.fyerstrading.dto;

import java.util.List;

public class OiSnapshot {
    OiSummary summary;
    double support;
    double resistance;
    List<OiStrikeRow> strikes;
	public OiSummary getSummary() {
		return summary;
	}
	public void setSummary(OiSummary summary) {
		this.summary = summary;
	}
	public double getSupport() {
		return support;
	}
	public void setSupport(double support) {
		this.support = support;
	}
	public double getResistance() {
		return resistance;
	}
	public void setResistance(double resistance) {
		this.resistance = resistance;
	}
	public List<OiStrikeRow> getStrikes() {
		return strikes;
	}
	public void setStrikes(List<OiStrikeRow> strikes) {
		this.strikes = strikes;
	}
    
    
    
}

