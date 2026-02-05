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
@Table(name = "intraday_trade")
public class IntradayTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;

    private LocalDate tradeDate;

    @Enumerated(EnumType.STRING)
    private Side side;

    private double entryPrice;
    private double exitPrice;

    private int quantity;

    private double pnl;

    private String exitReason; // TSL / TIME_EXIT

    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    private double initialSl;
    private double finalSl;

    private boolean live; // PAPER / LIVE

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

	public double getEntryPrice() {
		return entryPrice;
	}

	public void setEntryPrice(double entryPrice) {
		this.entryPrice = entryPrice;
	}

	public double getExitPrice() {
		return exitPrice;
	}

	public void setExitPrice(double exitPrice) {
		this.exitPrice = exitPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
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

	public LocalDateTime getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(LocalDateTime entryTime) {
		this.entryTime = entryTime;
	}

	public LocalDateTime getExitTime() {
		return exitTime;
	}

	public void setExitTime(LocalDateTime exitTime) {
		this.exitTime = exitTime;
	}

	public double getInitialSl() {
		return initialSl;
	}

	public void setInitialSl(double initialSl) {
		this.initialSl = initialSl;
	}

	public double getFinalSl() {
		return finalSl;
	}

	public void setFinalSl(double finalSl) {
		this.finalSl = finalSl;
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
    
    
}
