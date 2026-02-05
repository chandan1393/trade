package com.fyers.fyerstrading.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class UnusualOIEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private StockMaster stock;

    private LocalDateTime timestamp;

    private double strikePrice;

    private double callOiChangePercent;

    private double putOiChangePercent;

    private String direction; // e.g., Bullish, Bearish, Neutral

    private String reason; // Optional: e.g., "Put OI surged 45%"

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StockMaster getStock() {
		return stock;
	}

	public void setStock(StockMaster stock) {
		this.stock = stock;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public double getStrikePrice() {
		return strikePrice;
	}

	public void setStrikePrice(double strikePrice) {
		this.strikePrice = strikePrice;
	}

	public double getCallOiChangePercent() {
		return callOiChangePercent;
	}

	public void setCallOiChangePercent(double callOiChangePercent) {
		this.callOiChangePercent = callOiChangePercent;
	}

	public double getPutOiChangePercent() {
		return putOiChangePercent;
	}

	public void setPutOiChangePercent(double putOiChangePercent) {
		this.putOiChangePercent = putOiChangePercent;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

    
}

