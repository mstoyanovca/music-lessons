package com.mstoyanov.musiclessons.model

import android.arch.persistence.room.Embedded

class StudentPhoneNumber {
    @Embedded
    lateinit var student: Student
    @Embedded
    lateinit var phoneNumber: PhoneNumber
}