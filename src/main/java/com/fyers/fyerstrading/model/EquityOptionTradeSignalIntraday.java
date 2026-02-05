package com.fyers.fyerstrading.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fyers.fyerstrading.entity.StockMaster;

@Entity
@Table
public class EquityOptionTradeSignalIntraday {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private StockMaster stock;
	private String signalType; // "BUY_CE", "BUY_PE", etc.
	private double score;
	private String reason; // E.g. "Put unwinding + CE addition + price breakout"
	private Double entryPrice;
	private Double stopLoss;
	private Double targetPrice;
	private LocalDateTime timestamp;

	
	
	public EquityOptionTradeSignalIntraday() {
		
	}

	// Constructors
	public EquityOptionTradeSignalIntraday(StockMaster stock, String signalType, double score, String reason,Double entryPrice,
			Double stopLoss,Double targetPrice) {
		this.stock = stock;
		this.signalType = signalType;
		this.score = score;
		this.reason = reason;
		this.entryPrice=entryPrice;
		this.stopLoss=stopLoss;
		this.targetPrice=targetPrice;
	}

	// Getters & Setters
	
	
	public StockMaster getStock() {
		return stock;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setStock(StockMaster stock) {
		this.stock = stock;
	}

	public void setSignalType(String signalType) {
		this.signalType = signalType;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSignalType() {
		return signalType;
	}

	public double getScore() {
		return score;
	}

	public String getReason() {
		return reason;
	}

	public Double getEntryPrice() {
		return entryPrice;
	}

	public void setEntryPrice(Double entryPrice) {
		this.entryPrice = entryPrice;
	}

	public Double getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(Double stopLoss) {
		this.stopLoss = stopLoss;
	}

	public Double getTargetPrice() {
		return targetPrice;
	}

	public void setTargetPrice(Double targetPrice) {
		this.targetPrice = targetPrice;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	
	
	
	
}
