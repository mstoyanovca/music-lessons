package com.mstoyanov.musiclessons

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mstoyanov.musiclessons.global.Functions.formatter
import com.mstoyanov.musiclessons.model.Lesson
import com.mstoyanov.musiclessons.model.LessonWithStudent
import com.mstoyanov.musiclessons.model.Student
import com.mstoyanov.musiclessons.model.Weekday
import com.mstoyanov.musiclessons.repository.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class LessonDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var student: Student
    private lateinit var lesson: Lesson

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        student = Student(1L, "John", "Smith", "Test student", mutableListOf())
        runBlocking { db.studentDao.insert(student) }

        val timeFrom: LocalTime = LocalTime.parse("16:00", formatter)
        val timeTo: LocalTime = LocalTime.parse("16:30", formatter)
        lesson = Lesson(1L, Weekday.MONDAY, timeFrom, timeTo, student.studentId, student)
        runBlocking { db.lessonDao.insert(lesson) }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert_lesson() {
        val actualLessonWithStudent: List<LessonWithStudent> = runBlocking { db.lessonDao.findWithStudentByWeekday("Monday") }
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
        runBlocking { db.lessonDao.update(lesson) }

        var actualLessonWithStudent = runBlocking { db.lessonDao.findWithStudentByWeekday("Monday") }
        val actualLesson = actualLessonWithStudent[0].lesson
        assertEquals(LocalTime.parse("16:45", formatter), actualLesson.timeTo)

        runBlocking { db.lessonDao.delete(lesson) }
        actualLessonWithStudent = runBlocking { db.lessonDao.findWithStudentByWeekday("Monday") }
        assertEquals(actualLessonWithStudent.size, 0)
    }

    @Test
    @Throws(Exception::class)
    fun delete_lesson() {
        runBlocking { db.lessonDao.delete(lesson) }
        val actualLessonWithStudent = runBlocking { db.lessonDao.findWithStudentByWeekday("Monday") }
        assertEquals(actualLessonWithStudent.size, 0)
    }
}
