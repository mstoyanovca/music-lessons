package com.mstoyanov.myapplication.dao

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.mstoyanov.myapplication.entity.Lesson
import com.mstoyanov.myapplication.entity.LocalTimeConverter
import com.mstoyanov.myapplication.entity.PhoneNumber
import com.mstoyanov.myapplication.entity.PhoneNumberTypeConverter
import com.mstoyanov.myapplication.entity.Student
import com.mstoyanov.myapplication.entity.WeekdayConverter

@Database(
    version = 5,
    entities = [Student::class, PhoneNumber::class, Lesson::class],
    autoMigrations = [AutoMigration(from = 4, to = 5, spec = MusicLessonsDatabase.MyAutoMigration::class)]
)
@TypeConverters(LocalTimeConverter::class, PhoneNumberTypeConverter::class, WeekdayConverter::class)
abstract class MusicLessonsDatabase : RoomDatabase() {
    @DeleteColumn(tableName = "student", columnName = "email")
    class MyAutoMigration : AutoMigrationSpec

    abstract fun studentDao(): StudentDao
    abstract fun phoneNumberDao(): PhoneNumberDao
    abstract fun lessonDao(): LessonDao

    companion object {
        @Volatile
        private var Instance: MusicLessonsDatabase? = null

        fun getDatabase(context: Context): MusicLessonsDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MusicLessonsDatabase::class.java, "school")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
