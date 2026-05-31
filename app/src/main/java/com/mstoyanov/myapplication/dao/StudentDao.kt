package com.mstoyanov.myapplication.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mstoyanov.myapplication.MusicLessonsApplication.Companion.db
import com.mstoyanov.myapplication.entity.PhoneNumber
import com.mstoyanov.myapplication.entity.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface StudentDao {
    fun findById(studentId: Long): Flow<Student> {
        return findStudentById(studentId)
            .map { map ->
                map.entries.map { (student, phoneNumbers) ->
                    student.copy(phoneNumbers = phoneNumbers)
                }.first()
            }
    }

    @Query("select * from student left join phone_number on student.id = phone_number.student_id where student_id = :studentId")
    fun findStudentById(studentId: Long): Flow<Map<Student, List<PhoneNumber>>>

    fun findAll(): Flow<List<Student>> {
        return findAllStudents()
            .map { map ->
                map.entries.map { (student, phoneNumbers) ->
                    student.copy(phoneNumbers = phoneNumbers)
                }
            }
            .map { it.sorted() }
    }

    @Query("select * from student left join phone_number on student.id = phone_number.student_id")
    fun findAllStudents(): Flow<Map<Student, List<PhoneNumber>>>

    @Transaction
    suspend fun insert(student: Student) {
        val studentId = insertStudent(student)
        val phoneNumbers = student.phoneNumbers.map { it.copy(studentId = studentId) }
        db.phoneNumberDao().insertAll(phoneNumbers)
    }

    @Insert
    suspend fun insertStudent(student: Student): Long

    @Transaction
    suspend fun update(student: Student, phoneNumberIdsBeforeEditing: List<Long>) {
        updateStudent(student)
        db.phoneNumberDao().deleteByIds(phoneNumberIdsBeforeEditing - student.phoneNumbers.map { it.studentId }.toSet())
        db.phoneNumberDao().upsertAll(student.phoneNumbers.map { it.copy(studentId = student.id) })
    }

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun delete(student: Student)
}
