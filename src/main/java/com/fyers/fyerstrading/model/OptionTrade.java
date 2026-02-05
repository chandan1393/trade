package com.fyers.fyerstrading.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fyers.fyerstrading.entity.IndexOptionTradeLeg;
import com.fyers.fyerstrading.enu.InstrumentType;

@Entity
@Table(name = "option_trade")
public class OptionTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeId;

    // ===== Broker / Instrument Identity =====
    @Column(nullable = false)
    private String symbol;                 // NSE:COAL27JAN240CE

    @Column(nullable = false)
    private String side;                   // BUY / SELL

    @Column(nullable = false)
    private String underlying;             // NIFTY, COAL, ITC

    private LocalDate expiry;

    @Enumerated(EnumType.STRING)
    private InstrumentType instrumentType; // STOCK_OPTION / INDEX_OPTION

    // ===== Quantity =====
    private int totalQuantity;             // current qty from positions
    private int lotSize;                   // contract lot size

    // ===== Pricing =====
    private double entryPrice;             // avg buy price from broker
    private double structureSL;            // initial SL
    private double t1;
    private double t2;
    private double t3;

    // ===== State =====
    private String status;                 // OPEN / CLOSED / PARTIAL

    private boolean legsCreated;           // safety flag

    // ===== P&L Tracking =====
    private double realizedPnl;
    private double unrealizedPnl;

    // ===== Audit =====
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    
    @Transient
    private List<StockOptionTradeLeg> stockLegs;
    
    @Transient
    private List<IndexOptionTradeLeg> indexLegs;


	public Long getTradeId() {
		return tradeId;
	}
	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	public int getLotSize() {
		return lotSize;
	}
	public void setLotSize(int lotSize) {
		this.lotSize = lotSize;
	}
	public double getEntryPrice() {
		return entryPrice;
	}
	public void setEntryPrice(double entryPrice) {
		this.entryPrice = entryPrice;
	}
	public double getStructureSL() {
		return structureSL;
	}
	public void setStructureSL(double structureSL) {
		this.structureSL = structureSL;
	}
	public double getT1() {
		return t1;
	}
	public void setT1(double t1) {
		this.t1 = t1;
	}
	public double getT2() {
		return t2;
	}
	public void setT2(double t2) {
		this.t2 = t2;
	}
	public double getT3() {
		return t3;
	}
	public void setT3(double t3) {
		this.t3 = t3;
	}
	public LocalDate getExpiry() {
		return expiry;
	}
	public void setExpiry(LocalDate expiry) {
		this.expiry = expiry;
	}
	public InstrumentType getInstrumentType() {
		return instrumentType;
	}
	public void setInstrumentType(InstrumentType instrumentType) {
		this.instrumentType = instrumentType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public String getUnderlying() {
		return underlying;
	}
	public void setUnderlying(String underlying) {
		this.underlying = underlying;
	}
	
	public boolean isLegsCreated() {
		return legsCreated;
	}
	public void setLegsCreated(boolean legsCreated) {
		this.legsCreated = legsCreated;
	}
	public double getRealizedPnl() {
		return realizedPnl;
	}
	public void setRealizedPnl(double realizedPnl) {
		this.realizedPnl = realizedPnl;
	}
	public double getUnrealizedPnl() {
		return unrealizedPnl;
	}
	public void setUnrealizedPnl(double unrealizedPnl) {
		this.unrealizedPnl = unrealizedPnl;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getClosedAt() {
		return closedAt;
	}
	public void setClosedAt(LocalDateTime closedAt) {
		this.closedAt = closedAt;
	}
	public List<StockOptionTradeLeg> getStockLegs() {
		return stockLegs;
	}
	public void setStockLegs(List<StockOptionTradeLeg> stockLegs) {
		this.stockLegs = stockLegs;
	}
	public List<IndexOptionTradeLeg> getIndexLegs() {
		return indexLegs;
	}
	public void setIndexLegs(List<IndexOptionTradeLeg> indexLegs) {
		this.indexLegs = indexLegs;
	}
	
	
	
	
	
	
}
