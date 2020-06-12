package com.udacity.maluleque.meutako.utils

import java.text.NumberFormat
import java.util.*

object NumberUtils {
    @JvmStatic
    fun getFormattedAmount(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(amount)
    }
}