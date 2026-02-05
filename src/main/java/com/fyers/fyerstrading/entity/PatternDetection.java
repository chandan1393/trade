package com.fyers.fyerstrading.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pattern_detections")
public class PatternDetection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private LocalDate detectionDate;
    private String patternType;     // H&S, RECT, TRI
    private Double priceAtDetection;
    private Double volumeAtDetection;
    private Double volumeRatio;     // current_vol / avg_vol
    private Double sma200Value;     // To verify trend strength later
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
	public LocalDate getDetectionDate() {
		return detectionDate;
	}
	public void setDetectionDate(LocalDate detectionDate) {
		this.detectionDate = detectionDate;
	}
	public String getPatternType() {
		return patternType;
	}
	public void setPatternType(String patternType) {
		this.patternType = patternType;
	}
	public Double getPriceAtDetection() {
		return priceAtDetection;
	}
	public void setPriceAtDetection(Double priceAtDetection) {
		this.priceAtDetection = priceAtDetection;
	}
	public Double getVolumeAtDetection() {
		return volumeAtDetection;
	}
	public void setVolumeAtDetection(Double volumeAtDetection) {
		this.volumeAtDetection = volumeAtDetection;
	}
	public Double getVolumeRatio() {
		return volumeRatio;
	}
	public void setVolumeRatio(Double volumeRatio) {
		this.volumeRatio = volumeRatio;
	}
	public Double getSma200Value() {
		return sma200Value;
	}
	public void setSma200Value(Double sma200Value) {
		this.sma200Value = sma200Value;
	}
    
    
    
    
}
