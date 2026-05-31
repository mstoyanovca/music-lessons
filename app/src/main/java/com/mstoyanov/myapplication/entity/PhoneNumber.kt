package com.mstoyanov.myapplication.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "phone_number",
    foreignKeys = [(ForeignKey(
        entity = Student::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("student_id"),
        onDelete = ForeignKey.CASCADE
    ))],
    indices = [(Index(value = ["student_id"]))]
)
data class PhoneNumber(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    // max length 10, validated in NanpVisualTransformation:
    @ColumnInfo(name = "number")
    val number: String = "",
    @field:TypeConverters(PhoneNumberTypeConverter::class)
    @ColumnInfo(name = "type")
    val type: PhoneNumberType = PhoneNumberType.CELL,
    @ColumnInfo(name = "student_id")
    val studentId: Long = 0L,
)