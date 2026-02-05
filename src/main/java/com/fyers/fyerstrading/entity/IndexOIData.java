package com.fyers.fyerstrading.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "index_oi_data", indexes = { @Index(name = "idx_index_symbol_ts", columnList = "symbol, timestamp"),
		@Index(name = "idx_index_expiry", columnList = "expiry") })
public class IndexOIData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// NSE:NIFTY50-INDEX
	@Column(nullable = false, length = 50)
	private String symbol;

	@Column(nullable = false, length = 50)
	private String optionSymbol;

	@Column(nullable = false)
	private LocalDate expiry;

	@Column(nullable = false)
	private Double strikePrice;

	// ---- CALL ----
	private Long callOI;
	private Long callChgOI;
	private Double callLTP;
	private Long volumeCall;
	private Double ivCall;

	// ---- PUT ----
	private Long putOI;
	private Long putChgOI;
	private Double putLTP;
	private Long volumePut;
	private Double ivPut;

	// Index-only
	private Double indiaVixLtp;

	// Fixed by design
	@Column(nullable = false)
	private Integer intervalMin = 1;

	@Column(nullable = false)
	private LocalDateTime timestamp;

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

	public String getOptionSymbol() {
		return optionSymbol;
	}

	public void setOptionSymbol(String optionSymbol) {
		this.optionSymbol = optionSymbol;
	}

	public LocalDate getExpiry() {
		return expiry;
	}

	public void setExpiry(LocalDate expiry) {
		this.expiry = expiry;
	}

	public Double getStrikePrice() {
		return strikePrice;
	}

	public void setStrikePrice(Double strikePrice) {
		this.strikePrice = strikePrice;
	}

	public Long getCallOI() {
		return callOI;
	}

	public void setCallOI(Long callOI) {
		this.callOI = callOI;
	}

	public Long getCallChgOI() {
		return callChgOI;
	}

	public void setCallChgOI(Long callChgOI) {
		this.callChgOI = callChgOI;
	}

	public Double getCallLTP() {
		return callLTP;
	}

	public void setCallLTP(Double callLTP) {
		this.callLTP = callLTP;
	}

	public Long getVolumeCall() {
		return volumeCall;
	}

	public void setVolumeCall(Long volumeCall) {
		this.volumeCall = volumeCall;
	}

	public Double getIvCall() {
		return ivCall;
	}

	public void setIvCall(Double ivCall) {
		this.ivCall = ivCall;
	}

	public Long getPutOI() {
		return putOI;
	}

	public void setPutOI(Long putOI) {
		this.putOI = putOI;
	}

	public Long getPutChgOI() {
		return putChgOI;
	}

	public void setPutChgOI(Long putChgOI) {
		this.putChgOI = putChgOI;
	}

	public Double getPutLTP() {
		return putLTP;
	}

	public void setPutLTP(Double putLTP) {
		this.putLTP = putLTP;
	}

	public Long getVolumePut() {
		return volumePut;
	}

	public void setVolumePut(Long volumePut) {
		this.volumePut = volumePut;
	}

	public Double getIvPut() {
		return ivPut;
	}

	public void setIvPut(Double ivPut) {
		this.ivPut = ivPut;
	}

	public Double getIndiaVixLtp() {
		return indiaVixLtp;
	}

	public void setIndiaVixLtp(Double indiaVixLtp) {
		this.indiaVixLtp = indiaVixLtp;
	}

	public Integer getIntervalMin() {
		return intervalMin;
	}

	public void setIntervalMin(Integer intervalMin) {
		this.intervalMin = intervalMin;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

}
