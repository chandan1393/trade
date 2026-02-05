package com.fyers.fyerstrading.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ema_crossover_signals")
public class EMACrossoverSignal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol; // e.g., "NIFTY50"
    private String signal; // BUY / SELL / NO_SIGNAL
    private LocalDateTime timestamp; // when signal generated

    private Double ema9;
    private Double ema21;
    private Double closePrice;
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
	public String getSignal() {
		return signal;
	}
	public void setSignal(String signal) {
		this.signal = signal;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public Double getEma9() {
		return ema9;
	}
	public void setEma9(Double ema9) {
		this.ema9 = ema9;
	}
	public Double getEma21() {
		return ema21;
	}
	public void setEma21(Double ema21) {
		this.ema21 = ema21;
	}
	public Double getClosePrice() {
		return closePrice;
	}
	public void setClosePrice(Double closePrice) {
		this.closePrice = closePrice;
	}

    
}
