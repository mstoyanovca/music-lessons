package com.mstoyanov.musiclessons.repository

import android.arch.persistence.room.*
import com.mstoyanov.musiclessons.model.Lesson
import com.mstoyanov.musiclessons.model.LessonWithStudent

@Dao
interface LessonDao {

    @Query("select * from lesson " +
            "inner join student on lesson.student_id == student.s_id " +
            "where lesson.weekday == :weekday")
    fun findAllWithStudentByWeekday(weekday: String): MutableList<LessonWithStudent>

    @Insert
    fun insert(lesson: Lesson)

    @Update
    fun update(lesson: Lesson)

    @Delete
    fun delete(lesson: Lesson)
}