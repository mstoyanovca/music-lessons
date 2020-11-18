package com.mstoyanov.musiclessons.repository

import androidx.room.*
import com.mstoyanov.musiclessons.model.Student
import com.mstoyanov.musiclessons.model.StudentWithPhoneNumbers

@Dao
interface StudentDao {
    @Query("SELECT * FROM student")
    suspend fun findAll2(): MutableList<Student>

    // TODO: remove this:
    @Query("SELECT * FROM student")
    fun findAll(): MutableList<Student>

    @Transaction
    @Query("SELECT * FROM student")
    suspend fun findAllWithPhoneNumbers(): List<StudentWithPhoneNumbers>

    @Insert
    suspend fun insert(student: Student): Long

    @Update
    fun update(student: Student)

    @Delete
    fun delete(student: Student)
}
