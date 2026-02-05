package com.fyers.fyerstrading.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetPosition {
    private String symbol;
    private double rbiRefRate;
    private double sellVal;
    private double sellAvg;
    private int cfBuyQty;
    private double buyAvg;
    private double netAvg;
    private int slNo;

    @JsonProperty("unrealized_profit")
    private double unrealizedProfit;

    private int segment;
    private double buyVal;
    private String id;
    private String productType;
    private int side;

    @JsonProperty("qtyMulti_com")
    private int qtyMultiCom;

    private int netQty;
    private String crossCurrency;
    private int dayBuyQty;
    private int daySellQty;
    private double ltp;

    @JsonProperty("realized_profit")
    private double realizedProfit;

    private int sellQty;
    private String fyToken;
    private int cfSellQty;
    private int buyQty;
    private int qty;
    private int exchange;
    private double pl;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public double getRbiRefRate() {
		return rbiRefRate;
	}
	public void setRbiRefRate(double rbiRefRate) {
		this.rbiRefRate = rbiRefRate;
	}
	public double getSellVal() {
		return sellVal;
	}
	public void setSellVal(double sellVal) {
		this.sellVal = sellVal;
	}
	public double getSellAvg() {
		return sellAvg;
	}
	public void setSellAvg(double sellAvg) {
		this.sellAvg = sellAvg;
	}
	public int getCfBuyQty() {
		return cfBuyQty;
	}
	public void setCfBuyQty(int cfBuyQty) {
		this.cfBuyQty = cfBuyQty;
	}
	public double getBuyAvg() {
		return buyAvg;
	}
	public void setBuyAvg(double buyAvg) {
		this.buyAvg = buyAvg;
	}
	public double getNetAvg() {
		return netAvg;
	}
	public void setNetAvg(double netAvg) {
		this.netAvg = netAvg;
	}
	public int getSlNo() {
		return slNo;
	}
	public void setSlNo(int slNo) {
		this.slNo = slNo;
	}
	public double getUnrealizedProfit() {
		return unrealizedProfit;
	}
	public void setUnrealizedProfit(double unrealizedProfit) {
		this.unrealizedProfit = unrealizedProfit;
	}
	public int getSegment() {
		return segment;
	}
	public void setSegment(int segment) {
		this.segment = segment;
	}
	public double getBuyVal() {
		return buyVal;
	}
	public void setBuyVal(double buyVal) {
		this.buyVal = buyVal;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public int getSide() {
		return side;
	}
	public void setSide(int side) {
		this.side = side;
	}
	public int getQtyMultiCom() {
		return qtyMultiCom;
	}
	public void setQtyMultiCom(int qtyMultiCom) {
		this.qtyMultiCom = qtyMultiCom;
	}
	public int getNetQty() {
		return netQty;
	}
	public void setNetQty(int netQty) {
		this.netQty = netQty;
	}
	public String getCrossCurrency() {
		return crossCurrency;
	}
	public void setCrossCurrency(String crossCurrency) {
		this.crossCurrency = crossCurrency;
	}
	public int getDayBuyQty() {
		return dayBuyQty;
	}
	public void setDayBuyQty(int dayBuyQty) {
		this.dayBuyQty = dayBuyQty;
	}
	public int getDaySellQty() {
		return daySellQty;
	}
	public void setDaySellQty(int daySellQty) {
		this.daySellQty = daySellQty;
	}
	public double getLtp() {
		return ltp;
	}
	public void setLtp(double ltp) {
		this.ltp = ltp;
	}
	public double getRealizedProfit() {
		return realizedProfit;
	}
	public void setRealizedProfit(double realizedProfit) {
		this.realizedProfit = realizedProfit;
	}
	public int getSellQty() {
		return sellQty;
	}
	public void setSellQty(int sellQty) {
		this.sellQty = sellQty;
	}
	public String getFyToken() {
		return fyToken;
	}
	public void setFyToken(String fyToken) {
		this.fyToken = fyToken;
	}
	public int getCfSellQty() {
		return cfSellQty;
	}
	public void setCfSellQty(int cfSellQty) {
		this.cfSellQty = cfSellQty;
	}
	public int getBuyQty() {
		return buyQty;
	}
	public void setBuyQty(int buyQty) {
		this.buyQty = buyQty;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public int getExchange() {
		return exchange;
	}
	public void setExchange(int exchange) {
		this.exchange = exchange;
	}
	public double getPl() {
		return pl;
	}
	public void setPl(double pl) {
		this.pl = pl;
	}

    
}

