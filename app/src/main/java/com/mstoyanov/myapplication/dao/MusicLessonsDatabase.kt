package com.mstoyanov.myapplication.dao

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
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
    version = 6,
    entities = [PhoneNumber::class, Student::class, Lesson::class],
    autoMigrations = [AutoMigration(from = 5, to = 6, spec = MusicLessonsDatabase.FiveToSixMigration::class)],
    exportSchema = true
)
@TypeConverters(LocalTimeConverter::class, PhoneNumberTypeConverter::class, WeekdayConverter::class)
abstract class MusicLessonsDatabase : RoomDatabase() {
    @RenameColumn.Entries(
        RenameColumn(tableName = "phone_number", fromColumnName = "phone_number_id", toColumnName = "id"),
        RenameColumn(tableName = "phone_number", fromColumnName = "student_owner_id", toColumnName = "student_id"),
        RenameColumn(tableName = "student", fromColumnName = "student_id", toColumnName = "id"),
        RenameColumn(tableName = "lesson", fromColumnName = "lesson_id", toColumnName = "id"),
        RenameColumn(tableName = "lesson", fromColumnName = "student_owner_id", toColumnName = "student_id")
    )
    class FiveToSixMigration : AutoMigrationSpec

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
