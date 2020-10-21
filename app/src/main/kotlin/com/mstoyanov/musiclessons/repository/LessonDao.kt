package com.mstoyanov.musiclessons.repository

import androidx.room.*
import com.mstoyanov.musiclessons.model.Lesson
import com.mstoyanov.musiclessons.model.LessonWithStudent

@Dao
interface LessonDao {
    @Transaction
    @Query("select * from lesson where lesson.weekday == :weekday")
    suspend fun findAllWithStudentByWeekday(weekday: String): MutableList<LessonWithStudent>

    @Insert
    fun insert(lesson: Lesson)

    @Update
    fun update(lesson: Lesson)

    @Delete
    fun delete(lesson: Lesson)
}
