package com.mstoyanov.music_lessons.model

import androidx.room.TypeConverter

class WeekdayConverter {
    @TypeConverter
    fun toWeekdayName(value: String): Weekday? {
        return when (value) {
            "Monday" -> Weekday.MONDAY
            "Tuesday" -> Weekday.TUESDAY
            "Wednesday" -> Weekday.WEDNESDAY
            "Thursday" -> Weekday.THURSDAY
            "Friday" -> Weekday.FRIDAY
            "Saturday" -> Weekday.SATURDAY
            "Sunday" -> Weekday.SUNDAY
            else -> null
        }
    }

    @TypeConverter
    fun toString(weekday: Weekday): String {
        return weekday.displayValue()
    }
}
