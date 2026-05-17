package com.mstoyanov.myapplication.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mstoyanov.myapplication.entity.Lesson
import com.mstoyanov.myapplication.entity.LocalTimeConverter
import com.mstoyanov.myapplication.entity.PhoneNumber
import com.mstoyanov.myapplication.entity.PhoneNumberTypeConverter
import com.mstoyanov.myapplication.entity.Student
import com.mstoyanov.myapplication.entity.WeekdayConverter

@Database(
    version = 5,
    entities = [Student::class, PhoneNumber::class, Lesson::class],
    exportSchema = false
)
@TypeConverters(LocalTimeConverter::class, PhoneNumberTypeConverter::class, WeekdayConverter::class)
abstract class MusicLessonsDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun phoneNumberDao(): PhoneNumberDao
    abstract fun lessonDao(): LessonDao
}
