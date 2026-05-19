package com.mstoyanov.myapplication.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mstoyanov.myapplication.MusicLessonsApplication
import com.mstoyanov.myapplication.entity.PhoneNumber
import com.mstoyanov.myapplication.entity.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface StudentDao {
    fun findAll(): Flow<List<Student>> {
        return findAllStudents().map { map -> map.entries.map { (student, phoneNumbers) -> student.copy(phoneNumbers = phoneNumbers) } }
    }

    @Query("select * from student join phone_number on student.student_id = phone_number.student_owner_id")
    fun findAllStudents(): Flow<Map<Student, List<PhoneNumber>>>

    @Transaction
    suspend fun insert(student: Student) {
        val studentId = insertStudent(student)
        val phoneNumbers = student.phoneNumbers.map { it.copy(studentId = studentId) }
        return MusicLessonsApplication.db.phoneNumberDao().insertAll(phoneNumbers)
    }

    @Insert
    suspend fun insertStudent(student: Student): Long

    @Transaction
    suspend fun update(student: Student) {
        updateStudent(student)
        MusicLessonsApplication.db.phoneNumberDao().deleteAll(student.phoneNumbers)
        MusicLessonsApplication.db.phoneNumberDao().upsertAll(student.phoneNumbers)
    }

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun delete(student: Student)
}
