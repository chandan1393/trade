package com.fyers.fyerstrading.model;

public class GTTLeg {
	public double price;
	public double triggerPrice;
	public int qty;

	public GTTLeg(double price, double triggerPrice, int qty) {
		this.price = price;
		this.triggerPrice = triggerPrice;
		this.qty = qty;
	}
}
