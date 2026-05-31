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
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("student_id"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["student_id", "weekday"])]
)
data class Lesson(
    @PrimaryKey
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
    @Ignore val student: Student
) : Comparable<Lesson> {

    constructor(
        id: Long = 0L,
        weekday: Weekday = Weekday.MONDAY,
        timeFrom: LocalTime = LocalTime.of(16, 0),
        timeTo: LocalTime = LocalTime.of(16, 30),
        studentId: Long = 0L
    ) : this(
        id = id,
        weekday = weekday,
        timeFrom = timeFrom,
        timeTo = timeTo,
        studentId = studentId,
        student = Student()
    )

    override fun compareTo(other: Lesson): Int {
        return when {
            timeFrom.compareTo(other.timeFrom) != 0 -> timeFrom.compareTo(other.timeFrom)
            timeTo.compareTo(other.timeTo) != 0 -> timeTo.compareTo(other.timeTo)
            student.firstName.compareTo(other.student.firstName, ignoreCase = true) != 0 -> student.firstName.compareTo(other.student.firstName, ignoreCase = true)
            else -> student.lastName.compareTo(other.student.lastName, ignoreCase = true)
        }
    }

}
