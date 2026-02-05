package com.fyers.fyerstrading.entity;

import com.fyers.fyerstrading.model.TradeMasterSignal;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TradeSetupHourly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private String entryOrderId;
    private String ocoId;
    private double entryPrice;
    private double stopLoss;
    private double targetPrice;
    private int qty;
    private double atr;
    private boolean active;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private TradeMasterSignal.Direction direction;

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

	public String getEntryOrderId() {
		return entryOrderId;
	}

	public void setEntryOrderId(String entryOrderId) {
		this.entryOrderId = entryOrderId;
	}

	public String getOcoId() {
		return ocoId;
	}

	public void setOcoId(String ocoId) {
		this.ocoId = ocoId;
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

	public double getTargetPrice() {
		return targetPrice;
	}

	public void setTargetPrice(double targetPrice) {
		this.targetPrice = targetPrice;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public double getAtr() {
		return atr;
	}

	public void setAtr(double atr) {
		this.atr = atr;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public TradeMasterSignal.Direction getDirection() {
		return direction;
	}

	public void setDirection(TradeMasterSignal.Direction direction) {
		this.direction = direction;
	}

    
}
