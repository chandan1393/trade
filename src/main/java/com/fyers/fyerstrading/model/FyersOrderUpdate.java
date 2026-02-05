package com.fyers.fyerstrading.model;

public class FyersOrderUpdate {
	private String orderDateTime;
	private int remainingQuantity;
	private String symbol;
	private int side;
	private String clientId;
	private String description;
	private int instrument;
	private String source;
	private int type;
	private String fyToken;
	private boolean offlineOrder;
	private int segment;
	private int qty;
	private String orderValidity;
	private String ex_sym;
	private int exchange;
	private String id;
	private String orderTag;
	private String productType;
	private int status;

	public String getOrderDateTime() {
		return orderDateTime;
	}

	public void setOrderDateTime(String orderDateTime) {
		this.orderDateTime = orderDateTime;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getInstrument() {
		return instrument;
	}

	public void setInstrument(int instrument) {
		this.instrument = instrument;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFyToken() {
		return fyToken;
	}

	public void setFyToken(String fyToken) {
		this.fyToken = fyToken;
	}

	public boolean isOfflineOrder() {
		return offlineOrder;
	}

	public void setOfflineOrder(boolean offlineOrder) {
		this.offlineOrder = offlineOrder;
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

	public String getOrderValidity() {
		return orderValidity;
	}

	public void setOrderValidity(String orderValidity) {
		this.orderValidity = orderValidity;
	}

	public String getEx_sym() {
		return ex_sym;
	}

	public void setEx_sym(String ex_sym) {
		this.ex_sym = ex_sym;
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

	public String getOrderTag() {
		return orderTag;
	}

	public void setOrderTag(String orderTag) {
		this.orderTag = orderTag;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
