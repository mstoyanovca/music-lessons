package com.mstoyanov.musiclessons

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mstoyanov.musiclessons.model.PhoneNumber
import com.mstoyanov.musiclessons.model.PhoneNumberType
import com.mstoyanov.musiclessons.model.Student
import com.mstoyanov.musiclessons.model.StudentWithPhoneNumbers
import com.mstoyanov.musiclessons.repository.AppDatabase
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
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        phoneNumbers = mutableListOf(PhoneNumber(1L, "123-456-7890", PhoneNumberType.HOME, 1L, isValid = false))
        student = Student(1L, "John", "Smith", "jsmith@google.com", "Test student", mutableListOf())

        db.studentDao.insert(student)
        db.phoneNumberDao.insertAll(phoneNumbers)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun find_all_with_phone_numbers() {
        val actualStudentsWithPhoneNumbers: List<StudentWithPhoneNumbers> = db.studentDao.findAllWithPhoneNumbers()

        Assert.assertEquals(actualStudentsWithPhoneNumbers.size, 1)
        Assert.assertEquals(student, actualStudentsWithPhoneNumbers[0].student)
        Assert.assertEquals(phoneNumbers, actualStudentsWithPhoneNumbers[0].phoneNumbers)
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
