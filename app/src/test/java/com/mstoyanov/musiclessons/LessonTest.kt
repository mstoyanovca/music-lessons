package com.mstoyanov.musiclessons

import com.mstoyanov.musiclessons.model.Lesson
import com.mstoyanov.musiclessons.model.Student
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class LessonTest {
    private lateinit var format: SimpleDateFormat

    @Before
    fun setUp() {
        format = SimpleDateFormat("HH:mm", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
    }

    @Test
    fun compare_lessons_by_time_from() {
        val timeFrom1: Date = format.parse("16:00")
        val timeFrom2: Date = format.parse("16:15")
        val timeTo: Date = format.parse("16:45")

        val lesson1 = Lesson(1L, null, timeFrom1, timeTo, 1L, null)
        val lesson2 = Lesson(2L, null, timeFrom2, timeTo, 2L, null)

        Assert.assertEquals(lesson1.compareTo(lesson2), -1)
    }

    @Test
    fun compare_lessons_by_time_to() {
        val timeFrom: Date = format.parse("16:00")
        val timeTo1: Date = format.parse("16:30")
        val timeTo2: Date = format.parse("16:45")

        val lesson1 = Lesson(1L, null, timeFrom, timeTo1, 1L, null)
        val lesson2 = Lesson(2L, null, timeFrom, timeTo2, 2L, null)

        Assert.assertEquals(lesson1.compareTo(lesson2), -1)
    }

    @Test
    fun compare_lessons_by_student_first_name() {
        val timeFrom: Date = format.parse("16:00")
        val timeTo: Date = format.parse("16:30")

        val student1 = Student(1L, "Johm", "Smith", null, null, null)
        val student2 = Student(2L, "John", "Smith", null, null, null)

        val lesson1 = Lesson(1L, null, timeFrom, timeTo, student1.studentId, student1)
        val lesson2 = Lesson(2L, null, timeFrom, timeTo, student2.studentId, student2)

        Assert.assertEquals(lesson1.compareTo(lesson2), -1)
    }

    @Test
    fun compare_lessons_by_student_last_name() {
        val timeFrom: Date = format.parse("16:00")
        val timeTo: Date = format.parse("16:30")

        val student1 = Student(1L, "John", "Smitg", null, null, null)
        val student2 = Student(1L, "John", "Smith", null, null, null)

        val lesson1 = Lesson(1L, null, timeFrom, timeTo, student1.studentId, student1)
        val lesson2 = Lesson(1L, null, timeFrom, timeTo, student2.studentId, student2)

        Assert.assertEquals(lesson1.compareTo(lesson2), -1)
    }
}