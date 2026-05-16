package model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mstoyanov.myapplication.function.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
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
    @field:TypeConverters(WeekdayConverter::class) var weekday: Weekday,
    @Serializable(with = LocalTimeSerializer::class)
    @ColumnInfo(name = "time_from")
    @field:TypeConverters(LocalTimeConverter::class)
    var timeFrom: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    @ColumnInfo(name = "time_to")
    @field:TypeConverters(LocalTimeConverter::class)
    var timeTo: LocalTime,
    @ColumnInfo(name = "student_owner_id") var studentId: Long,
    @Ignore var student: Student
) : Comparable<Lesson> {

    constructor() : this(
        lessonId = 0L,
        weekday = Weekday.MONDAY,
        timeFrom = LocalTime.parse("16:00"),
        timeTo = LocalTime.parse("16:30"),
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
