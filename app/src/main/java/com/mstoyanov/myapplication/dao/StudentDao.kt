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
import com.mstoyanov.myapplication.entity.StudentWithPhoneNumbers
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Transaction
    @Query("select * from student")
    suspend fun findAllWithPhoneNumbers(): Flow<List<StudentWithPhoneNumbers>>

    @Insert
    suspend fun insert(student: Student): Long

    @Transaction
    @Insert
    suspend fun insertWithPhoneNumbers(studentWithPhoneNumbers: StudentWithPhoneNumbers, context: Context): List<Long> {
        val studentId = insert(studentWithPhoneNumbers.student)
        val phoneNumbers = studentWithPhoneNumbers.phoneNumbers.map { it.copy(studentId = studentId) }
        return MusicLessonsApplication.db.phoneNumberDao().insertAll(phoneNumbers)
    }

    @Update
    suspend fun update(student: Student)

    @Update
    suspend fun updateWithPhoneNumbers(student: Student, phoneNumbers: List<PhoneNumber>) {

    }

    @Delete
    suspend fun delete(student: Student)
}
