package com.mstoyanov.musiclessons

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import com.mstoyanov.musiclessons.model.Student
import com.mstoyanov.musiclessons.repository.AppDatabase
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class StudentDaoTest {
    private lateinit var format: SimpleDateFormat
    private lateinit var db: AppDatabase
    private lateinit var student: Student

    @Before
    fun createDb() {
        format = SimpleDateFormat("HH:mm", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")

        val context = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        student = Student(1L, "John", "Smith", "jsmith@google.com", "Test student", mutableListOf())
        db.studentDao.insert(student)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert_student() {
        val actualStudent = db.studentDao.findAll()

        Assert.assertEquals(actualStudent.size, 1)
        Assert.assertEquals(student, actualStudent[0])
    }

    @Test
    @Throws(Exception::class)
    fun update_student() {
        student.firstName = "Joane"
        db.studentDao.update(student)

        val actualStudent = db.studentDao.findAll()[0]
        Assert.assertEquals("Joane", actualStudent.firstName)
    }

    @Test
    @Throws(Exception::class)
    fun delete_student() {
        db.studentDao.delete(student)
        val actualStudent = db.studentDao.findAll()
        Assert.assertEquals(actualStudent.size, 0)
    }
}