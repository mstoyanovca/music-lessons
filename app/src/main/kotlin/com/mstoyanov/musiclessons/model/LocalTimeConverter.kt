package com.mstoyanov.musiclessons.model

import androidx.room.TypeConverter
import com.mstoyanov.musiclessons.global.Functions.formatter
import java.time.LocalTime

class LocalTimeConverter {
    @TypeConverter
    fun fromString(string: String): LocalTime {
        return LocalTime.parse(string, formatter)
    }

    @TypeConverter
    fun toString(localTime: LocalTime): String {
        return localTime.format(formatter)
    }
}
