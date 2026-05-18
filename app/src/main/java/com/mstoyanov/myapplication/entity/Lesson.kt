package com.mstoyanov.myapplication.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
@Entity(
    tableName = "lesson",
    indices = [Index(value = ["weekday"])]
)
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "lesson_id")
    val lessonId: Long,
    @field:TypeConverters(WeekdayConverter::class)
    @ColumnInfo(name = "weekday")
    val weekday: Weekday,
    @Serializable(with = LocalTimeSerializer::class)
    @field:TypeConverters(LocalTimeConverter::class)
    @ColumnInfo(name = "time_from")
    val timeFrom: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    @field:TypeConverters(LocalTimeConverter::class)
    @ColumnInfo(name = "time_to")
    val timeTo: LocalTime,
    @ColumnInfo(name = "student_owner_id")
    val studentId: Long,
) : Comparable<Lesson> {

    override fun compareTo(other: Lesson): Int {
        return when {
            timeFrom.compareTo(other.timeFrom) != 0 -> timeFrom.compareTo(other.timeFrom)
            else -> timeTo.compareTo(other.timeTo)
        }
    }

    /*override fun compareTo(other: Lesson): Int {
        return when {
            timeFrom.compareTo(other.timeFrom) != 0 -> timeFrom.compareTo(other.timeFrom)
            timeTo.compareTo(other.timeTo) != 0 -> timeTo.compareTo(other.timeTo)
            student.firstName.compareTo(
                other.student.firstName,
                ignoreCase = true
            ) != 0 -> student.firstName.compareTo(other.student.firstName, ignoreCase = true)

            else -> student.lastName.compareTo(other.student.lastName, ignoreCase = true)
        }
    }*/

}
