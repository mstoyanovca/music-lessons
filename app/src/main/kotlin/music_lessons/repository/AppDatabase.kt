package music_lessons.repository

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import music_lessons.model.Lesson
import music_lessons.model.LocalTimeConverter
import music_lessons.model.PhoneNumber
import music_lessons.model.PhoneNumberTypeConverter
import music_lessons.model.Student
import music_lessons.model.WeekdayConverter

@Database(
    version = 5,
    entities = [Student::class, PhoneNumber::class, Lesson::class],
    autoMigrations = [AutoMigration(from = 4, to = 5, spec = AppDatabase.MyAutoMigration::class)]
)
@TypeConverters(LocalTimeConverter::class, PhoneNumberTypeConverter::class, WeekdayConverter::class)
abstract class AppDatabase : RoomDatabase() {
    @DeleteColumn(tableName = "student", columnName = "email")
    class MyAutoMigration : AutoMigrationSpec

    abstract val studentDao: StudentDao
    abstract val phoneNumberDao: PhoneNumberDao
    abstract val lessonDao: LessonDao
}
