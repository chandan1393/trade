package com.fyers.fyerstrading.service.swing;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Objects;

public class YearWeek implements Comparable<YearWeek> {
    private final int year;
    private final int week;

    public YearWeek(int year, int week) {
        this.year = year;
        this.week = week;
    }

    public static YearWeek from(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int year = date.get(weekFields.weekBasedYear());
        int week = date.get(weekFields.weekOfWeekBasedYear());
        return new YearWeek(year, week);
    }

    public int getYear() {
        return year;
    }

    public int getWeek() {
        return week;
    }

    @Override
    public int compareTo(YearWeek other) {
        int cmp = Integer.compare(this.year, other.year);
        if (cmp != 0) return cmp;
        return Integer.compare(this.week, other.week);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YearWeek)) return false;
        YearWeek that = (YearWeek) o;
        return year == that.year && week == that.week;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, week);
    }

    @Override
    public String toString() {
        return String.format("%d-W%02d", year, week);
    }
}


