package com.mstoyanov.musiclessons

import com.mstoyanov.musiclessons.model.Lesson
import com.mstoyanov.musiclessons.model.Student
import com.mstoyanov.musiclessons.model.Weekday
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LessonTest {
    private lateinit var formatter: DateTimeFormatter

    @Before
    fun setUp() {
        formatter = DateTimeFormatter.ofPattern("HH:mm")
    }

    @Test
    fun compare_lessons_by_time_from() {
        val timeFrom1: LocalTime = LocalTime.parse("16:00", formatter)
        val timeFrom2: LocalTime = LocalTime.parse("16:15", formatter)
        val timeTo: LocalTime = LocalTime.parse("16:45", formatter)

        val lesson1 = Lesson(1L, Weekday.MONDAY, timeFrom1, timeTo, 1L, Student())
        val lesson2 = Lesson(2L, Weekday.MONDAY, timeFrom2, timeTo, 2L, Student())

        Assert.assertEquals(lesson1.compareTo(lesson2), -1)
    }

    @Test
    fun compare_lessons_by_time_to() {
        val timeFrom: LocalTime = LocalTime.parse("16:00", formatter)
        val timeTo1: LocalTime = LocalTime.parse("16:30", formatter)
        val timeTo2: LocalTime = LocalTime.parse("16:45", formatter)

        val lesson1 = Lesson(1L, Weekday.MONDAY, timeFrom, timeTo1, 1L, Student())
        val lesson2 = Lesson(2L, Weekday.MONDAY, timeFrom, timeTo2, 2L, Student())

        Assert.assertEquals(lesson1.compareTo(lesson2), -1)
    }

    @Test
    fun compare_lessons_by_student_first_name() {
        val timeFrom: LocalTime = LocalTime.parse("16:00", formatter)
        val timeTo: LocalTime = LocalTime.parse("16:30", formatter)

        val student1 = Student(1L, "Johm", "Smith", "", "", mutableListOf())
        val student2 = Student(2L, "John", "Smith", "", "", mutableListOf())

        val lesson1 = Lesson(1L, Weekday.MONDAY, timeFrom, timeTo, student1.studentId, student1)
        val lesson2 = Lesson(2L, Weekday.MONDAY, timeFrom, timeTo, student2.studentId, student2)

        Assert.assertEquals(lesson1.compareTo(lesson2), -1)
    }

    @Test
    fun compare_lessons_by_student_last_name() {
        val timeFrom: LocalTime = LocalTime.parse("16:00", formatter)
        val timeTo: LocalTime = LocalTime.parse("16:30", formatter)

        val student1 = Student(1L, "John", "Smitg", "", "", mutableListOf())
        val student2 = Student(1L, "John", "Smith", "", "", mutableListOf())

        val lesson1 = Lesson(1L, Weekday.MONDAY, timeFrom, timeTo, student1.studentId, student1)
        val lesson2 = Lesson(1L, Weekday.MONDAY, timeFrom, timeTo, student2.studentId, student2)

        Assert.assertEquals(lesson1.compareTo(lesson2), -1)
    }
}