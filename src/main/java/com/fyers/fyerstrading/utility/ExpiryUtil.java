package com.fyers.fyerstrading.utility;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ExpiryUtil {
	
	
	private static final Map<String, Month> MONTH_MAP = new HashMap<String, Month>();

    static {
        MONTH_MAP.put("JAN", Month.JANUARY);
        MONTH_MAP.put("FEB", Month.FEBRUARY);
        MONTH_MAP.put("MAR", Month.MARCH);
        MONTH_MAP.put("APR", Month.APRIL);
        MONTH_MAP.put("MAY", Month.MAY);
        MONTH_MAP.put("JUN", Month.JUNE);
        MONTH_MAP.put("JUL", Month.JULY);
        MONTH_MAP.put("AUG", Month.AUGUST);
        MONTH_MAP.put("SEP", Month.SEPTEMBER);
        MONTH_MAP.put("OCT", Month.OCTOBER);
        MONTH_MAP.put("NOV", Month.NOVEMBER);
        MONTH_MAP.put("DEC", Month.DECEMBER);
    }

    

	public static LocalDate extractExpiryFromSymbol(String symbol) {

		// Remove exchange prefix
		String s = symbol.contains(":") ? symbol.split(":")[1] : symbol;

		// Find first digit (start of expiry block)
		int i = 0;
		while (i < s.length() && !Character.isDigit(s.charAt(i))) {
			i++;
		}

		// Now s.substring(i) = 2620323950PE
		String expiryBlock = s.substring(i);

		// Year (first 2 digits)
		int year = 2000 + Integer.parseInt(expiryBlock.substring(0, 2));

		// MonthDay (next 3 digits)
		int md = Integer.parseInt(expiryBlock.substring(2, 5));
		int month = md / 100; // 203 → 2
		int day = md % 100; // 203 → 03

		return LocalDate.of(year, month, day);
	}
	
	
	
	public static LocalDate extractStockExpiry(String symbol) {

        // Remove NSE:
        String s = symbol.contains(":") ? symbol.split(":")[1] : symbol;
        // Example: VOLTAS26FEB1200CE

        // Find first digit
        int i = 0;
        while (i < s.length() && !Character.isDigit(s.charAt(i))) {
            i++;
        }

        // Day
        int day = Integer.parseInt(s.substring(i, i + 2));

        // Month (3-letter)
        String monStr = s.substring(i + 2, i + 5).toUpperCase(Locale.ENGLISH);
        Month month = MONTH_MAP.get(monStr);

        if (month == null) {
            throw new IllegalArgumentException("Invalid month in symbol: " + symbol);
        }

        int year = LocalDate.now().getYear();
        LocalDate expiry = LocalDate.of(year, month, day);

        // 🔁 Handle rollover (expiry already passed → next year/month cycle)
        if (expiry.isBefore(LocalDate.now())) {
            expiry = expiry.plusYears(1);
        }

        return expiry;
    }
	
	
	
}
