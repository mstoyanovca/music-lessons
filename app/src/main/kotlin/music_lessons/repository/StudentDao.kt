package music_lessons.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import music_lessons.MusicLessonsApplication
import music_lessons.model.PhoneNumber
import music_lessons.model.Student
import music_lessons.model.StudentWithPhoneNumbers

@Dao
interface StudentDao {
    @Query("select * from student")
    suspend fun findAll(): List<Student>

    @Transaction
    @Query("select * from student")
    suspend fun findAllStudentWithPhoneNumbers(): List<StudentWithPhoneNumbers>

    @Transaction
    suspend fun findAllWithPhoneNumbers(): List<Student> {
        val studentsWithPhoneNumbers = findAllStudentWithPhoneNumbers()
        studentsWithPhoneNumbers.forEach { it.student.phoneNumbers = it.phoneNumbers.toMutableList() }
        return studentsWithPhoneNumbers.map { it.student }.sorted()
    }

    @Insert
    suspend fun insert(student: Student): Long

    @Transaction
    suspend fun insertWithPhoneNumbers(student: Student) {
        val id = insert(student)
        student.studentId = id
        student.phoneNumbers.forEach { it.studentId = id }
        MusicLessonsApplication.db.phoneNumberDao.insertAll(student.phoneNumbers)
    }

    @Update
    suspend fun update(student: Student)

    @Transaction
    suspend fun updateStudentWithPhoneNumbers(student: Student, phoneNumbersBeforeEditing: List<PhoneNumber>): Student {
        update(student)
        phoneNumbersBeforeEditing.forEach { MusicLessonsApplication.db.phoneNumberDao.delete(it) }
        student.phoneNumbers.forEach { it.phoneNumberId = MusicLessonsApplication.db.phoneNumberDao.insert(it) }
        return student
    }

    @Delete
    suspend fun delete(student: Student)
}
