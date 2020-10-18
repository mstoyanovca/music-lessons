package com.mstoyanov.musiclessons

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mstoyanov.musiclessons.model.Lesson
import com.mstoyanov.musiclessons.model.LessonWithStudent
import com.mstoyanov.musiclessons.model.Student
import com.mstoyanov.musiclessons.model.Weekday
import com.mstoyanov.musiclessons.repository.AppDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class LessonDaoTest {
    private lateinit var formatter: DateTimeFormatter
    private lateinit var db: AppDatabase
    private lateinit var student: Student
    private lateinit var lesson: Lesson

    @Before
    fun createDb() {
        formatter = DateTimeFormatter.ofPattern("HH:mm")

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        student = Student(1L, "John", "Smith", "jsmith@google.com", "Test student", mutableListOf())
        db.studentDao.insert(student)

        val timeFrom: LocalTime = LocalTime.parse("16:00", formatter)
        val timeTo: LocalTime = LocalTime.parse("16:30", formatter)
        lesson = Lesson(1L, Weekday.MONDAY, timeFrom, timeTo, student.studentId, student)
        db.lessonDao.insert(lesson)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert_lesson() {
        val actualLessonWithStudent: MutableList<LessonWithStudent> = db.lessonDao.findAllWithStudentByWeekday("Monday")
        val actualStudent = actualLessonWithStudent[0].student
        val actualLesson = actualLessonWithStudent[0].lesson
        actualLesson.student = actualStudent

        assertEquals(actualLessonWithStudent.size, 1)
        assertEquals(lesson, actualLesson)
        assertEquals(student, actualStudent)
    }

    @Test
    @Throws(Exception::class)
    fun update_lesson() {
        lesson.timeTo = LocalTime.parse("16:45", formatter)
        db.lessonDao.update(lesson)

        var actualLessonWithStudent = db.lessonDao.findAllWithStudentByWeekday("Monday")
        val actualLesson = actualLessonWithStudent[0].lesson
        assertEquals(LocalTime.parse("16:45", formatter), actualLesson.timeTo)

        db.lessonDao.delete(lesson)
        actualLessonWithStudent = db.lessonDao.findAllWithStudentByWeekday("Monday")
        assertEquals(actualLessonWithStudent.size, 0)
    }

    @Test
    @Throws(Exception::class)
    fun delete_lesson() {
        db.lessonDao.delete(lesson)
        val actualLessonWithStudent = db.lessonDao.findAllWithStudentByWeekday("Monday")
        assertEquals(actualLessonWithStudent.size, 0)
    }
}
