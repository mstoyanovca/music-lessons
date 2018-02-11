package com.mstoyanov.musiclessons.repository

import android.arch.persistence.room.*
import com.mstoyanov.musiclessons.model.PhoneNumber

@Dao
interface PhoneNumberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(phoneNumbers: List<PhoneNumber>)

    @Query("select * from phone_number where student_id = :arg0")
    fun findAllByStudentId(studentId: Long): MutableList<PhoneNumber>

    @Delete
    fun delete(phoneNumber: PhoneNumber)
}