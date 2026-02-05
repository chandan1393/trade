package com.fyers.fyerstrading.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Position {

	private String symbol;
	private int side;

	@JsonProperty("qtyMulti_com")
	private int qtyMultiCom;

	private int netQty;
	private int dayBuyQty;
	private double rbiRefRate;
	private double sellVal;
	private int daySellQty;
	private double sellAvg;
	private int cfBuyQty;
	private double realized_profit;
	private int sellQty;
	private double buyAvg;
	private double netAvg;
	private String fyToken;
	private int cfSellQty;
	private int buyQty;
	private int segment;
	private int qty;
	private double buyVal;
	private int exchange;
	private String id;
	private String productType;


	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
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

	public int getDayBuyQty() {
		return dayBuyQty;
	}

	public void setDayBuyQty(int dayBuyQty) {
		this.dayBuyQty = dayBuyQty;
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

	public int getDaySellQty() {
		return daySellQty;
	}

	public void setDaySellQty(int daySellQty) {
		this.daySellQty = daySellQty;
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

	public double getRealized_profit() {
		return realized_profit;
	}

	public void setRealized_profit(double realized_profit) {
		this.realized_profit = realized_profit;
	}

	public int getSellQty() {
		return sellQty;
	}

	public void setSellQty(int sellQty) {
		this.sellQty = sellQty;
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

	public int getSegment() {
		return segment;
	}

	public void setSegment(int segment) {
		this.segment = segment;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public double getBuyVal() {
		return buyVal;
	}

	public void setBuyVal(double buyVal) {
		this.buyVal = buyVal;
	}

	public int getExchange() {
		return exchange;
	}

	public void setExchange(int exchange) {
		this.exchange = exchange;
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
}
