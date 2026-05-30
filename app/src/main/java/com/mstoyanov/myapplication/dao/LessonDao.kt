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
    fun findById(lessonId: Long): Flow<Lesson> {
        return findLessonById(lessonId)
            .map { map: Map<Lesson, Student> ->
                map.entries.map { (lesson, student) ->
                    lesson.copy(student = student)
                }.first()
            }
    }

    @Query(
        "select * from lesson " +
                "join student on lesson.student_owner_id = student.student_id " +
                "where lesson.lesson_id == :lessonId"
    )
    fun findLessonById(lessonId: Long): Flow<Map<Lesson, Student>>

    fun findByWeekday(weekday: String): Flow<List<Lesson>> {
        return findLessonsByWeekday(weekday)
            .map { map ->
                map.entries.flatMap { (lesson, studentToPhoneNumbers) ->
                    studentToPhoneNumbers.entries.map { (student, phoneNumbers) ->
                        lesson.copy(student = student.copy(phoneNumbers = phoneNumbers))
                    }
                }
            }
            .map { it.sorted() }
    }

    @Query(
        "select * from lesson " +
                "join student on lesson.student_owner_id = student.student_id " +
                "left join phone_number on student.student_id = phone_number.student_owner_id " +
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
