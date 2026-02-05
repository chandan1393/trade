package com.fyers.fyerstrading.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "resistance_setup")
public class ResistanceSetupEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String symbol;

	private double resistance;

	private LocalDate resistanceDate;

	private double lastClose;

	private double diffPercent;

	private boolean nearResistance;

	private LocalDate detectedDate;

	private String reason;

	// getters / setters
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

	public double getResistance() {
		return resistance;
	}

	public void setResistance(double resistance) {
		this.resistance = resistance;
	}

	public LocalDate getResistanceDate() {
		return resistanceDate;
	}

	public void setResistanceDate(LocalDate resistanceDate) {
		this.resistanceDate = resistanceDate;
	}

	public double getLastClose() {
		return lastClose;
	}

	public void setLastClose(double lastClose) {
		this.lastClose = lastClose;
	}

	public double getDiffPercent() {
		return diffPercent;
	}

	public void setDiffPercent(double diffPercent) {
		this.diffPercent = diffPercent;
	}

	public boolean isNearResistance() {
		return nearResistance;
	}

	public void setNearResistance(boolean nearResistance) {
		this.nearResistance = nearResistance;
	}

	public LocalDate getDetectedDate() {
		return detectedDate;
	}

	public void setDetectedDate(LocalDate detectedDate) {
		this.detectedDate = detectedDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}