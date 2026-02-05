package com.fyers.fyerstrading.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "manual_option_trades")
public class ManualOptionTrade {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String symbol;
	private double buyAvg;
	private int netQty;
	private Double stopLoss;
	private Double target; 
	private boolean open;
	private String fyBuyOrderId;
	private String fyOcoOrderId;
	private LocalDateTime entryTime;
	private double exitPrice;
	private LocalDateTime exitTime;
	private double realizedPnl;
	private LocalDateTime lastUpdated;
	private LocalDateTime lastTslUpdateTime;

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

	public double getBuyAvg() {
		return buyAvg;
	}

	public void setBuyAvg(double buyAvg) {
		this.buyAvg = buyAvg;
	}

	public int getNetQty() {
		return netQty;
	}

	public void setNetQty(int netQty) {
		this.netQty = netQty;
	}

	public Double getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(Double stopLoss) {
		this.stopLoss = stopLoss;
	}

	public Double getTarget() {
		return target;
	}

	public void setTarget(Double target) {
		this.target = target;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getFyBuyOrderId() {
		return fyBuyOrderId;
	}

	public void setFyBuyOrderId(String fyBuyOrderId) {
		this.fyBuyOrderId = fyBuyOrderId;
	}

	public String getFyOcoOrderId() {
		return fyOcoOrderId;
	}

	public void setFyOcoOrderId(String fyOcoOrderId) {
		this.fyOcoOrderId = fyOcoOrderId;
	}

	public LocalDateTime getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(LocalDateTime entryTime) {
		this.entryTime = entryTime;
	}

	public double getExitPrice() {
		return exitPrice;
	}

	public void setExitPrice(double exitPrice) {
		this.exitPrice = exitPrice;
	}

	public LocalDateTime getExitTime() {
		return exitTime;
	}

	public void setExitTime(LocalDateTime exitTime) {
		this.exitTime = exitTime;
	}

	public double getRealizedPnl() {
		return realizedPnl;
	}

	public void setRealizedPnl(double realizedPnl) {
		this.realizedPnl = realizedPnl;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public LocalDateTime getLastTslUpdateTime() {
		return lastTslUpdateTime;
	}

	public void setLastTslUpdateTime(LocalDateTime lastTslUpdateTime) {
		this.lastTslUpdateTime = lastTslUpdateTime;
	}

}
