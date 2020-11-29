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
                database.execSQL("begin transaction;")

                database.execSQL("create table if not exists student_4 (student_id integer primary key autoincrement not null, first_name text not null, last_name text not null, email text not null, notes text not null);")
                database.execSQL("create table if not exists phone_number_4 (phone_number_id integer primary key autoincrement not null, number text not null, type text not null, student_owner_id integer not null, foreign key(student_owner_id) references student(student_id) on update no action on delete cascade);")
                database.execSQL("create table if not exists lesson_4 (lesson_id integer primary key autoincrement not null, weekday text not null, time_from text not null, time_to text not null, student_owner_id integer not null, foreign key(student_owner_id) references student(student_id) on update no action on delete cascade);")

                database.execSQL("insert into student_4 (student_id, first_name, last_name, email, notes) select s_id, first_name, last_name, email, notes from student;")
                database.execSQL("insert into phone_number_4 (phone_number_id, number, type, student_owner_id) select phone_number_id, number, type, student_id from phone_number;")
                database.execSQL("insert into lesson_4 (lesson_id, weekday, time_from, time_to, student_owner_id) select lesson_id, weekday, strftime('%H:%M', time_from/1000, 'unixepoch'), strftime('%H:%M', time_to/1000, 'unixepoch'), student_id from lesson;")

                database.execSQL("drop table if exists student;")
                database.execSQL("drop table if exists phone_number;")
                database.execSQL("drop index if exists index_phone_number_student_id;")
                database.execSQL("drop table if exists lesson;")
                database.execSQL("drop index if exists index_lesson_student_id;")

                database.execSQL("alter table student_4 rename to student;")
                database.execSQL("alter table phone_number_4 rename to phone_number;")
                database.execSQL("create index if not exists index_phone_number_student_owner_id on phone_number(student_owner_id);")
                database.execSQL("alter table lesson_4 rename to lesson;")
                database.execSQL("create index if not exists index_lesson_student_owner_id on lesson(student_owner_id);")

                database.execSQL("commit;")
            }
        }
    }
}
