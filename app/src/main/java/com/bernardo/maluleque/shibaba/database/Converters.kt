package com.bernardo.maluleque.shibaba.database

import androidx.room.TypeConverter
import com.bernardo.maluleque.shibaba.model.CategoryType
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun fromCategoryType(value: CategoryType): String = value.type

    @TypeConverter
    fun toCategoryType(value: String): CategoryType = enumValueOf<CategoryType>(value)

}