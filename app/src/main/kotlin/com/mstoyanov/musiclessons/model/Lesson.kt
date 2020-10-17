package com.mstoyanov.musiclessons.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import org.jetbrains.annotations.NotNull
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "lesson",
        foreignKeys = [(ForeignKey(entity = Student::class, parentColumns = arrayOf("s_id"), childColumns = arrayOf("student_id"), onDelete = CASCADE))],
        indices = [(Index(value = ["student_id"]))])
data class Lesson(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "lesson_id") @NotNull var lessonId: Long,
                  @ColumnInfo(name = "weekday") @NotNull @TypeConverters(WeekdayConverter::class) var weekday: Weekday,
                  @ColumnInfo(name = "time_from") @NotNull @TypeConverters(DateConverter::class) var timeFrom: Date,
                  @ColumnInfo(name = "time_to") @NotNull @TypeConverters(DateConverter::class) var timeTo: Date,
                  @ColumnInfo(name = "student_id") @NotNull var studentId: Long,
                  @Ignore var student: Student) : Comparable<Lesson>, Serializable {

    constructor() : this(
            lessonId = 0L,
            weekday = Weekday.MONDAY,
            timeFrom = SimpleDateFormat("HH:mm", Locale.US).parse("8:00"),
            timeTo = SimpleDateFormat("HH:mm", Locale.US).parse("8:30"),
            studentId = 0L,
            student = Student())

    override fun compareTo(other: Lesson): Int {
        return when {
            timeFrom.toString().compareTo(other.timeFrom.toString(), ignoreCase = true) != 0 -> timeFrom.toString().compareTo(other.timeFrom.toString(), ignoreCase = true)
            timeTo.toString().compareTo(other.timeTo.toString(), ignoreCase = true) != 0 -> timeTo.toString().compareTo(other.timeTo.toString(), ignoreCase = true)
            student.firstName.compareTo(other.student.firstName, ignoreCase = true) != 0 -> student.firstName.compareTo(other.student.firstName, ignoreCase = true)
            else -> student.lastName.compareTo(other.student.lastName, ignoreCase = true)
        }
    }
}