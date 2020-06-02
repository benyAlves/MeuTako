package com.udacity.maluleque.meutako.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtils {
    public static String getFormattedAmount(double amount) {
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(amount);
    }
}
