package com.fyers.fyerstrading.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Overall {

	@JsonProperty("pl_realized")
	private double plRealized;
	
	@JsonProperty("pl_total")
	private double plTotal;
	
	@JsonProperty("pl_unrealized")
	private double plUnrealized;

	
    @JsonProperty("total_pl")
    private double totalPl;

    @JsonProperty("count_total")
    private int countTotal;
    
    @JsonProperty("count_open")
    private int countOpen;

    @JsonProperty("total_investment")
    private double totalInvestment;

    @JsonProperty("total_current_value")
    private double totalCurrentValue;

    @JsonProperty("pnl_perc")
    private double pnlPerc;

	public double getTotalPl() {
		return totalPl;
	}

	public void setTotalPl(double totalPl) {
		this.totalPl = totalPl;
	}

	public int getCountTotal() {
		return countTotal;
	}

	public void setCountTotal(int countTotal) {
		this.countTotal = countTotal;
	}

	public double getTotalInvestment() {
		return totalInvestment;
	}

	public void setTotalInvestment(double totalInvestment) {
		this.totalInvestment = totalInvestment;
	}

	public double getTotalCurrentValue() {
		return totalCurrentValue;
	}

	public void setTotalCurrentValue(double totalCurrentValue) {
		this.totalCurrentValue = totalCurrentValue;
	}

	public double getPnlPerc() {
		return pnlPerc;
	}

	public void setPnlPerc(double pnlPerc) {
		this.pnlPerc = pnlPerc;
	}

	public int getCountOpen() {
		return countOpen;
	}

	public void setCountOpen(int countOpen) {
		this.countOpen = countOpen;
	}

	public double getPlRealized() {
		return plRealized;
	}

	public void setPlRealized(double plRealized) {
		this.plRealized = plRealized;
	}

	public double getPlTotal() {
		return plTotal;
	}

	public void setPlTotal(double plTotal) {
		this.plTotal = plTotal;
	}

	public double getPlUnrealized() {
		return plUnrealized;
	}

	public void setPlUnrealized(double plUnrealized) {
		this.plUnrealized = plUnrealized;
	}

   
    
    
}