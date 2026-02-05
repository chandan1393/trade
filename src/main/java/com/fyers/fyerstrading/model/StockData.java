package com.fyers.fyerstrading.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true) // ✅ Ignore any extra fields in JSON
public class StockData {
    private String fyToken;
    private Integer exToken;
    private String exSymbol;
    private String exSymName;
    private Integer exchange;
    private Integer segment;
    private String exSeries;
    private Integer exInstType;
    private Integer tradeStatus;
    private String underSym;
    private String underFyTok;
    private String expiryDate;
    private String optType;
    private Double strikePrice;
    private Integer minLotSize;
    private Double tickSize;
    private String isin;
    private String symDetails;
    private Double upperPrice;
    private Double lowerPrice;
    private Integer faceValue;
    private String qtyFreeze;
    private String lastUpdate;
    private String tradingSession;
    private String currencyCode;
    private String symTicker;
    private String exchangeName;
    private String symbolDesc;
    private Integer qtyMultiplier;
    private String originalExpDate;
    private Integer previousOi;
    private Double previousClose;
    private Integer isMtfTradable;
    private Integer mtfMargin;
    private String asmGsmVal;
    private String stream;
    private String cautionaryMsg;
    private String symbolDetails;
    private Integer mppFlag;
	public String getFyToken() {
		return fyToken;
	}
	public void setFyToken(String fyToken) {
		this.fyToken = fyToken;
	}
	public Integer getExToken() {
		return exToken;
	}
	public void setExToken(Integer exToken) {
		this.exToken = exToken;
	}
	public String getExSymbol() {
		return exSymbol;
	}
	public void setExSymbol(String exSymbol) {
		this.exSymbol = exSymbol;
	}
	public String getExSymName() {
		return exSymName;
	}
	public void setExSymName(String exSymName) {
		this.exSymName = exSymName;
	}
	public Integer getExchange() {
		return exchange;
	}
	public void setExchange(Integer exchange) {
		this.exchange = exchange;
	}
	public Integer getSegment() {
		return segment;
	}
	public void setSegment(Integer segment) {
		this.segment = segment;
	}
	public String getExSeries() {
		return exSeries;
	}
	public void setExSeries(String exSeries) {
		this.exSeries = exSeries;
	}
	public Integer getExInstType() {
		return exInstType;
	}
	public void setExInstType(Integer exInstType) {
		this.exInstType = exInstType;
	}
	public Integer getTradeStatus() {
		return tradeStatus;
	}
	public void setTradeStatus(Integer tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	public String getUnderSym() {
		return underSym;
	}
	public void setUnderSym(String underSym) {
		this.underSym = underSym;
	}
	public String getUnderFyTok() {
		return underFyTok;
	}
	public void setUnderFyTok(String underFyTok) {
		this.underFyTok = underFyTok;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getOptType() {
		return optType;
	}
	public void setOptType(String optType) {
		this.optType = optType;
	}
	public Double getStrikePrice() {
		return strikePrice;
	}
	public void setStrikePrice(Double strikePrice) {
		this.strikePrice = strikePrice;
	}
	public Integer getMinLotSize() {
		return minLotSize;
	}
	public void setMinLotSize(Integer minLotSize) {
		this.minLotSize = minLotSize;
	}
	public Double getTickSize() {
		return tickSize;
	}
	public void setTickSize(Double tickSize) {
		this.tickSize = tickSize;
	}
	public String getIsin() {
		return isin;
	}
	public void setIsin(String isin) {
		this.isin = isin;
	}
	public String getSymDetails() {
		return symDetails;
	}
	public void setSymDetails(String symDetails) {
		this.symDetails = symDetails;
	}
	public Double getUpperPrice() {
		return upperPrice;
	}
	public void setUpperPrice(Double upperPrice) {
		this.upperPrice = upperPrice;
	}
	public Double getLowerPrice() {
		return lowerPrice;
	}
	public void setLowerPrice(Double lowerPrice) {
		this.lowerPrice = lowerPrice;
	}
	public Integer getFaceValue() {
		return faceValue;
	}
	public void setFaceValue(Integer faceValue) {
		this.faceValue = faceValue;
	}
	public String getQtyFreeze() {
		return qtyFreeze;
	}
	public void setQtyFreeze(String qtyFreeze) {
		this.qtyFreeze = qtyFreeze;
	}
	public String getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public String getTradingSession() {
		return tradingSession;
	}
	public void setTradingSession(String tradingSession) {
		this.tradingSession = tradingSession;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getSymTicker() {
		return symTicker;
	}
	public void setSymTicker(String symTicker) {
		this.symTicker = symTicker;
	}
	public String getExchangeName() {
		return exchangeName;
	}
	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
	public String getSymbolDesc() {
		return symbolDesc;
	}
	public void setSymbolDesc(String symbolDesc) {
		this.symbolDesc = symbolDesc;
	}
	public Integer getQtyMultiplier() {
		return qtyMultiplier;
	}
	public void setQtyMultiplier(Integer qtyMultiplier) {
		this.qtyMultiplier = qtyMultiplier;
	}
	public String getOriginalExpDate() {
		return originalExpDate;
	}
	public void setOriginalExpDate(String originalExpDate) {
		this.originalExpDate = originalExpDate;
	}
	public Integer getPreviousOi() {
		return previousOi;
	}
	public void setPreviousOi(Integer previousOi) {
		this.previousOi = previousOi;
	}
	public Double getPreviousClose() {
		return previousClose;
	}
	public void setPreviousClose(Double previousClose) {
		this.previousClose = previousClose;
	}
	public Integer getIsMtfTradable() {
		return isMtfTradable;
	}
	public void setIsMtfTradable(Integer isMtfTradable) {
		this.isMtfTradable = isMtfTradable;
	}
	public Integer getMtfMargin() {
		return mtfMargin;
	}
	public void setMtfMargin(Integer mtfMargin) {
		this.mtfMargin = mtfMargin;
	}
	public String getAsmGsmVal() {
		return asmGsmVal;
	}
	public void setAsmGsmVal(String asmGsmVal) {
		this.asmGsmVal = asmGsmVal;
	}
	public String getStream() {
		return stream;
	}
	public void setStream(String stream) {
		this.stream = stream;
	}
	public String getCautionaryMsg() {
		return cautionaryMsg;
	}
	public void setCautionaryMsg(String cautionaryMsg) {
		this.cautionaryMsg = cautionaryMsg;
	}
	public String getSymbolDetails() {
		return symbolDetails;
	}
	public void setSymbolDetails(String symbolDetails) {
		this.symbolDetails = symbolDetails;
	}
	public Integer getMppFlag() {
		return mppFlag;
	}
	public void setMppFlag(Integer mppFlag) {
		this.mppFlag = mppFlag;
	}
	
    
    
    
    
}
