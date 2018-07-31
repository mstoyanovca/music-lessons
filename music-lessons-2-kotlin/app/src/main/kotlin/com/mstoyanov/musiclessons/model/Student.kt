package com.mstoyanov.musiclessons.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "student")
data class Student @Ignore constructor(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") var studentId: Long = 0,
                                       @ColumnInfo(name = "first_name") var firstName: String? = null,
                                       @ColumnInfo(name = "last_name") var lastName: String? = null,
                                       var email: String? = null,
                                       var notes: String? = null,
                                       @Ignore var phoneNumbers: MutableList<PhoneNumber>? = null) : Comparable<Student>, Serializable {

    constructor() : this(0, null, null, null, null, null)

    override fun compareTo(other: Student): Int {
        return if (firstName!!.compareTo(other.firstName!!, ignoreCase = true) != 0)
            firstName!!.compareTo(other.firstName!!, ignoreCase = true)
        else
            lastName!!.compareTo(other.lastName!!, ignoreCase = true)
    }
}