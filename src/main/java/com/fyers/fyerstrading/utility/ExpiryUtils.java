package com.fyers.fyerstrading.utility;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

public class ExpiryUtils {

    public static String getExpiryPrefix() {
        LocalDate today = LocalDate.now();
        LocalDate nextThursday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));

        // If today is Thursday after 3:30 PM, use next week's expiry
        if (today.getDayOfWeek() == DayOfWeek.THURSDAY && LocalTime.now().isAfter(LocalTime.of(15, 30))) {
            nextThursday = nextThursday.plusWeeks(1);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMMyy", Locale.ENGLISH);
        String formatted = nextThursday.format(formatter).toUpperCase(); // e.g. 18JUL24

        return "NIFTY" + formatted.substring(0, 5); 
    }
}

