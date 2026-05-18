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

@Dao
interface StudentDao {
    @Query("select * from student join phone_number on student.student_id = phone_number.student_owner_id")
    suspend fun findAllStudents(): Flow<Map<Student, List<PhoneNumber>>>

    @Transaction
    @Insert
    suspend fun insert(student: Student, context: Context): List<Long> {
        val studentId = insertStudent(student)
        val phoneNumbers = student.phoneNumbers.map { it.copy(studentId = studentId) }
        return MusicLessonsApplication.db.phoneNumberDao().insertAll(phoneNumbers)
    }

    @Insert
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun update(student: Student)

    @Update
    suspend fun updateWithPhoneNumbers(student: Student, phoneNumbers: List<PhoneNumber>) {

    }

    @Delete
    suspend fun delete(student: Student)
}
