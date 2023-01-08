package com.mstoyanov.musiclessons.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.io.Serializable

@Entity(
    tableName = "phone_number",
    foreignKeys = [(ForeignKey(
        entity = Student::class,
        parentColumns = arrayOf("student_id"),
        childColumns = arrayOf("student_owner_id"),
        onDelete = CASCADE
    ))],
    indices = [(Index(value = ["student_owner_id"]))]
)
data class PhoneNumber(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "phone_number_id") var phoneNumberId: Long,
    var number: String,
    @TypeConverters(PhoneNumberTypeConverter::class) var type: PhoneNumberType,
    @ColumnInfo(name = "student_owner_id") var studentId: Long,
    @Ignore var isValid: Boolean
) : Serializable {

    constructor() : this(0L, "", PhoneNumberType.CELL, 0L, false)

    @Ignore
    constructor(number: String, type: PhoneNumberType) : this() {
        this.number = number
        this.type = type
    }
}
