package music_lessons.model

import androidx.room.TypeConverter
import music_lessons.global.Functions
import java.time.LocalTime

class LocalTimeConverter {
    @TypeConverter
    fun fromString(string: String): LocalTime {
        return LocalTime.parse(string, Functions.dateTimeFormatter)
    }

    @TypeConverter
    fun toString(localTime: LocalTime): String {
        return localTime.format(Functions.dateTimeFormatter)
    }
}
