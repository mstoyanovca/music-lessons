package com.mstoyanov.myapplication.entity

import androidx.room.Embedded
import androidx.room.Relation

data class StudentWithPhoneNumbers(
    @Embedded val student: Student,
    @Relation(parentColumn = "student_id", entityColumn = "student_owner_id")
    val phoneNumbers: List<PhoneNumber>
)
