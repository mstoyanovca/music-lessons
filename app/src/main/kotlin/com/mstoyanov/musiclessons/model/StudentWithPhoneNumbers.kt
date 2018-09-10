package com.mstoyanov.musiclessons.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

class StudentWithPhoneNumbers {
    @Embedded
    lateinit var student: Student
    @Relation(parentColumn = "s_id", entityColumn = "student_id")
    lateinit var phoneNumbers: List<PhoneNumber>
}