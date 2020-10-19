package com.mstoyanov.musiclessons.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = "phone_number",
        foreignKeys = [(ForeignKey(entity = Student::class, parentColumns = arrayOf("s_id"), childColumns = arrayOf("student_id"), onDelete = CASCADE))],
        indices = [(Index(value = ["student_id"]))])
data class PhoneNumber(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "phone_number_id") @NotNull var phoneNumberId: Long,
                       @NotNull var number: String,
                       @TypeConverters(PhoneNumberTypeConverter::class) @NotNull var type: PhoneNumberType,
                       @ColumnInfo(name = "student_id") @NotNull var studentId: Long,
                       @Ignore var isValid: Boolean) : Serializable {

    constructor() : this(0L, "", PhoneNumberType.HOME, 0L, false)

    @Ignore
    constructor(number: String, type: PhoneNumberType) : this() {
        this.number = number
        this.type = type
    }
}
