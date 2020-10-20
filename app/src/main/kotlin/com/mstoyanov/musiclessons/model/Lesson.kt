package com.mstoyanov.musiclessons.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import org.jetbrains.annotations.NotNull
import java.io.Serializable
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "lesson",
        foreignKeys = [ForeignKey(entity = Student::class, parentColumns = arrayOf("student_id"), childColumns = arrayOf("student_owner_id"), onDelete = CASCADE)],
        indices = [Index(value = ["student_owner_id"])])
data class Lesson(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "lesson_id") @NotNull var lessonId: Long,
                  @NotNull @TypeConverters(WeekdayConverter::class) var weekday: Weekday,
                  @ColumnInfo(name = "time_from") @NotNull @TypeConverters(LocalTimeConverter::class) var timeFrom: LocalTime,
                  @ColumnInfo(name = "time_to") @NotNull @TypeConverters(LocalTimeConverter::class) var timeTo: LocalTime,
                  @ColumnInfo(name = "student_owner_id") @NotNull var studentId: Long,
                  @Ignore var student: Student) : Comparable<Lesson>, Serializable {

    constructor() : this(
            lessonId = 0L,
            weekday = Weekday.MONDAY,
            timeFrom = LocalTime.parse("08:00", DateTimeFormatter.ofPattern("HH:mm")),
            timeTo = LocalTime.parse("08:30", DateTimeFormatter.ofPattern("HH:mm")),
            studentId = 0L,
            student = Student())

    override fun compareTo(other: Lesson): Int {
        return when {
            timeFrom.compareTo(other.timeFrom) != 0 -> timeFrom.compareTo(other.timeFrom)
            timeTo.compareTo(other.timeTo) != 0 -> timeTo.compareTo(other.timeTo)
            student.firstName.compareTo(other.student.firstName, ignoreCase = true) != 0 -> student.firstName.compareTo(other.student.firstName, ignoreCase = true)
            else -> student.lastName.compareTo(other.student.lastName, ignoreCase = true)
        }
    }
}
