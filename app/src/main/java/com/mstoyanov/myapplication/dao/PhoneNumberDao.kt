package com.mstoyanov.myapplication.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Upsert
import com.mstoyanov.myapplication.entity.PhoneNumber

@Dao
interface PhoneNumberDao {
    @Insert
    suspend fun insertAll(phoneNumbers: List<PhoneNumber>): List<Long>

    @Upsert
    suspend fun upsertAll(phoneNumbers: List<PhoneNumber>): List<Long>

    @Delete
    suspend fun deleteAll(phoneNumbers: List<PhoneNumber>)
}
