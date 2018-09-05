package com.mstoyanov.musiclessons.repository

import android.arch.persistence.room.*
import com.mstoyanov.musiclessons.model.PhoneNumber

@Dao
interface PhoneNumberDao {

    @Query("select * from phone_number where student_id == :studentId")
    fun findAllByStudentId(studentId: Long): MutableList<PhoneNumber>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(phoneNumbers: List<PhoneNumber>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(phoneNumber: PhoneNumber): Long

    @Delete
    fun delete(phoneNumber: PhoneNumber)
}