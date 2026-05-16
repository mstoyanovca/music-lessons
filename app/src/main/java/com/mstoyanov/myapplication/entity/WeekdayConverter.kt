package com.mstoyanov.myapplication.entity

import androidx.room.TypeConverter

class WeekdayConverter {
    // do not delete this method, AndroidStudio doesn't see it being used, but it is:
    @TypeConverter
    fun toWeekdayName(value: String): Weekday? {
        return when (value) {
            "Monday" -> Weekday.MONDAY
            "Tuesday" -> Weekday.TUESDAY
            "Wednesday" -> Weekday.WEDNESDAY
            "Thursday" -> Weekday.THURSDAY
            "Friday" -> Weekday.FRIDAY
            "Saturday" -> Weekday.SATURDAY
            else -> null
        }
    }

    @TypeConverter
    fun toString(weekday: Weekday): String {
        return weekday.value
    }
}
