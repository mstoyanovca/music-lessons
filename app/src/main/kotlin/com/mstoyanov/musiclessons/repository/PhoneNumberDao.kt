package com.mstoyanov.musiclessons.repository

import androidx.room.*
import com.mstoyanov.musiclessons.model.PhoneNumber

@Dao
interface PhoneNumberDao {
    @Query("select * from phone_number where student_owner_id == :studentId")
    suspend fun findByStudentId(studentId: Long): MutableList<PhoneNumber>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(phoneNumbers: List<PhoneNumber>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(phoneNumber: PhoneNumber): Long

    @Delete
    suspend fun delete(phoneNumber: PhoneNumber)
}
