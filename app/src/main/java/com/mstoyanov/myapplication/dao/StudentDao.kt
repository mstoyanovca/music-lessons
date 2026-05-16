package com.mstoyanov.myapplication.dao

import com.mstoyanov.myapplication.entity.PhoneNumber
import com.mstoyanov.myapplication.entity.PhoneNumberType
import com.mstoyanov.myapplication.entity.Student

object StudentDao {
    private val students = mutableListOf(
        Student(
            studentId = 1L,
            firstName = "Martin",
            lastName = "Stoyanov",
            notes = "Clarinet, beginner",
            phoneNumbers = mutableListOf(
                PhoneNumber(phoneNumberId = 1L, number = "6478804399", type = PhoneNumberType.CELL, studentId = 1L, isValid = true),
                PhoneNumber(phoneNumberId = 4L, number = "2223334567", type = PhoneNumberType.HOME, studentId = 1L, isValid = true),
                PhoneNumber(phoneNumberId = 5L, number = "2223338888", type = PhoneNumberType.WORK, studentId = 1L, isValid = true),
                PhoneNumber(phoneNumberId = 6L, number = "2224569999", type = PhoneNumberType.OTHER, studentId = 1L, isValid = true)
            )
        ),
        Student(
            studentId = 2L,
            firstName = "Daniel",
            lastName = "Stoyanov",
            notes = "Piano, intermediate",
            phoneNumbers = mutableListOf(PhoneNumber(phoneNumberId = 2L, number = "9054626476", type = PhoneNumberType.CELL, studentId = 2L, isValid = true))
        ),
        Student(
            studentId = 3L,
            firstName = "Stela",
            lastName = "Stoyanova",
            notes = "Flute, advanced",
            phoneNumbers = mutableListOf(PhoneNumber(phoneNumberId = 3L, number = "9054624722", type = PhoneNumberType.CELL, studentId = 3L, isValid = true))
        ),
        Student(
            studentId = 4L,
            firstName = "John",
            lastName = "Smith",
            notes = "",
            phoneNumbers = mutableListOf(PhoneNumber(phoneNumberId = 7L, number = "9052223333", type = PhoneNumberType.CELL, studentId = 4L, isValid = true))
        ),
        Student(
            studentId = 5L,
            firstName = "Samantha",
            lastName = "Fox",
            notes = "Empty phone numbers list for testing",
            phoneNumbers = mutableListOf()
        ),
        Student(
            studentId = 6L,
            firstName = "Samantha",
            lastName = "",
            notes = "",
            phoneNumbers = mutableListOf()
        ),
        Student(
            studentId = 7L,
            firstName = "",
            lastName = "Fox",
            notes = "",
            phoneNumbers = mutableListOf()
        ),
        Student(
            studentId = 8L,
            firstName = "Damandjanchakraputrathehgreatkilimandjaroclimber",
            lastName = "Huyumandjurparkanistankirkardirmaduristanmuharadja",
            notes = "If you are looking to limit the number of characters a user can type into an input field like a TextField there is no direct maxLength parameter.",
            phoneNumbers = mutableListOf()
        )
    )

    fun findAll(): MutableList<Student> {
        return students.sorted() as MutableList<Student>
    }
}
