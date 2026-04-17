package com.mstoyanov.music_lessons.repository

import androidx.room.*
import com.mstoyanov.music_lessons.model.Lesson
import com.mstoyanov.music_lessons.model.LessonWithStudent

@Dao
interface LessonDao {
    @Transaction
    @Query("select lesson.*, student.* from lesson inner join student on lesson.student_owner_id == student.student_id where lesson.weekday == :weekday")
    suspend fun findWithStudentByWeekday(weekday: String): List<LessonWithStudent>

    @Insert
    suspend fun insert(lesson: Lesson)

    @Update
    suspend fun update(lesson: Lesson)

    @Delete
    suspend fun delete(lesson: Lesson)
}
