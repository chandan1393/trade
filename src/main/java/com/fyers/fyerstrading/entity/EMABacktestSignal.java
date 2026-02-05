package com.fyers.fyerstrading.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ema_backtest_signal")
public class EMABacktestSignal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    private String direction;          // "LONG" or "SHORT"
    private double entryPrice;
    private LocalDateTime entryTime;
    private String entryReason;

    // Exit details
    private double exitPrice;
    private LocalDateTime exitTime;
    private String exitReason;

    // Risk Management
    private double stopLoss;           // Dynamic SL at entry

    // Performance metrics
    private double pnlPoints;          // Exit - Entry (or reverse for SHORT)
    private double pnlPercent;         // Percentage gain/loss

    // ---------------- Getters / Setters ----------------

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

    public LocalDateTime getEntryTime() {
        return entryTime;
    }
    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public String getEntryReason() {
        return entryReason;
    }
    public void setEntryReason(String entryReason) {
        this.entryReason = entryReason;
    }

    public double getExitPrice() {
        return exitPrice;
    }
    public void setExitPrice(double exitPrice) {
        this.exitPrice = exitPrice;
        computePnL(); // auto compute when exit is set
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }
    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public String getExitReason() {
        return exitReason;
    }
    public void setExitReason(String exitReason) {
        this.exitReason = exitReason;
    }

    public double getStopLoss() {
        return stopLoss;
    }
    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

    public double getPnlPoints() {
        return pnlPoints;
    }
    public double getPnlPercent() {
        return pnlPercent;
    }

    
    
    
    // ---------------- Utility Methods ----------------

    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	private void computePnL() {
        if (direction == null || entryPrice == 0) return;

        if ("LONG".equalsIgnoreCase(direction)) {
            pnlPoints = exitPrice - entryPrice;
        } else if ("SHORT".equalsIgnoreCase(direction)) {
            pnlPoints = entryPrice - exitPrice;
        }
        pnlPercent = (pnlPoints / entryPrice) * 100.0;
    }

    @Override
    public String toString() {
        return entryTime + " → " + direction +
                " @ " + entryPrice +
                " | SL: " + stopLoss +
                " | Exit: " + exitPrice +
                " | PnL: " + String.format("%.2f", pnlPoints) + " (" + String.format("%.2f", pnlPercent) + "%)" +
                " | Reason: " + entryReason +
                " | ExitReason: " + exitReason;
    }
}
