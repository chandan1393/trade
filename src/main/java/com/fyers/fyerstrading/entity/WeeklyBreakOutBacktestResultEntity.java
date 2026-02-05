package com.fyers.fyerstrading.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "backtest_result")
public class WeeklyBreakOutBacktestResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private LocalDate entryDate;
    private Double entry;
    private Double stopLoss;
    private Double target1;
    private Double target2;

    private String exitReason;
    private Double exitPrice;
    private LocalDate exitDate;
    private Double pnlPercent;
    private Double pnlAmount;
    private double breakoutDeliveryPercent;
    private double breakoutVolume;
    private int score;
    private boolean aggressive;
    private LocalDate breakoutCandleDate;
    // Optional: audit columns
    private LocalDate createdAt = LocalDate.now();

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

	

	public Double getEntry() {
		return entry;
	}

	public void setEntry(Double entry) {
		this.entry = entry;
	}

	public Double getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(Double stopLoss) {
		this.stopLoss = stopLoss;
	}

	public Double getTarget1() {
		return target1;
	}

	public void setTarget1(Double target1) {
		this.target1 = target1;
	}

	public Double getTarget2() {
		return target2;
	}

	public void setTarget2(Double target2) {
		this.target2 = target2;
	}

	public String getExitReason() {
		return exitReason;
	}

	public void setExitReason(String exitReason) {
		this.exitReason = exitReason;
	}

	public Double getExitPrice() {
		return exitPrice;
	}

	public void setExitPrice(Double exitPrice) {
		this.exitPrice = exitPrice;
	}

	
	public Double getPnlPercent() {
		return pnlPercent;
	}

	public void setPnlPercent(Double pnlPercent) {
		this.pnlPercent = pnlPercent;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public Double getPnlAmount() {
		return pnlAmount;
	}

	public void setPnlAmount(Double pnlAmount) {
		this.pnlAmount = pnlAmount;
	}

	public LocalDate getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(LocalDate entryDate) {
		this.entryDate = entryDate;
	}

	public LocalDate getExitDate() {
		return exitDate;
	}

	public void setExitDate(LocalDate exitDate) {
		this.exitDate = exitDate;
	}

	public double getBreakoutDeliveryPercent() {
		return breakoutDeliveryPercent;
	}

	public void setBreakoutDeliveryPercent(double breakoutDeliveryPercent) {
		this.breakoutDeliveryPercent = breakoutDeliveryPercent;
	}

	public double getBreakoutVolume() {
		return breakoutVolume;
	}

	public void setBreakoutVolume(double breakoutVolume) {
		this.breakoutVolume = breakoutVolume;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isAggressive() {
		return aggressive;
	}

	public void setAggressive(boolean aggressive) {
		this.aggressive = aggressive;
	}

	public LocalDate getBreakoutCandleDate() {
		return breakoutCandleDate;
	}

	public void setBreakoutCandleDate(LocalDate breakoutCandleDate) {
		this.breakoutCandleDate = breakoutCandleDate;
	}

    
    
    
    
    
}
