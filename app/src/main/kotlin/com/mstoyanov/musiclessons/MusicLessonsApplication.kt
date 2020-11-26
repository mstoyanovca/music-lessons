package com.mstoyanov.musiclessons

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mstoyanov.musiclessons.repository.AppDatabase

class MusicLessonsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DB_NAME).addMigrations(MIGRATION_3_4).fallbackToDestructiveMigration().build()
    }

    companion object {
        lateinit var db: AppDatabase
            private set
        private const val DB_NAME = "school"

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS student_4 (student_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, first_name TEXT NOT NULL, last_name TEXT NOT NULL, email TEXT NOT NULL, notes TEXT NOT NULL);")
                database.execSQL("CREATE TABLE IF NOT EXISTS phone_number_4 (phone_number_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, number TEXT NOT NULL, type TEXT NOT NULL, student_owner_id INTEGER NOT NULL, FOREIGN KEY(student_owner_id) REFERENCES student(student_id) ON UPDATE NO ACTION ON DELETE CASCADE);")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_phone_number_4_student_owner_id ON student(student_owner_id);")
                database.execSQL("CREATE TABLE IF NOT EXISTS lesson (lesson_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, weekday TEXT NOT NULL, time_from TEXT NOT NULL, time_to TEXT NOT NULL, student_owner_id INTEGER NOT NULL, FOREIGN KEY(student_owner_id) REFERENCES student(student_id) ON UPDATE NO ACTION ON DELETE CASCADE );")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_lesson_4_student_owner_id ON lesson(student_owner_id);")

                database.execSQL("insert into student_4 (student_id, first_name, last_name, email, notes) select s_id, first_name, last_name, email, notes from student;")
                database.execSQL("insert into phone_number_4 (phone_number_id, number, type, student_owner_id) select phone_number_id, number, type, student_id from phone_number;")
                database.execSQL("insert into lesson_4 (lesson_id, weekday, time_from, time_to, student_owner_id) select lesson_id, weekday, time_from, time_to, student_id from lesson;")

                database.execSQL("drop table if exists student;")
                database.execSQL("drop table if exists phone_number;")
                database.execSQL("drop table if exists lesson;")

                database.execSQL("alter table student_4 rename to student;")
                database.execSQL("alter table phone_number_4 rename to phone_number;")
                database.execSQL("alter table lesson_4 rename to lesson;")
            }
        }
    }
}
