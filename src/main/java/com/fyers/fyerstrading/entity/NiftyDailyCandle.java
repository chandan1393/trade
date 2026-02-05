package com.fyers.fyerstrading.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "nifty_daily_candle")
public class NiftyDailyCandle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Double open;
	private Double high;
	private Double low;
	private Double close;
	private LocalDate tradeDate;

	
	
	
	public NiftyDailyCandle() {
		
	}

	public NiftyDailyCandle(Double open, Double high, Double low, Double close, LocalDate tradeDate) {
		super();
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.tradeDate = tradeDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public Double getOpen() {
		return open;
	}

	public void setOpen(Double open) {
		this.open = open;
	}

	public Double getHigh() {
		return high;
	}

	public void setHigh(Double high) {
		this.high = high;
	}

	public Double getLow() {
		return low;
	}

	public void setLow(Double low) {
		this.low = low;
	}

	public Double getClose() {
		return close;
	}

	public void setClose(Double close) {
		this.close = close;
	}

	

	public LocalDate getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate = tradeDate;
	}



	
	
	
}
