package com.fyers.fyerstrading.dto;

import java.time.LocalDate;

public class ExitPlanRequest  {
	private LocalDate expiry;
	private double structureSL;
	private double t1;
	private double t2;
	private double t3;

	public double getStructureSL() {
		return structureSL;
	}

	public void setStructureSL(double structureSL) {
		this.structureSL = structureSL;
	}

	public double getT1() {
		return t1;
	}

	public void setT1(double t1) {
		this.t1 = t1;
	}

	public double getT2() {
		return t2;
	}

	public void setT2(double t2) {
		this.t2 = t2;
	}

	public double getT3() {
		return t3;
	}

	public void setT3(double t3) {
		this.t3 = t3;
	}

	public LocalDate getExpiry() {
		return expiry;
	}

	public void setExpiry(LocalDate expiry) {
		this.expiry = expiry;
	}

	
}
