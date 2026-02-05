package com.fyers.fyerstrading.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "ema_trade_signals")
public class EMATradeSignal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String direction; // LONG or SHORT
	private double entryPrice;
	private double exitPrice;
	private double sl; // Stop Loss
	private double target; // Target price (optional)
	private String optionSymbol; // Traded option symbol
	private String reason; // EMA crossover + Base breakout/breakdown + Trend
	private String exitReason; // Stop loss, opposite crossover, or EOD
	private LocalDateTime timestamp; // Entry timestamp
	private LocalDateTime exitTime; // Exit timestamp

	@Lob
	private String orderResponse; // JSON or string representation of Fyers order response

	// Constructors
	public EMATradeSignal() {
	}

	public EMATradeSignal(String direction, double entryPrice, double sl, double target, String optionSymbol,
			String reason, LocalDateTime timestamp) {
		this.direction = direction;
		this.entryPrice = entryPrice;
		this.sl = sl;
		this.target = target;
		this.optionSymbol = optionSymbol;
		this.reason = reason;
		this.timestamp = timestamp;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public double getEntryPrice() {
		return entryPrice;
	}

	public void setEntryPrice(double entryPrice) {
		this.entryPrice = entryPrice;
	}

	public double getExitPrice() {
		return exitPrice;
	}

	public void setExitPrice(double exitPrice) {
		this.exitPrice = exitPrice;
	}

	public double getSl() {
		return sl;
	}

	public void setSl(double sl) {
		this.sl = sl;
	}

	public double getTarget() {
		return target;
	}

	public void setTarget(double target) {
		this.target = target;
	}

	public String getOptionSymbol() {
		return optionSymbol;
	}

	public void setOptionSymbol(String optionSymbol) {
		this.optionSymbol = optionSymbol;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getExitReason() {
		return exitReason;
	}

	public void setExitReason(String exitReason) {
		this.exitReason = exitReason;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public LocalDateTime getExitTime() {
		return exitTime;
	}

	public void setExitTime(LocalDateTime exitTime) {
		this.exitTime = exitTime;
	}

	public String getOrderResponse() {
		return orderResponse;
	}

	public void setOrderResponse(String orderResponse) {
		this.orderResponse = orderResponse;
	}

	@Override
	public String toString() {
		return "EMATradeSignal{" + "id=" + id + ", direction='" + direction + '\'' + ", entryPrice=" + entryPrice
				+ ", exitPrice=" + exitPrice + ", sl=" + sl + ", target=" + target + ", optionSymbol='" + optionSymbol
				+ '\'' + ", reason='" + reason + '\'' + ", exitReason='" + exitReason + '\'' + ", timestamp="
				+ timestamp + ", exitTime=" + exitTime + '}';
	}
}
