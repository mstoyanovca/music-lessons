package com.mstoyanov.myapplication.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "phone_number",
    foreignKeys = [(ForeignKey(
        entity = Student::class,
        parentColumns = arrayOf("student_id"),
        childColumns = arrayOf("student_owner_id"),
        onUpdate = CASCADE,
        onDelete = CASCADE
    ))],
    indices = [(Index(value = ["student_owner_id"]))]
)
data class PhoneNumber(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "phone_number_id")
    val phoneNumberId: Long,
    // max length 32
    @ColumnInfo(name = "number")
    val number: String,
    @field:TypeConverters(PhoneNumberTypeConverter::class)
    @ColumnInfo(name = "type")
    val type: PhoneNumberType,
    @ColumnInfo(name = "student_owner_id")
    val studentId: Long,
) {

}
