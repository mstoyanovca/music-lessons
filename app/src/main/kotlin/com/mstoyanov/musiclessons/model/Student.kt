package com.mstoyanov.musiclessons.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "student")
data class Student(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") var studentId: Long,
                   @ColumnInfo(name = "first_name") var firstName: String,
                   @ColumnInfo(name = "last_name") var lastName: String,
                   var email: String,
                   var notes: String,
                   @Ignore var phoneNumbers: MutableList<PhoneNumber>) : Comparable<Student>, Serializable {

    // TODO change to List

    constructor() : this(0L, "", "", "", "", mutableListOf<PhoneNumber>())

    override fun compareTo(other: Student): Int {
        return if (firstName.compareTo(other.firstName, ignoreCase = true) != 0)
            firstName.compareTo(other.firstName, ignoreCase = true)
        else
            lastName.compareTo(other.lastName, ignoreCase = true)
    }
}