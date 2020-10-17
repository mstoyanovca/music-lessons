package com.mstoyanov.musiclessons.model

import androidx.room.Embedded
import androidx.room.Relation

class StudentWithPhoneNumbers {
    @Embedded
    lateinit var student: Student
    @Relation(parentColumn = "s_id", entityColumn = "student_id")
    lateinit var phoneNumbers: List<PhoneNumber>
}