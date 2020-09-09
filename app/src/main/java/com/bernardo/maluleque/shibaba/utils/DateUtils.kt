package com.bernardo.maluleque.shibaba.utils

import android.text.format.DateFormat
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatDate(date: Date?): String {
        val pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "ddMMyyyy")
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    fun formatDateMonth(date: Date?): String {
        val simpleDateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    fun formatDateMonth(date: String?): Date? {
        val simpleDateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        try {
            return simpleDateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun lastMonth(): String {
        val calendar: Calendar = GregorianCalendar()
        calendar.add(Calendar.MONTH, -1)
        return formatDateMonth(calendar.time)
    }

    @JvmStatic
    fun thisMonth(): String {
        val calendar: Calendar = GregorianCalendar()
        return formatDateMonth(calendar.time)
    }

    @JvmStatic
    fun nextMonth(): String {
        val calendar: Calendar = GregorianCalendar()
        calendar.add(Calendar.MONTH, 1)
        return formatDateMonth(calendar.time)
    }

    @JvmStatic
    fun getDataDayMonth(date: Long): String {
        val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
        val d = Date(date)
        return formatter.format(d)
    }

    fun getDateIntervals(date: String?): LongArray {
        val formatter = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val dates = LongArray(2)
        return try {
            val convertedDate = formatter.parse(date)
            dates[0] = convertedDate.time
            val c = Calendar.getInstance()
            c.time = convertedDate
            c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
            dates[1] = c.time.time
            dates
        } catch (e: ParseException) {
            Log.e("DateUtils ", "Error parsing date", e)
            dates
        }
    }

    fun generateDates(): List<String> {
        val dates: MutableList<String> = ArrayList()
        var calendar: Calendar = GregorianCalendar()
        calendar.add(Calendar.MONTH, 1)
        dates.add(formatDateMonth(calendar.time))
        calendar = GregorianCalendar()
        dates.add(formatDateMonth(calendar.getTime()))
        for (i in 0..9) {
            calendar.add(Calendar.MONTH, -1)
            dates.add(formatDateMonth(calendar.getTime()))
        }
        Collections.reverse(dates)
        return dates
    }
}