package com.fyers.fyerstrading.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TradeExecutionHourly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private String entryOrderId;
    private double entryPrice;
    private int qty;
    private String ocoId;
    private LocalDateTime entryTimestamp;
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
	public double getEntryPrice() {
		return entryPrice;
	}
	public void setEntryPrice(double entryPrice) {
		this.entryPrice = entryPrice;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public String getOcoId() {
		return ocoId;
	}
	public void setOcoId(String ocoId) {
		this.ocoId = ocoId;
	}
	public LocalDateTime getEntryTimestamp() {
		return entryTimestamp;
	}
	public void setEntryTimestamp(LocalDateTime entryTimestamp) {
		this.entryTimestamp = entryTimestamp;
	}

    
}
