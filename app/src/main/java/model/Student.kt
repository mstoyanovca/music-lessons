package model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "student")
data class Student(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "student_id") val studentId: Long,
    // max length 24
    @ColumnInfo(name = "first_name") var firstName: String,
    // max length 24
    @ColumnInfo(name = "last_name") var lastName: String,
    // max length 128
    var notes: String,
    @Ignore var phoneNumbers: MutableList<PhoneNumber>
) : Comparable<Student> {

    constructor() : this(0L, "", "", "", mutableListOf<PhoneNumber>())

    override fun compareTo(other: Student): Int {
        return if (firstName.isNotEmpty() && firstName.compareTo(other.firstName, ignoreCase = true) != 0)
            firstName.compareTo(other.firstName, ignoreCase = true)
        else
            lastName.compareTo(other.lastName, ignoreCase = true)
    }

}
