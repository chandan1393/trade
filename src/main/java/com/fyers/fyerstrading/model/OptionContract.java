package com.fyers.fyerstrading.model;

import java.time.LocalDate;

public class OptionContract {

    private String optionSymbol;
    private LocalDate expiry;
    private int strike;
    private int qty;
	public String getOptionSymbol() {
		return optionSymbol;
	}
	public void setOptionSymbol(String optionSymbol) {
		this.optionSymbol = optionSymbol;
	}
	public LocalDate getExpiry() {
		return expiry;
	}
	public void setExpiry(LocalDate expiry) {
		this.expiry = expiry;
	}
	public int getStrike() {
		return strike;
	}
	public void setStrike(int strike) {
		this.strike = strike;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	
	

    
}
