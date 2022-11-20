package com.marxelo.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    // SUNDAY = 7
    final static int SATURDAY = 6;

    public static int getDayOfWeek(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day.getValue();
    }

    public static int getDayOfWeek(String sDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(sDate, formatter);

        return getDayOfWeek(date);
    }

    public static boolean isWeekend(String sDate) {
        return getDayOfWeek(sDate) >= SATURDAY;
    }
}
