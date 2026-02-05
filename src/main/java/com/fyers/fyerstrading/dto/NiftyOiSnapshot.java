package com.fyers.fyerstrading.dto;

public class NiftyOiSnapshot {

    public double niftyLtp;

    public long totalCallOi;
    public long totalPutOi;

    public double pcr;

    public String bias;        // BULLISH / BEARISH / RANGE
    public String trend;       // UP / DOWN / SIDEWAYS
	public double getNiftyLtp() {
		return niftyLtp;
	}
	public void setNiftyLtp(double niftyLtp) {
		this.niftyLtp = niftyLtp;
	}
	public long getTotalCallOi() {
		return totalCallOi;
	}
	public void setTotalCallOi(long totalCallOi) {
		this.totalCallOi = totalCallOi;
	}
	public long getTotalPutOi() {
		return totalPutOi;
	}
	public void setTotalPutOi(long totalPutOi) {
		this.totalPutOi = totalPutOi;
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
	public String getTrend() {
		return trend;
	}
	public void setTrend(String trend) {
		this.trend = trend;
	}
    
    
    
    
}

