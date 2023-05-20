package com.mstoyanov.musiclessons

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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
class PhoneNumberDaoTest {
    private lateinit var db: AppDatabase
    private val phoneNumbers: MutableList<PhoneNumber> = mutableListOf()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        var phoneNumber = PhoneNumber(1L, "123-456-7890", PhoneNumberType.HOME, 1L, false)
        phoneNumbers.add(phoneNumber)
        phoneNumber = PhoneNumber(2L, "333-456-9900", PhoneNumberType.CELL, 1L, false)
        phoneNumbers.add(phoneNumber)

        val student = Student(1L, "John", "Smith", "Test student", phoneNumbers)
        runBlocking { db.studentDao.insert(student) }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert_phone_numbers() {
        val actualPhoneNumbers: List<PhoneNumber>
        runBlocking {
            db.phoneNumberDao.insertAll(phoneNumbers)
            actualPhoneNumbers = db.phoneNumberDao.findByStudentId(1L)
        }

        Assert.assertEquals(actualPhoneNumbers.size, 2)
        Assert.assertEquals(phoneNumbers[0], actualPhoneNumbers[0])
        Assert.assertEquals(phoneNumbers[1], actualPhoneNumbers[1])
    }

    @Test
    @Throws(Exception::class)
    fun insert_phone_number() {
        val actualPhoneNumbers: List<PhoneNumber>
        runBlocking {
            db.phoneNumberDao.insert(phoneNumbers[0])
            actualPhoneNumbers = db.phoneNumberDao.findByStudentId(1L)
        }

        Assert.assertEquals(actualPhoneNumbers.size, 1)
        Assert.assertEquals(phoneNumbers[0], actualPhoneNumbers[0])
    }

    @Test
    @Throws(Exception::class)
    fun update_phone_number() {
        var actualPhoneNumbers: List<PhoneNumber>
        runBlocking {
            db.phoneNumberDao.insertAll(phoneNumbers)
            actualPhoneNumbers = db.phoneNumberDao.findByStudentId(1L)
        }

        actualPhoneNumbers[0].number = "123-456-7899"
        runBlocking {
            db.phoneNumberDao.insertAll(actualPhoneNumbers)
            actualPhoneNumbers = db.phoneNumberDao.findByStudentId(1L)
        }

        Assert.assertEquals("123-456-7899", actualPhoneNumbers[0].number)
    }

    @Test
    @Throws(Exception::class)
    fun delete_phone_number() {
        val actualPhoneNumbers: List<PhoneNumber>
        runBlocking {
            db.phoneNumberDao.insertAll(phoneNumbers)
            db.phoneNumberDao.delete(phoneNumbers[0])
            actualPhoneNumbers = db.phoneNumberDao.findByStudentId(1L)
        }


        Assert.assertEquals(actualPhoneNumbers.size, 1)
        Assert.assertEquals(phoneNumbers[1], actualPhoneNumbers[0])
    }
}
