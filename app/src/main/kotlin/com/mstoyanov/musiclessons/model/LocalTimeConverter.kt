package com.mstoyanov.musiclessons.model

import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LocalTimeConverter {
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    @TypeConverter
    fun fromString(string: String): LocalTime {
        return LocalTime.parse(string, formatter)
    }

    @TypeConverter
    fun toString(localTime: LocalTime): String {
        return localTime.format(formatter)
    }
}
