package com.fyers.fyerstrading.model;

import java.time.Instant;
import java.time.LocalDate;

public class StockCandle {
    private Instant timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
    private LocalDate date;

    // Constructor
    public StockCandle(Instant timestamp, double open, double high, double low, double close, double volume,LocalDate date) {
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.date=date;
        
    }

    // Getters & Setters
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
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

	public void setVolume(double volume) {
		this.volume = volume;
	}

	

	

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "StockCandle [timestamp=" + timestamp + ", open=" + open + ", high=" + high + ", low=" + low + ", close="
				+ close + ", volume=" + volume + ", date=" + date + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}

	

	
}
