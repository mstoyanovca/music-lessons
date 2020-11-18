package com.mstoyanov.musiclessons.repository

import androidx.room.*
import com.mstoyanov.musiclessons.model.PhoneNumber

@Dao
interface PhoneNumberDao {
    // TODO: remove
    @Query("select * from phone_number where student_owner_id == :studentId")
    fun findAllByStudentId(studentId: Long): MutableList<PhoneNumber>

    @Query("select * from phone_number where student_owner_id == :studentId")
    suspend fun findAllByStudentId2(studentId: Long): MutableList<PhoneNumber>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(phoneNumbers: List<PhoneNumber>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(phoneNumber: PhoneNumber): Long

    @Delete
    fun delete(phoneNumber: PhoneNumber)
}
