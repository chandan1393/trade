package com.fyers.fyerstrading.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fyers.fyerstrading.enu.ExitType;
import com.fyers.fyerstrading.enu.TradeRole;

@Entity
@Table(name = "stock_option_trade_leg")
public class StockOptionTradeLeg {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long legId;

	@Column(nullable = false)
	private Long tradeId;

	@Column(nullable = false)
	private int legNo; // 1,2,...

	@Column(nullable = false)
	private int quantity; // one lot quantity

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TradeRole role; // INCOME / RUNNER

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ExitType exitType; // TARGET / SL_UPGRADE / TRAIL

	// ===== Pricing =====
	private double entry;
	private double sl;

	private Double target; // null for runner

	private double t1;
	private double t2;
	private double t3;

	// ===== Milestone Flags =====
	private boolean t1Hit;
	private boolean t2Hit;
	private boolean t3Hit;

	// ===== Broker Orders =====
	private String entryOrderId;
	private String slOrderId;
	private String targetOrderId;

	// ===== Status =====
	private String status; // OPEN / EXITED / CANCELLED

	private LocalDateTime createdAt;
	private LocalDateTime exitedAt;


	public Long getLegId() {
		return legId;
	}

	public void setLegId(Long legId) {
		this.legId = legId;
	}

	public Long getTradeId() {
		return tradeId;
	}

	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}

	public int getLegNo() {
		return legNo;
	}

	public void setLegNo(int legNo) {
		this.legNo = legNo;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public TradeRole getRole() {
		return role;
	}

	public void setRole(TradeRole role) {
		this.role = role;
	}

	public ExitType getExitType() {
		return exitType;
	}

	public void setExitType(ExitType exitType) {
		this.exitType = exitType;
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

	public Double getTarget() {
		return target;
	}

	public void setTarget(Double target) {
		this.target = target;
	}

	public double getT1() {
		return t1;
	}

	public void setT1(double t1) {
		this.t1 = t1;
	}

	public double getT2() {
		return t2;
	}

	public void setT2(double t2) {
		this.t2 = t2;
	}

	public double getT3() {
		return t3;
	}

	public void setT3(double t3) {
		this.t3 = t3;
	}

	public boolean isT1Hit() {
		return t1Hit;
	}

	public void setT1Hit(boolean t1Hit) {
		this.t1Hit = t1Hit;
	}

	public boolean isT2Hit() {
		return t2Hit;
	}

	public void setT2Hit(boolean t2Hit) {
		this.t2Hit = t2Hit;
	}

	public boolean isT3Hit() {
		return t3Hit;
	}

	public void setT3Hit(boolean t3Hit) {
		this.t3Hit = t3Hit;
	}

	public String getEntryOrderId() {
		return entryOrderId;
	}

	public void setEntryOrderId(String entryOrderId) {
		this.entryOrderId = entryOrderId;
	}

	public String getSlOrderId() {
		return slOrderId;
	}

	public void setSlOrderId(String slOrderId) {
		this.slOrderId = slOrderId;
	}

	public String getTargetOrderId() {
		return targetOrderId;
	}

	public void setTargetOrderId(String targetOrderId) {
		this.targetOrderId = targetOrderId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getExitedAt() {
		return exitedAt;
	}

	public void setExitedAt(LocalDateTime exitedAt) {
		this.exitedAt = exitedAt;
	}
	
	

}
