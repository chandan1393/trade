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
public class IndexOptionTradeSignalIntraday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private StockMaster stock;
    private String direction;      // BUY_CE / BUY_PE
    private double strikePrice;
    private double entryPrice;
    private double stopLoss;
    private double target;
    private double tsl;
    private double score;
    private String reason;
    private LocalDateTime timestamp;

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

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public double getStrikePrice() {
		return strikePrice;
	}

	public void setStrikePrice(double strikePrice) {
		this.strikePrice = strikePrice;
	}

	public double getEntryPrice() {
		return entryPrice;
	}

	public void setEntryPrice(double entryPrice) {
		this.entryPrice = entryPrice;
	}

	public double getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}

	public double getTarget() {
		return target;
	}

	public void setTarget(double target) {
		this.target = target;
	}

	public double getTsl() {
		return tsl;
	}

	public void setTsl(double tsl) {
		this.tsl = tsl;
	}

	

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}


}