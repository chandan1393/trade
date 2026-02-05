package com.fyers.fyerstrading.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fyers.fyerstrading.enu.Side;

@Entity
@Table(name = "intraday_trade_debug")
public class IntradayTradeDebug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private LocalDate tradeDate;

    @Enumerated(EnumType.STRING)
    private Side side;

    // === Context ===
    private double prevDayHigh;
    private double prevDayLow;
    private double prevDayRangePct;

    private double atr;
    private double atrPct;

    private double vwapAtEntry;
    private double priceAtEntry;

    private double volumeAtEntry;
    private double avgVolume;

    private double slDistance;
    private boolean trailActivated;
    private double maxFavorableMove;

    // === Result ===
    private double pnl;
    private String exitReason;

    @CreationTimestamp
    private LocalDateTime createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public LocalDate getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate = tradeDate;
	}

	public Side getSide() {
		return side;
	}

	public void setSide(Side side) {
		this.side = side;
	}

	public double getPrevDayHigh() {
		return prevDayHigh;
	}

	public void setPrevDayHigh(double prevDayHigh) {
		this.prevDayHigh = prevDayHigh;
	}

	public double getPrevDayLow() {
		return prevDayLow;
	}

	public void setPrevDayLow(double prevDayLow) {
		this.prevDayLow = prevDayLow;
	}

	public double getPrevDayRangePct() {
		return prevDayRangePct;
	}

	public void setPrevDayRangePct(double prevDayRangePct) {
		this.prevDayRangePct = prevDayRangePct;
	}

	public double getAtr() {
		return atr;
	}

	public void setAtr(double atr) {
		this.atr = atr;
	}

	public double getAtrPct() {
		return atrPct;
	}

	public void setAtrPct(double atrPct) {
		this.atrPct = atrPct;
	}

	public double getVwapAtEntry() {
		return vwapAtEntry;
	}

	public void setVwapAtEntry(double vwapAtEntry) {
		this.vwapAtEntry = vwapAtEntry;
	}

	public double getPriceAtEntry() {
		return priceAtEntry;
	}

	public void setPriceAtEntry(double priceAtEntry) {
		this.priceAtEntry = priceAtEntry;
	}

	public double getVolumeAtEntry() {
		return volumeAtEntry;
	}

	public void setVolumeAtEntry(double volumeAtEntry) {
		this.volumeAtEntry = volumeAtEntry;
	}

	public double getAvgVolume() {
		return avgVolume;
	}

	public void setAvgVolume(double avgVolume) {
		this.avgVolume = avgVolume;
	}

	public double getSlDistance() {
		return slDistance;
	}

	public void setSlDistance(double slDistance) {
		this.slDistance = slDistance;
	}

	public boolean isTrailActivated() {
		return trailActivated;
	}

	public void setTrailActivated(boolean trailActivated) {
		this.trailActivated = trailActivated;
	}

	public double getMaxFavorableMove() {
		return maxFavorableMove;
	}

	public void setMaxFavorableMove(double maxFavorableMove) {
		this.maxFavorableMove = maxFavorableMove;
	}

	public double getPnl() {
		return pnl;
	}

	public void setPnl(double pnl) {
		this.pnl = pnl;
	}

	public String getExitReason() {
		return exitReason;
	}

	public void setExitReason(String exitReason) {
		this.exitReason = exitReason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
    
    
}

