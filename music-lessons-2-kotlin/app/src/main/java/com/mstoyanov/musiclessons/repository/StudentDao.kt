package com.mstoyanov.musiclessons.repository

import android.arch.persistence.room.*
import com.mstoyanov.musiclessons.model.Student

@Dao
interface StudentDao {

    @Query("SELECT * FROM student")
    fun findAll(): MutableList<Student>

    @Insert
    fun insert(student: Student): Long

    @Update
    fun update(student: Student)

    @Delete
    fun delete(student: Student)
}