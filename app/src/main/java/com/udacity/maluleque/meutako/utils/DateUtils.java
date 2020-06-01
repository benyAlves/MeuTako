package com.udacity.maluleque.meutako.utils;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String formatDate(Date date) {
        String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "ddMMyyyy");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return simpleDateFormat.format(date);
    }
}
