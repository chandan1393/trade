package com.fyers.fyerstrading.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fyers.fyerstrading.enu.ExitType;

@Entity
@Table(name = "index_option_trade_leg", indexes = @Index(name = "idx_index_trade", columnList = "tradeId"))
public class IndexOptionTradeLeg {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long tradeId;

	private int legNo;

	private int quantity;

	private double entry;

	private double target;

	private double sl;

	private LocalDateTime exitedAt;

	@Enumerated(EnumType.STRING)
	private ExitType exitType;

	private String status;

	private LocalDateTime createdAt = LocalDateTime.now();

	@Transient
	private List<IndexOptionTradeLeg> legs;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public double getEntry() {
		return entry;
	}

	public void setEntry(double entry) {
		this.entry = entry;
	}

	public double getTarget() {
		return target;
	}

	public void setTarget(double target) {
		this.target = target;
	}

	public double getSl() {
		return sl;
	}

	public void setSl(double sl) {
		this.sl = sl;
	}

	public ExitType getExitType() {
		return exitType;
	}

	public void setExitType(ExitType exitType) {
		this.exitType = exitType;
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

	public List<IndexOptionTradeLeg> getLegs() {
		return legs;
	}

	public void setLegs(List<IndexOptionTradeLeg> legs) {
		this.legs = legs;
	}

}
