package com.mstoyanov.musiclessons.repository;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.mstoyanov.musiclessons.model.PhoneNumber;

import java.util.List;

@Dao
public interface PhoneNumberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PhoneNumber> phoneNumbers);

    @Query("select * from phone_number where student_id = :studentId")
    List<PhoneNumber> findAllByStudentId(long studentId);

    @Delete
    void delete(PhoneNumber phoneNumber);
}