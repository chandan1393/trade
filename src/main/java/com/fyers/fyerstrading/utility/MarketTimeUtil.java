package com.fyers.fyerstrading.utility;

import java.time.LocalTime;
import java.time.ZoneId;

public class MarketTimeUtil {

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");
    private static final LocalTime START = LocalTime.of(9, 15);
    private static final LocalTime END = LocalTime.of(15, 30);

    public static boolean isMarketOpen() {
        LocalTime now = LocalTime.now(ZONE);
        return !now.isBefore(START) && !now.isAfter(END);
    }
}

