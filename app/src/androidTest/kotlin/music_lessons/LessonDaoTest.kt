package music_lessons

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import music_lessons.global.Functions.dateTimeFormatter
import music_lessons.model.Lesson
import music_lessons.model.LessonWithStudent
import music_lessons.model.Student
import music_lessons.model.Weekday
import music_lessons.repository.AppDatabase
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

        val timeFrom: LocalTime = LocalTime.parse("16:00", dateTimeFormatter)
        val timeTo: LocalTime = LocalTime.parse("16:30", dateTimeFormatter)
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

        assertEquals(1, actualLessonWithStudent.size)
        assertEquals(lesson, actualLesson)
        assertEquals(student, actualStudent)
    }

    @Test
    @Throws(Exception::class)
    fun update_lesson() {
        lesson.timeTo = LocalTime.parse("16:45", dateTimeFormatter)
        runBlocking { db.lessonDao.update(lesson) }

        var actualLessonWithStudent = runBlocking { db.lessonDao.findWithStudentByWeekday("Monday") }
        val actualLesson = actualLessonWithStudent[0].lesson
        assertEquals(LocalTime.parse("16:45", dateTimeFormatter), actualLesson.timeTo)

        runBlocking { db.lessonDao.delete(lesson) }
        actualLessonWithStudent = runBlocking { db.lessonDao.findWithStudentByWeekday("Monday") }
        assertEquals(0, actualLessonWithStudent.size)
    }

    @Test
    @Throws(Exception::class)
    fun delete_lesson() {
        runBlocking { db.lessonDao.delete(lesson) }
        val actualLessonWithStudent = runBlocking { db.lessonDao.findWithStudentByWeekday("Monday") }
        assertEquals(0, actualLessonWithStudent.size)
    }
}
