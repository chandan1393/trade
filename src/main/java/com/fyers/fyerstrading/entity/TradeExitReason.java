package com.fyers.fyerstrading.entity;

public enum TradeExitReason {
    STOP_LOSS,
    TARGET1,
    TARGET2,
    TRAILING_STOP_LOSS_BEFORE_T1,
    TRAILING_STOP_LOSS_AFTER_T1,
    NONE, TSL
}
