package com.mstoyanov.musiclessons

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import com.mstoyanov.musiclessons.model.PhoneNumber
import com.mstoyanov.musiclessons.model.PhoneNumberType
import com.mstoyanov.musiclessons.model.Student
import com.mstoyanov.musiclessons.repository.AppDatabase
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException

class PhoneNumberDaoTest {
    private lateinit var db: AppDatabase
    private val phoneNumbers: MutableList<PhoneNumber> = mutableListOf()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        var phoneNumber = PhoneNumber(1L, "123-456-7890", PhoneNumberType.HOME, 1L, false)
        phoneNumbers.add(phoneNumber)
        phoneNumber = PhoneNumber(2L, "333-456-9900", PhoneNumberType.CELL, 1L, false)
        phoneNumbers.add(phoneNumber)

        val student = Student(1L, "John", "Smith", "jsmith@google.com", "Test student", phoneNumbers)
        db.studentDao.insert(student)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert_phone_numbers() {
        db.phoneNumberDao.insertAll(phoneNumbers)
        val actualPhoneNumbers = db.phoneNumberDao.findAllByStudentId(1L)

        Assert.assertEquals(actualPhoneNumbers.size, 2)
        Assert.assertEquals(phoneNumbers[0], actualPhoneNumbers[0])
        Assert.assertEquals(phoneNumbers[1], actualPhoneNumbers[1])
    }

    @Test
    @Throws(Exception::class)
    fun insert_phone_number() {
        db.phoneNumberDao.insert(phoneNumbers[0])
        val actualPhoneNumbers = db.phoneNumberDao.findAllByStudentId(1L)

        Assert.assertEquals(actualPhoneNumbers.size, 1)
        Assert.assertEquals(phoneNumbers[0], actualPhoneNumbers[0])
    }

    @Test
    @Throws(Exception::class)
    fun update_phone_number() {
        db.phoneNumberDao.insertAll(phoneNumbers)
        var actualPhoneNumbers = db.phoneNumberDao.findAllByStudentId(1L)

        actualPhoneNumbers[0].number = "123-456-7899"
        db.phoneNumberDao.insertAll(actualPhoneNumbers)

        actualPhoneNumbers = db.phoneNumberDao.findAllByStudentId(1L)

        Assert.assertEquals("123-456-7899", actualPhoneNumbers[0].number)
    }

    @Test
    @Throws(Exception::class)
    fun delete_phone_number() {
        db.phoneNumberDao.insertAll(phoneNumbers)
        db.phoneNumberDao.delete(phoneNumbers[0])

        val actualPhoneNumbers = db.phoneNumberDao.findAllByStudentId(1L)

        Assert.assertEquals(actualPhoneNumbers.size, 1)
        Assert.assertEquals(phoneNumbers[1], actualPhoneNumbers[0])
    }
}