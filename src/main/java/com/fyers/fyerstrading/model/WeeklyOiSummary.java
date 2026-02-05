package com.fyers.fyerstrading.model;

import java.util.ArrayList;
import java.util.List;

public class WeeklyOiSummary {
    public double support;
    public double resistance;

    public String bias;          // BULLISH, BEARISH, RANGE
    public boolean supportBroken;
    public boolean resistanceBroken;

    public List<Double> trappedCallStrikes = new ArrayList<>();
    public List<Double> trappedPutStrikes = new ArrayList<>();

    public String volatility;    // EXPANDING, CONTRACTING, NORMAL

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

	public String getBias() {
		return bias;
	}

	public void setBias(String bias) {
		this.bias = bias;
	}

	public boolean isSupportBroken() {
		return supportBroken;
	}

	public void setSupportBroken(boolean supportBroken) {
		this.supportBroken = supportBroken;
	}

	public boolean isResistanceBroken() {
		return resistanceBroken;
	}

	public void setResistanceBroken(boolean resistanceBroken) {
		this.resistanceBroken = resistanceBroken;
	}

	public List<Double> getTrappedCallStrikes() {
		return trappedCallStrikes;
	}

	public void setTrappedCallStrikes(List<Double> trappedCallStrikes) {
		this.trappedCallStrikes = trappedCallStrikes;
	}

	public List<Double> getTrappedPutStrikes() {
		return trappedPutStrikes;
	}

	public void setTrappedPutStrikes(List<Double> trappedPutStrikes) {
		this.trappedPutStrikes = trappedPutStrikes;
	}

	public String getVolatility() {
		return volatility;
	}

	public void setVolatility(String volatility) {
		this.volatility = volatility;
	}
    
    
    
    
    
}

