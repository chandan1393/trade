package com.fyers.fyerstrading.model;

public class OverallPnL {
    private int count_open;
    private int count_total;
    private double pl_realized;
    private double pl_total;
    private double pl_unrealized;
	public int getCount_open() {
		return count_open;
	}
	public void setCount_open(int count_open) {
		this.count_open = count_open;
	}
	public int getCount_total() {
		return count_total;
	}
	public void setCount_total(int count_total) {
		this.count_total = count_total;
	}
	public double getPl_realized() {
		return pl_realized;
	}
	public void setPl_realized(double pl_realized) {
		this.pl_realized = pl_realized;
	}
	public double getPl_total() {
		return pl_total;
	}
	public void setPl_total(double pl_total) {
		this.pl_total = pl_total;
	}
	public double getPl_unrealized() {
		return pl_unrealized;
	}
	public void setPl_unrealized(double pl_unrealized) {
		this.pl_unrealized = pl_unrealized;
	}

   
}

