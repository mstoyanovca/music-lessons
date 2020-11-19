package com.mstoyanov.musiclessons.repository

import androidx.room.*
import com.mstoyanov.musiclessons.MusicLessonsApplication
import com.mstoyanov.musiclessons.model.PhoneNumber
import com.mstoyanov.musiclessons.model.Student
import com.mstoyanov.musiclessons.model.StudentWithPhoneNumbers

@Dao
abstract class StudentDao {
    @Query("select * from student")
    abstract suspend fun findAll2(): MutableList<Student>

    // TODO: remove this:
    @Query("SELECT * FROM student")
    abstract fun findAll(): MutableList<Student>

    @Transaction
    @Query("select * from student")
    abstract suspend fun findAllStudentWithPhoneNumbers(): List<StudentWithPhoneNumbers>

    open suspend fun findAllWithPhoneNumbers(): List<Student> {
        val studentsWithPhoneNumbers = findAllStudentWithPhoneNumbers()
        studentsWithPhoneNumbers.forEach { it.student.phoneNumbers = it.phoneNumbers.toMutableList() }
        return studentsWithPhoneNumbers.map { it.student }.sorted()
    }

    @Insert
    abstract suspend fun insert(student: Student): Long

    @Transaction
    open suspend fun insertWithPhoneNumbers(student: Student) {
        val id = insert(student)
        student.studentId = id
        student.phoneNumbers.forEach { it.studentId = id }
        MusicLessonsApplication.db.phoneNumberDao.insertAll(student.phoneNumbers)
    }

    @Update
    abstract suspend fun update(student: Student)

    @Transaction
    open suspend fun updateStudentWithPhoneNumbers(student: Student, phoneNumbersBeforeEditing: List<PhoneNumber>): Student {
        update(student)
        phoneNumbersBeforeEditing.forEach { MusicLessonsApplication.db.phoneNumberDao.delete(it) }
        student.phoneNumbers.forEach { it.phoneNumberId = MusicLessonsApplication.db.phoneNumberDao.insert(it) }
        return student
    }

    @Delete
    abstract suspend fun delete(student: Student)
}
