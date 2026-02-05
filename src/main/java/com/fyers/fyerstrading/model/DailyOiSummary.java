package com.fyers.fyerstrading.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyOiSummary {
    public LocalDate date;

    public double support;
    public double resistance;

    public String bias;          // BULLISH / BEARISH / RANGE
    public String biasReason;

    public boolean weeklySupportBroken;
    public boolean weeklyResistanceBroken;

    public List<Double> trappedCalls = new ArrayList<>();
    public List<Double> trappedPuts = new ArrayList<>();

    public String volatility;
    public String volatilityReason;

    public String tradePlan;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
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

	public String getBias() {
		return bias;
	}

	public void setBias(String bias) {
		this.bias = bias;
	}

	public String getBiasReason() {
		return biasReason;
	}

	public void setBiasReason(String biasReason) {
		this.biasReason = biasReason;
	}

	public boolean isWeeklySupportBroken() {
		return weeklySupportBroken;
	}

	public void setWeeklySupportBroken(boolean weeklySupportBroken) {
		this.weeklySupportBroken = weeklySupportBroken;
	}

	public boolean isWeeklyResistanceBroken() {
		return weeklyResistanceBroken;
	}

	public void setWeeklyResistanceBroken(boolean weeklyResistanceBroken) {
		this.weeklyResistanceBroken = weeklyResistanceBroken;
	}

	public List<Double> getTrappedCalls() {
		return trappedCalls;
	}

	public void setTrappedCalls(List<Double> trappedCalls) {
		this.trappedCalls = trappedCalls;
	}

	public List<Double> getTrappedPuts() {
		return trappedPuts;
	}

	public void setTrappedPuts(List<Double> trappedPuts) {
		this.trappedPuts = trappedPuts;
	}

	public String getVolatility() {
		return volatility;
	}

	public void setVolatility(String volatility) {
		this.volatility = volatility;
	}

	public String getVolatilityReason() {
		return volatilityReason;
	}

	public void setVolatilityReason(String volatilityReason) {
		this.volatilityReason = volatilityReason;
	}

	public String getTradePlan() {
		return tradePlan;
	}

	public void setTradePlan(String tradePlan) {
		this.tradePlan = tradePlan;
	}
    
    
    
    
    
}

