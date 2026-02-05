package com.fyers.fyerstrading.model;

public class Level {
	public double strike;
	public String type; // SUPPORT or RESISTANCE
	public String strength; // WEAK / MODERATE / STRONG
	public double getStrike() {
		return strike;
	}
	public void setStrike(double strike) {
		this.strike = strike;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStrength() {
		return strength;
	}
	public void setStrength(String strength) {
		this.strength = strength;
	}
	
	
	
	
}
