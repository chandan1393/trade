package com.fyers.fyerstrading.model;

public class OptionData {
    private double strikePrice;
    private String optionType; // "CE" or "PE"
    private long oi;
    private long prevOi;
    private double ltp;
    private double priceChange;
	public double getStrikePrice() {
		return strikePrice;
	}
	public void setStrikePrice(double strikePrice) {
		this.strikePrice = strikePrice;
	}
	public String getOptionType() {
		return optionType;
	}
	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}
	public long getOi() {
		return oi;
	}
	public void setOi(long oi) {
		this.oi = oi;
	}
	public long getPrevOi() {
		return prevOi;
	}
	public void setPrevOi(long prevOi) {
		this.prevOi = prevOi;
	}
	public double getLtp() {
		return ltp;
	}
	public void setLtp(double ltp) {
		this.ltp = ltp;
	}
	public double getPriceChange() {
		return priceChange;
	}
	public void setPriceChange(double priceChange) {
		this.priceChange = priceChange;
	}

    
}

