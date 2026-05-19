package com.mstoyanov.myapplication.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mstoyanov.myapplication.entity.Lesson
import com.mstoyanov.myapplication.entity.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface LessonDao {
    fun findByWeekday(weekday: String): Flow<List<Lesson>> {
        return findLessonsByWeekday(weekday).map { map -> map.entries.map { it.key.copy(student = it.value) } }
    }

    @Query("select * from lesson join student on lesson.student_owner_id = student.student_id where lesson.weekday == :weekday")
    fun findLessonsByWeekday(weekday: String): Flow<Map<Lesson, Student>>

    @Insert
    suspend fun insert(lesson: Lesson)

    @Update
    suspend fun update(lesson: Lesson)

    @Delete
    suspend fun delete(lesson: Lesson)
}
