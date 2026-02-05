package com.fyers.fyerstrading.model;

public class FyersTradeUpdate {
    private String orderDateTime;
    private String symbol;
    private String tradeNumber;
    private int orderType;
    private int side;
    private String clientId;
    private String orderNumber;
    private double tradeValue;
    private String fyToken;
    private int tradedQty;
    private String exchangeOrderNo;
    private int segment;
    private int exchange;
    private double tradePrice;
    private String productType;
	public String getOrderDateTime() {
		return orderDateTime;
	}
	public void setOrderDateTime(String orderDateTime) {
		this.orderDateTime = orderDateTime;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getTradeNumber() {
		return tradeNumber;
	}
	public void setTradeNumber(String tradeNumber) {
		this.tradeNumber = tradeNumber;
	}
	public int getOrderType() {
		return orderType;
	}
	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}
	public int getSide() {
		return side;
	}
	public void setSide(int side) {
		this.side = side;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public double getTradeValue() {
		return tradeValue;
	}
	public void setTradeValue(double tradeValue) {
		this.tradeValue = tradeValue;
	}
	public String getFyToken() {
		return fyToken;
	}
	public void setFyToken(String fyToken) {
		this.fyToken = fyToken;
	}
	public int getTradedQty() {
		return tradedQty;
	}
	public void setTradedQty(int tradedQty) {
		this.tradedQty = tradedQty;
	}
	public String getExchangeOrderNo() {
		return exchangeOrderNo;
	}
	public void setExchangeOrderNo(String exchangeOrderNo) {
		this.exchangeOrderNo = exchangeOrderNo;
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
	public double getTradePrice() {
		return tradePrice;
	}
	public void setTradePrice(double tradePrice) {
		this.tradePrice = tradePrice;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}

    
}

