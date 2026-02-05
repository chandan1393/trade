package com.fyers.fyerstrading.model;

import java.util.List;

public class HoldingsResponse {

    private int code;
    private String s;
    private Overall overall;
    private List<Holding> holdings;
    private String message;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	public Overall getOverall() {
		return overall;
	}
	public void setOverall(Overall overall) {
		this.overall = overall;
	}
	public List<Holding> getHoldings() {
		return holdings;
	}
	public void setHoldings(List<Holding> holdings) {
		this.holdings = holdings;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

    
    
    
}

  
