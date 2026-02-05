package com.fyers.fyerstrading.model;

import java.time.LocalDateTime;

public  class Totals {
    public long totalCall;
    public long totalPut;
    public double pcr;
    public LocalDateTime latestTs;
	public long getTotalCall() {
		return totalCall;
	}
	public void setTotalCall(long totalCall) {
		this.totalCall = totalCall;
	}
	public long getTotalPut() {
		return totalPut;
	}
	public void setTotalPut(long totalPut) {
		this.totalPut = totalPut;
	}
	public double getPcr() {
		return pcr;
	}
	public void setPcr(double pcr) {
		this.pcr = pcr;
	}
	public LocalDateTime getLatestTs() {
		return latestTs;
	}
	public void setLatestTs(LocalDateTime latestTs) {
		this.latestTs = latestTs;
	}
    
    
    
    
}
