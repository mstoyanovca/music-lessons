package com.mstoyanov.myapplication.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mstoyanov.myapplication.MusicLessonsApplication.Companion.db
import com.mstoyanov.myapplication.entity.Lesson
import com.mstoyanov.myapplication.entity.PhoneNumber
import com.mstoyanov.myapplication.entity.Student
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

@Dao
interface LessonDao {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun findById(lessonId: Long): Flow<Lesson?> {
        return findLessonById(lessonId).flatMapConcat { l: Lesson? ->
            db.studentDao().findById(l?.studentId ?: 0).map { s: Student? -> l?.copy(student = s ?: Student()) }
        }
    }

    @Query("select * from lesson where lesson_id = :lessonId")
    fun findLessonById(lessonId: Long): Flow<Lesson?>

    fun findByWeekday(weekday: String): Flow<List<Lesson>> {
        return findLessonsByWeekday(weekday)
            .map { map ->
                map.entries.map { it ->
                    val lesson = it.key
                    val student = it.value.entries.map { it.key }.first()
                    val phoneNumbers = it.value.entries.flatMap { it.value }
                    lesson.copy(student = student.copy(phoneNumbers = phoneNumbers))
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
