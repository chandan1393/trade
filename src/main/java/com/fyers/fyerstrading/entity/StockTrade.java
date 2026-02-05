package com.fyers.fyerstrading.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "stock_trades")
public class StockTrade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String stockSymbol;
    private LocalDateTime entryDate;
    private double entryPrice;
    private double exitPrice;
    private double stopLoss;
    private String remark; // "Entry", "Stop-Loss Hit", "Target Hit", "Trailing Stop"
    
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    private LocalDateTime exitDate;
    private String result; // "Win" or "Loss"
    private double ema9;
    private double ema21;
    private double rsi;
    private double atr;
    private boolean status;
    private String exitRemark;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getStockSymbol() {
		return stockSymbol;
	}
	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public double getEma9() {
		return ema9;
	}
	public void setEma9(double ema9) {
		this.ema9 = ema9;
	}
	public double getEma21() {
		return ema21;
	}
	public void setEma21(double ema21) {
		this.ema21 = ema21;
	}
	public double getAtr() {
		return atr;
	}
	public void setAtr(double atr) {
		this.atr = atr;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getExitRemark() {
		return exitRemark;
	}
	public void setExitRemark(String exitRemark) {
		this.exitRemark = exitRemark;
	}
	public double getRsi() {
		return rsi;
	}
	public void setRsi(double rsi) {
		this.rsi = rsi;
	}
	public double getStopLoss() {
		return stopLoss;
	}
	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}
	public LocalDateTime getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(LocalDateTime entryDate) {
		this.entryDate = entryDate;
	}
	public LocalDateTime getExitDate() {
		return exitDate;
	}
	public void setExitDate(LocalDateTime exitDate) {
		this.exitDate = exitDate;
	}
	
	
    
    
}