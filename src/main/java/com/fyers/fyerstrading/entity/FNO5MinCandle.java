package com.fyers.fyerstrading.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
    name = "fno_5min_candles",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"symbol", "timestamp"})
    }
)
public class FNO5MinCandle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol; // e.g., NSE:RELIANCE-EQ

    @Column(nullable = false)
    private LocalDateTime timestamp; // candle timestamp

    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;

    // ✅ Optional: Constructors, getters, setters
	public FNO5MinCandle() {
		
	}
	public FNO5MinCandle(String symbol, LocalDateTime timestamp, double open, double high, double low, double close,
			double volume) {
		super();
		this.symbol = symbol;
		this.timestamp = timestamp;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}
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
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(double d) {
		this.volume = d;
	}

    
    
    
    
}
