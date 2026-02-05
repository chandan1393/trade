package com.fyers.fyerstrading.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    @JsonProperty("clientId")
    private String clientId;

    @JsonProperty("exchange")
    private int exchange;

    @JsonProperty("fy_token")
    private String fyToken;

    @JsonProperty("remainingQuantity")
    private int remainingQuantity;
    
    
    @JsonProperty("id_fyers")
    private String idFyers;

    @JsonProperty("id")
    private String id;

    @JsonProperty("instrument")
    private int instrument;

    @JsonProperty("lot_size")
    private int lotSize;

    @JsonProperty("multiplier")
    private int multiplier;

    @JsonProperty("ord_status")
    private int ordStatus;

    @JsonProperty("precision")
    private int precision;

    @JsonProperty("price_limit")
    private double priceLimit;

    @JsonProperty("price2_limit")
    private double price2Limit;

    @JsonProperty("price_trigger")
    private double priceTrigger;

    @JsonProperty("price2_trigger")
    private double price2Trigger;

    @JsonProperty("product_type")
    private String productType;

    @JsonProperty("qty")
    private int qty;

    @JsonProperty("qty2")
    private int qty2;

    @JsonProperty("report_type")
    private String reportType;

    @JsonProperty("segment")
    private int segment;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("symbol_desc")
    private String symbolDesc;

    @JsonProperty("symbol_exch")
    private String symbolExch;

    @JsonProperty("tick_size")
    private double tickSize;

    @JsonProperty("tran_side")
    private int tranSide;

    @JsonProperty("gtt_oco_ind")
    private int gttOcoInd;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("create_time_epoch")
    private long createTimeEpoch;

    @JsonProperty("oms_msg")
    private String omsMsg;

    @JsonProperty("ltp_ch")
    private double ltpCh;

    @JsonProperty("ltp_chp")
    private double ltpChp;

    @JsonProperty("ltp")
    private double ltp;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public int getExchange() {
		return exchange;
	}

	public void setExchange(int exchange) {
		this.exchange = exchange;
	}

	public String getFyToken() {
		return fyToken;
	}

	public void setFyToken(String fyToken) {
		this.fyToken = fyToken;
	}

	public String getIdFyers() {
		return idFyers;
	}

	public void setIdFyers(String idFyers) {
		this.idFyers = idFyers;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getInstrument() {
		return instrument;
	}

	public void setInstrument(int instrument) {
		this.instrument = instrument;
	}

	public int getLotSize() {
		return lotSize;
	}

	public void setLotSize(int lotSize) {
		this.lotSize = lotSize;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}

	public int getOrdStatus() {
		return ordStatus;
	}

	public void setOrdStatus(int ordStatus) {
		this.ordStatus = ordStatus;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public double getPriceLimit() {
		return priceLimit;
	}

	public void setPriceLimit(double priceLimit) {
		this.priceLimit = priceLimit;
	}

	public double getPrice2Limit() {
		return price2Limit;
	}

	public void setPrice2Limit(double price2Limit) {
		this.price2Limit = price2Limit;
	}

	public double getPriceTrigger() {
		return priceTrigger;
	}

	public void setPriceTrigger(double priceTrigger) {
		this.priceTrigger = priceTrigger;
	}

	public double getPrice2Trigger() {
		return price2Trigger;
	}

	public void setPrice2Trigger(double price2Trigger) {
		this.price2Trigger = price2Trigger;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public int getQty2() {
		return qty2;
	}

	public void setQty2(int qty2) {
		this.qty2 = qty2;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public int getSegment() {
		return segment;
	}

	public void setSegment(int segment) {
		this.segment = segment;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbolDesc() {
		return symbolDesc;
	}

	public void setSymbolDesc(String symbolDesc) {
		this.symbolDesc = symbolDesc;
	}

	public String getSymbolExch() {
		return symbolExch;
	}

	public void setSymbolExch(String symbolExch) {
		this.symbolExch = symbolExch;
	}

	public double getTickSize() {
		return tickSize;
	}

	public void setTickSize(double tickSize) {
		this.tickSize = tickSize;
	}

	public int getTranSide() {
		return tranSide;
	}

	public void setTranSide(int tranSide) {
		this.tranSide = tranSide;
	}

	public int getGttOcoInd() {
		return gttOcoInd;
	}

	public void setGttOcoInd(int gttOcoInd) {
		this.gttOcoInd = gttOcoInd;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public long getCreateTimeEpoch() {
		return createTimeEpoch;
	}

	public void setCreateTimeEpoch(long createTimeEpoch) {
		this.createTimeEpoch = createTimeEpoch;
	}

	public String getOmsMsg() {
		return omsMsg;
	}

	public void setOmsMsg(String omsMsg) {
		this.omsMsg = omsMsg;
	}

	public double getLtpCh() {
		return ltpCh;
	}

	public void setLtpCh(double ltpCh) {
		this.ltpCh = ltpCh;
	}

	public double getLtpChp() {
		return ltpChp;
	}

	public void setLtpChp(double ltpChp) {
		this.ltpChp = ltpChp;
	}

	public double getLtp() {
		return ltp;
	}

	public void setLtp(double ltp) {
		this.ltp = ltp;
	}

	public int getRemainingQuantity() {
		return remainingQuantity;
	}

	public void setRemainingQuantity(int remainingQuantity) {
		this.remainingQuantity = remainingQuantity;
	}

   
}
