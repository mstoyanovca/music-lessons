package model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Ignore
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
        onDelete = CASCADE
    ))],
    indices = [(Index(value = ["student_owner_id"]))]
)
data class PhoneNumber(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "phone_number_id") var phoneNumberId: Long,
    var number: String,
    @field:TypeConverters(PhoneNumberTypeConverter::class) var type: PhoneNumberType,
    @ColumnInfo(name = "student_owner_id") var studentId: Long,
    @Ignore var isValid: Boolean
) {
    constructor() : this(0L, "", PhoneNumberType.CELL, 0L, false)

}
