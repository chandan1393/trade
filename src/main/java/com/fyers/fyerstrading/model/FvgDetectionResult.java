package com.fyers.fyerstrading.model;

import com.fyers.fyerstrading.entity.StockDailyPrice;

public class FvgDetectionResult {
    private StockDailyPrice detectedCandle;
    private double entryPrice;
    private double stopLoss;
    private double target1;
    private double target2;
    private double initialTsl;

    public StockDailyPrice getDetectedCandle() {
        return detectedCandle;
    }

    public void setDetectedCandle(StockDailyPrice detectedCandle) {
        this.detectedCandle = detectedCandle;
    }

    public double getEntryPrice() {
        return entryPrice;
    }

    public void setEntryPrice(double entryPrice) {
        this.entryPrice = entryPrice;
    }

    public double getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

    

    public double getTarget1() {
		return target1;
	}

	public void setTarget1(double target1) {
		this.target1 = target1;
	}

	public double getTarget2() {
		return target2;
	}

	public void setTarget2(double target2) {
		this.target2 = target2;
	}

	public double getInitialTsl() {
        return initialTsl;
    }

    public void setInitialTsl(double initialTsl) {
        this.initialTsl = initialTsl;
    }
}

