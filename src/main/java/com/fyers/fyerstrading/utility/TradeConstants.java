package com.fyers.fyerstrading.utility;

import java.util.Set;

import com.fyers.fyerstrading.entity.TradeStatus;

public final class TradeConstants {

    private TradeConstants() {
        // prevent instantiation
    }

    public static final Set<TradeStatus> ACTIVE_STATUSES = Set.of(
        TradeStatus.GTT_SELL_ORDERS_PLACED,
        TradeStatus.GTT_ORDER_MODIFIED_AFTER_T1
    );

}