package com.fyers.fyerstrading.model;

public class SMCBTradeLevel {
	private SMCBSetup setup;
	private double entry, stopLoss, target, rr;

	public SMCBTradeLevel(SMCBSetup setup, double entry, double stopLoss, double target, double rr) {
		this.setup = setup;
		this.entry = entry;
		this.stopLoss = stopLoss;
		this.target = target;
		this.rr = rr;
	}

	public SMCBSetup getSetup() {
		return setup;
	}

	public double getEntry() {
		return entry;
	}

	public double getStopLoss() {
		return stopLoss;
	}

	public double getTarget() {
		return target;
	}

	public double getRr() {
		return rr;
	}
}