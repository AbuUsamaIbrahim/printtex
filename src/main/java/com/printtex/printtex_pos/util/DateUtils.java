package com.printtex.printtex_pos.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    private DateUtils() {

    }

    public static Integer getYearFromCurrentDate() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static Integer getYearFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static Integer getMonthFromCurrentDate() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static String getStringDate(Date date) {
        return getStringDate(date, "dd-MM-yyyy");
    }

    public static String getStringDate(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static Date getDateFromString(String date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getDateFromString(String date) {
        return getDateFromString(date, "dd-MM-yyyy");
    }

    public static Date getExpirationTime(Long expireHours) {
        Date now = new Date();
        Long expireInMilis = TimeUnit.HOURS.toMillis(expireHours);
        return new Date(expireInMilis + now.getTime());
    }
}
