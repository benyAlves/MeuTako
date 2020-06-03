package com.udacity.maluleque.meutako.utils;

import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class DateUtils {

    public static String formatDate(Date date) {
        String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "ddMMyyyy");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String formatDateMonth(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static Date formatDateMonth(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String lastMonth() {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, -1);
        return formatDateMonth(calendar.getTime());
    }

    public static String thisMonth() {
        Calendar calendar = new GregorianCalendar();
        return formatDateMonth(calendar.getTime());
    }

    public static String nextMonth() {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, 1);
        return formatDateMonth(calendar.getTime());
    }

    public static String getDataDayMonth(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd", Locale.getDefault());
        Date d = new Date(date);
        return formatter.format(d);
    }

    public static long[] getDateIntervals(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        long[] dates = new long[2];
        try {
            Date convertedDate = formatter.parse(date);
            dates[0] = convertedDate.getTime();

            Calendar c = Calendar.getInstance();
            c.setTime(convertedDate);
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

            dates[1] = c.getTime().getTime();
            return dates;
        } catch (ParseException e) {
            Log.e("DateUtils ", "Error parsing date", e);
            return dates;
        }
    }

    public static List<String> generateDates() {
        List<String> dates = new ArrayList<>();

        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, 1);

        dates.add(formatDateMonth(calendar.getTime()));

        calendar = new GregorianCalendar();

        dates.add(formatDateMonth(calendar.getTime()));

        for (int i = 0; i < 10; i++) {
            calendar.add(Calendar.MONTH, -1);
            dates.add(formatDateMonth(calendar.getTime()));
        }
        Collections.reverse(dates);

        return dates;
    }


}
