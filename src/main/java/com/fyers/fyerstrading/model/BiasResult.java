package com.fyers.fyerstrading.model;

public class BiasResult {
    public String value;     // BULLISH / BEARISH / RANGE
    public String reason;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
    
    
    
    
}
