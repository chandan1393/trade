package com.fyers.fyerstrading.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StockQuote {
    
    private long tt;
    private String symbol;
    private double lp;
    private double ch;
    
    @JsonProperty("high_price")  // Map JSON field "high_price" to Java field "highPrice"
    private double highPrice;
    
    private String description;
    private double chp;
    private String fyToken;
    private double spread;
    private long volume;
    
    @JsonProperty("original_name") 
    private String originalName;
    
    private double ask;
    private String exchange;
    
    @JsonProperty("short_name") 
    private String shortName;
    
    private double bid;
    
    @JsonProperty("low_price")  
    private double lowPrice;
    
    @JsonProperty("open_price")  
    private double openPrice;
    
    @JsonProperty("prev_close_price")  
    private double prevClosePrice;
    
    private double atp;
	// ✅ Getters and Setters
	public long getTt() {
		return tt;
	}

	public void setTt(long tt) {
		this.tt = tt;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public double getLp() {
		return lp;
	}

	public void setLp(double lp) {
		this.lp = lp;
	}

	public double getCh() {
		return ch;
	}

	public void setCh(double ch) {
		this.ch = ch;
	}

	public double getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getChp() {
		return chp;
	}

	public void setChp(double chp) {
		this.chp = chp;
	}

	public String getFyToken() {
		return fyToken;
	}

	public void setFyToken(String fyToken) {
		this.fyToken = fyToken;
	}

	public double getSpread() {
		return spread;
	}

	public void setSpread(double spread) {
		this.spread = spread;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public double getAsk() {
		return ask;
	}

	public void setAsk(double ask) {
		this.ask = ask;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public double getBid() {
		return bid;
	}

	public void setBid(double bid) {
		this.bid = bid;
	}

	public double getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(double lowPrice) {
		this.lowPrice = lowPrice;
	}

	public double getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
	}

	public double getPrevClosePrice() {
		return prevClosePrice;
	}

	public void setPrevClosePrice(double prevClosePrice) {
		this.prevClosePrice = prevClosePrice;
	}

	public double getAtp() {
		return atp;
	}

	public void setAtp(double atp) {
		this.atp = atp;
	}
}
