package com.mstoyanov.myapplication.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "student")
data class Student(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "student_id") val studentId: Long = 0L,
    // max length 24
    @ColumnInfo(name = "first_name") val firstName: String = "",
    // max length 24
    @ColumnInfo(name = "last_name") val lastName: String = "",
    // max length 128
    @ColumnInfo(name = "notes") val notes: String = "",
    @Ignore var phoneNumbers: List<PhoneNumber> = listOf()
) : Comparable<Student> {

    override fun compareTo(other: Student): Int {
        return when {
            firstName.isNotEmpty() && firstName.compareTo(other.firstName, ignoreCase = true) != 0 -> firstName.compareTo(other.firstName, ignoreCase = true)
            else -> lastName.compareTo(other.lastName, ignoreCase = true)
        }
    }

}
