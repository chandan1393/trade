package com.fyers.fyerstrading.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fyers.fyerstrading.enu.Side;

public class TradeResult {

    // ================= CORE TRADE DATA =================
    private LocalDate tradeDate;
    private Side side;

    private double entryPrice;
    private double exitPrice;
    private int qty;
    private double pnl;

    private String exitReason;

    // ================= TIME ANALYTICS =================
    private LocalDateTime setupTime;   // breakout detected
    private LocalDateTime entryTime;   // actual entry candle
    private LocalDateTime exitTime;    // TSL / TIME_EXIT candle

    // ================= CONSTRUCTORS =================
    public TradeResult() {}

    // ================= GETTERS / SETTERS =================
    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
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

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPnl() {
        return pnl;
    }

    public void setPnl(double pnl) {
        this.pnl = pnl;
    }

    public String getExitReason() {
        return exitReason;
    }

    public void setExitReason(String exitReason) {
        this.exitReason = exitReason;
    }

    public LocalDateTime getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(LocalDateTime setupTime) {
        this.setupTime = setupTime;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    // ================= HELPER =================
    public long getTradeDurationMinutes() {
        if (entryTime == null || exitTime == null)
            return 0;
        return java.time.Duration.between(entryTime, exitTime).toMinutes();
    }

    // ================= DEBUG PRINT =================
    @Override
    public String toString() {
        return String.format(
            "%s | %s | Entry: %.2f | Exit: %.2f | Qty: %d | PnL: %.2f | %s | Setup: %s | EntryTime: %s | ExitTime: %s",
            tradeDate,
            side,
            entryPrice,
            exitPrice,
            qty,
            pnl,
            exitReason,
            setupTime,
            entryTime,
            exitTime
        );
    }
}
