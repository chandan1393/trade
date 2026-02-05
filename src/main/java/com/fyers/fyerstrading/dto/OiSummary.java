package com.fyers.fyerstrading.dto;

public class OiSummary {
	double niftyLtp;
	double pcr;
	String bias; // BULLISH / BEARISH / RANGE
	int biasStrength; // %

	public double getNiftyLtp() {
		return niftyLtp;
	}

	public void setNiftyLtp(double niftyLtp) {
		this.niftyLtp = niftyLtp;
	}

	public double getPcr() {
		return pcr;
	}

	public void setPcr(double pcr) {
		this.pcr = pcr;
	}

	public String getBias() {
		return bias;
	}

	public void setBias(String bias) {
		this.bias = bias;
	}

	public int getBiasStrength() {
		return biasStrength;
	}

	public void setBiasStrength(int biasStrength) {
		this.biasStrength = biasStrength;
	}

}
