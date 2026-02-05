package com.fyers.fyerstrading.model;

import java.time.LocalDateTime;

public class TradeOutcome {
	private double pnl;
	private double trailDistance;
	private boolean partialBooked;
	private int qty;
	private double exitPrice;
	private LocalDateTime exitTime;

	public TradeOutcome(double pnl, boolean partialBooked, int qty, double exitPrice, LocalDateTime exitTime, double trailDistance) {
        this.pnl = pnl; this.partialBooked = partialBooked;
        this.qty = qty; this.exitPrice = exitPrice; this.exitTime = exitTime;
        this.trailDistance = trailDistance;
    }

	public double getPnl() {
		return pnl;
	}

	public void setPnl(double pnl) {
		this.pnl = pnl;
	}

	public double getTrailDistance() {
		return trailDistance;
	}

	public void setTrailDistance(double trailDistance) {
		this.trailDistance = trailDistance;
	}

	public boolean isPartialBooked() {
		return partialBooked;
	}

	public void setPartialBooked(boolean partialBooked) {
		this.partialBooked = partialBooked;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public double getExitPrice() {
		return exitPrice;
	}

	public void setExitPrice(double exitPrice) {
		this.exitPrice = exitPrice;
	}

	public LocalDateTime getExitTime() {
		return exitTime;
	}

	public void setExitTime(LocalDateTime exitTime) {
		this.exitTime = exitTime;
	}
	
	
	
	

	
}