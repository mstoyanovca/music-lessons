package com.mstoyanov.myapplication.dao

import android.content.Context
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
import kotlinx.coroutines.flow.flow

@Dao
interface StudentDao {
    @Transaction
    suspend fun findAll(): Flow<List<Student>> {
        return flow { findAllStudents().map { it.key.copy(phoneNumbers = it.value) } }
    }

    @Query("select * from student join phone_number on student.student_id = phone_number.student_owner_id")
    suspend fun findAllStudents(): Map<Student, List<PhoneNumber>>

    @Transaction
    suspend fun insert(student: Student, context: Context): List<Long> {
        val studentId = insertStudent(student)
        val phoneNumbers = student.phoneNumbers.map { it.copy(studentId = studentId) }
        return MusicLessonsApplication.db.phoneNumberDao().insertAll(phoneNumbers)
    }

    @Insert
    suspend fun insertStudent(student: Student): Long

    @Transaction
    suspend fun update(student: Student, deletedPhoneNumbers: List<PhoneNumber>) {
        updateStudent(student)
        MusicLessonsApplication.db.phoneNumberDao().deleteAll(deletedPhoneNumbers)
        MusicLessonsApplication.db.phoneNumberDao().upsertAll(student.phoneNumbers)
    }

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun delete(student: Student)
}
