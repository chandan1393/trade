package com.fyers.fyerstrading.dto;

public class NiftyOiStrikeRow {

    public double strike;

    public long callOi;
    public long putOi;

    public long callChgOi;
    public long putChgOi;

    public String zone;        // SUPPORT / RESISTANCE / NEUTRAL
    public String trap;        // CALL_TRAP / PUT_TRAP / NONE
	public double getStrike() {
		return strike;
	}
	public void setStrike(double strike) {
		this.strike = strike;
	}
	public long getCallOi() {
		return callOi;
	}
	public void setCallOi(long callOi) {
		this.callOi = callOi;
	}
	public long getPutOi() {
		return putOi;
	}
	public void setPutOi(long putOi) {
		this.putOi = putOi;
	}
	public long getCallChgOi() {
		return callChgOi;
	}
	public void setCallChgOi(long callChgOi) {
		this.callChgOi = callChgOi;
	}
	public long getPutChgOi() {
		return putChgOi;
	}
	public void setPutChgOi(long putChgOi) {
		this.putChgOi = putChgOi;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getTrap() {
		return trap;
	}
	public void setTrap(String trap) {
		this.trap = trap;
	}
    
    
    
}

