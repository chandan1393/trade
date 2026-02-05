package com.fyers.fyerstrading.model;

import java.time.LocalDateTime;

public class Candle {
    private LocalDateTime startTime;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;

   

    public Candle() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Candle(LocalDateTime startTime, double open, double high, double low, double close, long volume) {
		super();
		this.startTime = startTime;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}

	public void update(double price, long volume) {
        this.high = Math.max(this.high, price);
        this.low = Math.min(this.low, price);
        this.close = price;
        this.volume += volume;
    }

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
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

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

    
}

