package com.mstoyanov.musiclessons.entity

import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LocalTimeConverter {
    @TypeConverter
    fun fromString(value: String?): LocalTime? {
        // this is needed to parse properly "9:30":
        return value?.let { LocalTime.parse(it, DateTimeFormatter.ofPattern("H:mm")) }
    }

    @TypeConverter
    fun timeToString(time: LocalTime?): String? {
        return time?.toString()
    }
}
