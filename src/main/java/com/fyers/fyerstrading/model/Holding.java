package com.fyers.fyerstrading.model;

public class Holding {
    private int remainingQuantity;
    private String symbol;
    private int quantity;
    private double costPrice;
    private int qty_t1;
    private double ltp;
    private String fyToken;
    private double marketVal;
    private int remainingPledgeQuantity;
    private int collateralQuantity;
    private String holdingType;
    private int segment;
    private int exchange;
    private int id;
    private double pl;
    private String isin;
	public int getRemainingQuantity() {
		return remainingQuantity;
	}
	public void setRemainingQuantity(int remainingQuantity) {
		this.remainingQuantity = remainingQuantity;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(double costPrice) {
		this.costPrice = costPrice;
	}
	public int getQty_t1() {
		return qty_t1;
	}
	public void setQty_t1(int qty_t1) {
		this.qty_t1 = qty_t1;
	}
	public double getLtp() {
		return ltp;
	}
	public void setLtp(double ltp) {
		this.ltp = ltp;
	}
	public String getFyToken() {
		return fyToken;
	}
	public void setFyToken(String fyToken) {
		this.fyToken = fyToken;
	}
	public double getMarketVal() {
		return marketVal;
	}
	public void setMarketVal(double marketVal) {
		this.marketVal = marketVal;
	}
	public int getRemainingPledgeQuantity() {
		return remainingPledgeQuantity;
	}
	public void setRemainingPledgeQuantity(int remainingPledgeQuantity) {
		this.remainingPledgeQuantity = remainingPledgeQuantity;
	}
	public int getCollateralQuantity() {
		return collateralQuantity;
	}
	public void setCollateralQuantity(int collateralQuantity) {
		this.collateralQuantity = collateralQuantity;
	}
	public String getHoldingType() {
		return holdingType;
	}
	public void setHoldingType(String holdingType) {
		this.holdingType = holdingType;
	}
	public int getSegment() {
		return segment;
	}
	public void setSegment(int segment) {
		this.segment = segment;
	}
	public int getExchange() {
		return exchange;
	}
	public void setExchange(int exchange) {
		this.exchange = exchange;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getPl() {
		return pl;
	}
	public void setPl(double pl) {
		this.pl = pl;
	}
	public String getIsin() {
		return isin;
	}
	public void setIsin(String isin) {
		this.isin = isin;
	}

    
}

