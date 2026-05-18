package com.mstoyanov.myapplication.entity

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
        parentColumns = arrayOf("student_id"),
        childColumns = arrayOf("student_owner_id"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["student_owner_id", "weekday"])]
)
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "lesson_id")
    val lessonId: Long = 0L,
    @field:TypeConverters(WeekdayConverter::class)
    @ColumnInfo(name = "weekday")
    val weekday: Weekday = Weekday.MONDAY,
    @Serializable(with = LocalTimeSerializer::class)
    @field:TypeConverters(LocalTimeConverter::class)
    @ColumnInfo(name = "time_from")
    val timeFrom: LocalTime = LocalTime.of(16, 0),
    @Serializable(with = LocalTimeSerializer::class)
    @field:TypeConverters(LocalTimeConverter::class)
    @ColumnInfo(name = "time_to")
    val timeTo: LocalTime = LocalTime.of(16, 30),
    @ColumnInfo(name = "student_owner_id")
    val studentId: Long = 0L,
    @Ignore val student: Student = Student()
) : Comparable<Lesson> {

    override fun compareTo(other: Lesson): Int {
        return when {
            timeFrom.compareTo(other.timeFrom) != 0 -> timeFrom.compareTo(other.timeFrom)
            timeTo.compareTo(other.timeTo) != 0 -> timeTo.compareTo(other.timeTo)
            student.firstName.compareTo(other.student.firstName, ignoreCase = true) != 0 -> student.firstName.compareTo(other.student.firstName, ignoreCase = true)
            else -> student.lastName.compareTo(other.student.lastName, ignoreCase = true)
        }
    }

}
