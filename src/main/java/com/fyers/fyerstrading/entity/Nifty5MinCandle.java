package com.fyers.fyerstrading.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "nifty_five_min_candle_data",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"timestamp"})
    })
public class Nifty5MinCandle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Double open;
	private Double high;
	private Double low;
	private Double close;
	private Double volume;

    @Column(nullable = false)
	private LocalDateTime timestamp;

	private Double ema9;
	private Double ema21;

	public Nifty5MinCandle() {

	}

	public Nifty5MinCandle(LocalDate date, Double open, Double high, Double low, Double close, Double volume) {
		super();
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
	}

	public Nifty5MinCandle(Double open, Double high, Double low, Double close, LocalDateTime timestamp) {
		super();
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.timestamp = timestamp;
	}

	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nifty5MinCandle)) return false;
        Nifty5MinCandle that = (Nifty5MinCandle) o;
        return timestamp != null && timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return timestamp != null ? timestamp.hashCode() : 0;
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



	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Double getEma9() {
		return ema9;
	}

	public void setEma9(Double ema9) {
		this.ema9 = ema9;
	}

	public Double getEma21() {
		return ema21;
	}

	public void setEma21(Double ema21) {
		this.ema21 = ema21;
	}

	

}
