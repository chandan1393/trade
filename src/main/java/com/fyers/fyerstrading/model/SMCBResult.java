package com.fyers.fyerstrading.model;

public class SMCBResult {
	private SMCBSetup setup;
	private double entry, stopLoss, target, rr;
	private boolean hitTarget, hitSL, falseBreakout;
	private int daysHeld;

	public SMCBResult(SMCBSetup setup, double entry, double stopLoss, double target, double rr, boolean hitTarget,
			boolean hitSL, boolean falseBreakout, int daysHeld) {
		this.setup = setup;
		this.entry = entry;
		this.stopLoss = stopLoss;
		this.target = target;
		this.rr = rr;
		this.hitTarget = hitTarget;
		this.hitSL = hitSL;
		this.falseBreakout = falseBreakout;
		this.daysHeld = daysHeld;
	}

	public boolean isHitTarget() {
		return hitTarget;
	}

	public boolean isHitSL() {
		return hitSL;
	}

	public boolean isFalseBreakout() {
		return falseBreakout;
	}

	public int getDaysHeld() {
		return daysHeld;
	}

	public double getRr() {
		return rr;
	}

	public SMCBSetup getSetup() {
		return setup;
	}
}
