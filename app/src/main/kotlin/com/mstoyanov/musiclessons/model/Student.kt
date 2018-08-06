package com.mstoyanov.musiclessons.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = "student")
data class Student(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "s_id") @NotNull var studentId: Long,
                   @ColumnInfo(name = "first_name") @NotNull var firstName: String,
                   @ColumnInfo(name = "last_name") @NotNull var lastName: String,
                   @NotNull var email: String,
                   @NotNull var notes: String,
                   @Ignore var phoneNumbers: List<PhoneNumber>) : Comparable<Student>, Serializable {

    constructor() : this(0L, "", "", "", "", listOf<PhoneNumber>())

    override fun compareTo(other: Student): Int {
        return if (firstName.compareTo(other.firstName, ignoreCase = true) != 0)
            firstName.compareTo(other.firstName, ignoreCase = true)
        else
            lastName.compareTo(other.lastName, ignoreCase = true)
    }
}