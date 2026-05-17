package com.mstoyanov.myapplication.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mstoyanov.myapplication.entity.PhoneNumber
import kotlinx.coroutines.flow.Flow

@Dao
interface PhoneNumberDao {
    @Query("select * from phone_number where student_owner_id == :studentId")
    suspend fun findByStudentId(studentId: Long): Flow<MutableList<PhoneNumber>>

    @Insert
    suspend fun insert(phoneNumber: PhoneNumber): Long

    @Insert
    suspend fun insertAll(phoneNumbers: List<PhoneNumber>): List<Long>

    @Delete
    suspend fun delete(phoneNumber: PhoneNumber)
}
