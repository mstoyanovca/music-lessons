package com.mstoyanov.musiclessons.model

import androidx.room.TypeConverter
import com.mstoyanov.musiclessons.global.Functions.dateTimeFormatter
import java.time.LocalTime

class LocalTimeConverter {
    @TypeConverter
    fun fromString(string: String): LocalTime {
        return LocalTime.parse(string, dateTimeFormatter)
    }

    @TypeConverter
    fun toString(localTime: LocalTime): String {
        return localTime.format(dateTimeFormatter)
    }
}
