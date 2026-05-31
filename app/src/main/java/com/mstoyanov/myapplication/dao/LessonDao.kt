package com.mstoyanov.myapplication.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mstoyanov.myapplication.entity.Lesson
import com.mstoyanov.myapplication.entity.PhoneNumber
import com.mstoyanov.myapplication.entity.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface LessonDao {
    fun findById(id: Long): Flow<Lesson> {
        return findLessonById(id)
            .map { map: Map<Lesson, Student> ->
                map.entries.map { (lesson, student) ->
                    lesson.student = student
                    lesson
                }.first()
            }
    }

    @Query(
        "select * from lesson " +
                "join student on lesson.student_id = student.id " +
                "where lesson.id == :id"
    )
    fun findLessonById(id: Long): Flow<Map<Lesson, Student>>

    fun findByWeekday(weekday: String): Flow<List<Lesson>> {
        return findLessonsByWeekday(weekday)
            .map { map ->
                map.entries.flatMap { (lesson, studentToPhoneNumbers) ->
                    studentToPhoneNumbers.entries.map { (student, phoneNumbers) ->
                        lesson.student = student.copy(phoneNumbers = phoneNumbers)
                        lesson
                    }
                }
            }
            .map { it.sorted() }
    }

    @Query(
        "select * from lesson " +
                "join student on lesson.student_id = student.id " +
                "left join phone_number on student.id = phone_number.student_id " +
                "where lesson.weekday == :weekday"
    )
    fun findLessonsByWeekday(weekday: String): Flow<Map<Lesson, Map<Student, List<PhoneNumber>>>>

    @Insert
    suspend fun insert(lesson: Lesson)

    @Update
    suspend fun update(lesson: Lesson)

    @Delete
    suspend fun delete(lesson: Lesson)
}
