package com.mstoyanov.musiclessons.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mstoyanov.musiclessons.model.*

@Database(entities = [Student::class, PhoneNumber::class, Lesson::class], version = 4)
@TypeConverters(LocalTimeConverter::class, PhoneNumberTypeConverter::class, WeekdayConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val studentDao: StudentDao
    abstract val phoneNumberDao: PhoneNumberDao
    abstract val lessonDao: LessonDao
}