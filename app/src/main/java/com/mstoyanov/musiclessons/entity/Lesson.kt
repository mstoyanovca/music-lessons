package com.mstoyanov.musiclessons.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
@Entity(
    tableName = "lesson",
    foreignKeys = [ForeignKey(
        entity = Student::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("student_id"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["student_id", "weekday"])]
)
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
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
    @ColumnInfo(name = "student_id")
    val studentId: Long,
) : Comparable<Lesson> {
    @Ignore
    lateinit var student: Student

    override fun compareTo(other: Lesson): Int {
        return when {
            timeFrom.compareTo(other.timeFrom) != 0 -> timeFrom.compareTo(other.timeFrom)
            timeTo.compareTo(other.timeTo) != 0 -> timeTo.compareTo(other.timeTo)
            student.firstName.compareTo(other.student.firstName, ignoreCase = true) != 0 -> student.firstName.compareTo(other.student.firstName, ignoreCase = true)
            else -> student.lastName.compareTo(other.student.lastName, ignoreCase = true)
        }
    }

}
