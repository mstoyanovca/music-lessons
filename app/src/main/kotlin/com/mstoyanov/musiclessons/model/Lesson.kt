package com.mstoyanov.musiclessons.model

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import com.mstoyanov.musiclessons.global.Functions.formatter
import java.io.Serializable
import java.time.LocalTime

@Entity(
    tableName = "lesson",
    foreignKeys = [ForeignKey(
        entity = Student::class,
        parentColumns = arrayOf("student_id"),
        childColumns = arrayOf("student_owner_id"),
        onDelete = CASCADE
    )],
    indices = [Index(value = ["student_owner_id"])]
)
data class Lesson(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "lesson_id") var lessonId: Long,
    @TypeConverters(WeekdayConverter::class) var weekday: Weekday,
    @ColumnInfo(name = "time_from") @TypeConverters(LocalTimeConverter::class) var timeFrom: LocalTime,
    @ColumnInfo(name = "time_to") @TypeConverters(LocalTimeConverter::class) var timeTo: LocalTime,
    @ColumnInfo(name = "student_owner_id") var studentId: Long,
    @Ignore var student: Student
) : Comparable<Lesson>, Serializable {

    constructor() : this(
        lessonId = 0L,
        weekday = Weekday.MONDAY,
        timeFrom = LocalTime.parse("16:00", formatter),
        timeTo = LocalTime.parse("16:30", formatter),
        studentId = 0L,
        student = Student()
    )

    override fun compareTo(other: Lesson): Int {
        return when {
            timeFrom.compareTo(other.timeFrom) != 0 -> timeFrom.compareTo(other.timeFrom)
            timeTo.compareTo(other.timeTo) != 0 -> timeTo.compareTo(other.timeTo)
            student.firstName.compareTo(
                other.student.firstName,
                ignoreCase = true
            ) != 0 -> student.firstName.compareTo(other.student.firstName, ignoreCase = true)
            else -> student.lastName.compareTo(other.student.lastName, ignoreCase = true)
        }
    }
}
