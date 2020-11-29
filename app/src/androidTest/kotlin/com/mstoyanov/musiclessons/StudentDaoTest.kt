package com.mstoyanov.musiclessons

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mstoyanov.musiclessons.model.PhoneNumber
import com.mstoyanov.musiclessons.model.PhoneNumberType
import com.mstoyanov.musiclessons.model.Student
import com.mstoyanov.musiclessons.repository.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class StudentDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var student: Student
    private lateinit var phoneNumbers: List<PhoneNumber>

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        phoneNumbers = mutableListOf(PhoneNumber(1L, "123-456-7890", PhoneNumberType.HOME, 1L, isValid = false))
        student = Student(1L, "John", "Smith", "jsmith@google.com", "Test student", phoneNumbers as MutableList<PhoneNumber>)

        runBlocking {
            db.studentDao.insert(student)
            db.phoneNumberDao.insertAll(phoneNumbers)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun find_all_with_phone_numbers() {
        val actualStudentsWithPhoneNumbers: List<Student> = runBlocking { db.studentDao.findAllWithPhoneNumbers() }

        Assert.assertEquals(actualStudentsWithPhoneNumbers.size, 1)
        Assert.assertEquals(student, actualStudentsWithPhoneNumbers[0])
        Assert.assertEquals(phoneNumbers, actualStudentsWithPhoneNumbers[0].phoneNumbers)
    }

    @Test
    @Throws(Exception::class)
    fun insert_student() {
        val actualStudents: List<Student>
        runBlocking { actualStudents = db.studentDao.findAll() }
        student.phoneNumbers = mutableListOf()

        Assert.assertEquals(actualStudents.size, 1)
        Assert.assertEquals(student, actualStudents[0])
    }

    @Test
    @Throws(Exception::class)
    fun update_student() {
        student.firstName = "Joane"
        val actualStudent: Student

        runBlocking {
            db.studentDao.update(student)
            actualStudent = db.studentDao.findAll()[0]
        }

        Assert.assertEquals("Joane", actualStudent.firstName)
    }

    @Test
    @Throws(Exception::class)
    fun delete_student() {
        val actualStudents: List<Student>

        runBlocking {
            db.studentDao.delete(student)
            actualStudents = db.studentDao.findAll()
        }

        Assert.assertEquals(actualStudents.size, 0)
    }
}
