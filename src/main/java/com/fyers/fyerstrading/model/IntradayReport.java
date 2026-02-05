package com.fyers.fyerstrading.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IntradayReport {
    public LocalDate date;
    public double openMove;
    public double maxUp;
    public double maxDown;
    public double closeMove;
    public int bullishSlots;
    public int bearishSlots;
    public int reversals;
    public List<String> timeline = new ArrayList<>();
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public double getOpenMove() {
		return openMove;
	}
	public void setOpenMove(double openMove) {
		this.openMove = openMove;
	}
	public double getMaxUp() {
		return maxUp;
	}
	public void setMaxUp(double maxUp) {
		this.maxUp = maxUp;
	}
	public double getMaxDown() {
		return maxDown;
	}
	public void setMaxDown(double maxDown) {
		this.maxDown = maxDown;
	}
	public double getCloseMove() {
		return closeMove;
	}
	public void setCloseMove(double closeMove) {
		this.closeMove = closeMove;
	}
	public int getBullishSlots() {
		return bullishSlots;
	}
	public void setBullishSlots(int bullishSlots) {
		this.bullishSlots = bullishSlots;
	}
	public int getBearishSlots() {
		return bearishSlots;
	}
	public void setBearishSlots(int bearishSlots) {
		this.bearishSlots = bearishSlots;
	}
	public int getReversals() {
		return reversals;
	}
	public void setReversals(int reversals) {
		this.reversals = reversals;
	}
	public List<String> getTimeline() {
		return timeline;
	}
	public void setTimeline(List<String> timeline) {
		this.timeline = timeline;
	}
    
    
    
    
    
}

