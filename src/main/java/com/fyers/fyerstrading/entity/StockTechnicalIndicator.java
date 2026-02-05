package com.fyers.fyerstrading.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "stock_technical_indicator", 
       uniqueConstraints = @UniqueConstraint(columnNames = { "daily_price_id" }))
public class StockTechnicalIndicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "daily_price_id", nullable = false, unique = true)
    private StockDailyPrice stockDailyPrice; // ✅ Strongly linked to daily price

    @Column(nullable = false)
    private LocalDate tradeDate;

    private Double ema9;
    private Double ema10;
    private Double ema14;
    private Double ema20;
    private Double ema21;
    private Double ema50;
    private Double ema200;

    private Double rsi;
    private Double atr;
    private Double percentChange;
    private Double avgGain;
    private Double avgLoss;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
		public LocalDate getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate = tradeDate;
	}
	public Double getEma9() {
		return ema9;
	}
	public void setEma9(Double ema9) {
		this.ema9 = ema9;
	}
	public Double getEma10() {
		return ema10;
	}
	public void setEma10(Double ema10) {
		this.ema10 = ema10;
	}
	public Double getEma20() {
		return ema20;
	}
	public void setEma20(Double ema20) {
		this.ema20 = ema20;
	}
	public Double getEma21() {
		return ema21;
	}
	public void setEma21(Double ema21) {
		this.ema21 = ema21;
	}
	public Double getEma50() {
		return ema50;
	}
	public void setEma50(Double ema50) {
		this.ema50 = ema50;
	}
	public Double getEma200() {
		return ema200;
	}
	public void setEma200(Double ema200) {
		this.ema200 = ema200;
	}
	public Double getRsi() {
		return rsi;
	}
	public void setRsi(Double rsi) {
		this.rsi = rsi;
	}
	public Double getAtr() {
		return atr;
	}
	public void setAtr(Double atr) {
		this.atr = atr;
	}
	public Double getPercentChange() {
		return percentChange;
	}
	public void setPercentChange(Double percentChange) {
		this.percentChange = percentChange;
	}
	public Double getAvgGain() {
		return avgGain;
	}
	public void setAvgGain(Double avgGain) {
		this.avgGain = avgGain;
	}
	public Double getAvgLoss() {
		return avgLoss;
	}
	public void setAvgLoss(Double avgLoss) {
		this.avgLoss = avgLoss;
	}
	public Double getEma14() {
		return ema14;
	}
	public void setEma14(Double ema14) {
		this.ema14 = ema14;
	}
	public StockDailyPrice getStockDailyPrice() {
		return stockDailyPrice;
	}
	public void setStockDailyPrice(StockDailyPrice stockDailyPrice) {
		this.stockDailyPrice = stockDailyPrice;
	}
    
    
}
