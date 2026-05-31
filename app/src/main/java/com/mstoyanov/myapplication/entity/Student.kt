package com.mstoyanov.myapplication.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "student")
data class Student(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    // max length 24, validated in AddStudentComponents
    @ColumnInfo(name = "first_name") val firstName: String,
    // max length 24, validated in AddStudentComponents
    @ColumnInfo(name = "last_name") val lastName: String,
    // max length 128, validated in AddStudentComponents
    @ColumnInfo(name = "notes") val notes: String,
    @Ignore
    val phoneNumbers: List<PhoneNumber>
) : Comparable<Student> {

    constructor(
        id: Long,
        firstName: String,
        lastName: String,
        notes: String
    ) : this(
        id = id,
        firstName = firstName,
        lastName = lastName,
        notes = notes,
        phoneNumbers = listOf()
    )

    override fun compareTo(other: Student): Int {
        return when {
            firstName.isNotEmpty() && firstName.compareTo(other.firstName, ignoreCase = true) != 0 -> firstName.compareTo(other.firstName, ignoreCase = true)
            else -> lastName.compareTo(other.lastName, ignoreCase = true)
        }
    }

}
