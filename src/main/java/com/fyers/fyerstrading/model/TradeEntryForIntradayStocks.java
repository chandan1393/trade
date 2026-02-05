package com.fyers.fyerstrading.model;

import java.time.LocalDateTime;

public class TradeEntryForIntradayStocks {
	
	 private  double entry;
	 private  double sl;
	 private LocalDateTime entryTime;
	 
	public    TradeEntryForIntradayStocks(double entry, double sl, LocalDateTime entryTime) {
	        this.entry = entry; this.sl = sl; this.entryTime = entryTime;
	    }

	public double getEntry() {
		return entry;
	}

	public void setEntry(double entry) {
		this.entry = entry;
	}

	public double getSl() {
		return sl;
	}

	public void setSl(double sl) {
		this.sl = sl;
	}

	public LocalDateTime getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(LocalDateTime entryTime) {
		this.entryTime = entryTime;
	}
	
	
	
	
	
}
