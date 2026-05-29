package com.mstoyanov.myapplication.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.mstoyanov.myapplication.entity.PhoneNumber

@Dao
interface PhoneNumberDao {
    @Insert
    suspend fun insertAll(phoneNumbers: List<PhoneNumber>)

    @Upsert
    suspend fun upsertAll(phoneNumbers: List<PhoneNumber>)

    @Query("delete from phone_number where phone_number_id in (:phoneNumberIds)")
    suspend fun deleteByIds(phoneNumberIds: List<Long>)
}
