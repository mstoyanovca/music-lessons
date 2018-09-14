package com.mstoyanov.musiclessons

import android.app.Application
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.migration.Migration
import com.mstoyanov.musiclessons.repository.AppDatabase

class MusicLessonsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DB_NAME).addMigrations(MIGRATION_1_3, MIGRATION_2_3).build()
    }

    companion object {
        lateinit var db: AppDatabase
            private set
        private const val DB_NAME = "school"

        private val MIGRATION_1_3 = object : Migration(1, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table if not exists student (s_id integer primary key autoincrement not null, first_name text not null, last_name text not null, email text not null, notes text not null);")
                database.execSQL("create table if not exists phone_number (phone_number_id integer primary key autoincrement not null, number text not null, type text not null, student_id integer not null, foreign key(student_id) references student(s_id) on update no action on delete cascade);")
                database.execSQL("create index index_phone_number_student_id on phone_number (student_id);")
                database.execSQL("create table if not exists lesson (lesson_id integer primary key autoincrement not null, weekday text not null, time_from integer not null, time_to integer not null, student_id integer not null, foreign key(student_id) references student(s_id) on update no action on delete cascade);")
                database.execSQL("create index index_lesson_student_id on lesson (student_id);")

                database.execSQL("insert into student (first_name, last_name, email, notes) select firstName, lastName, case when email is null then '' else email end, '' from students;")
                database.execSQL("insert into phone_number (number, student_id, type) select homePhone, studentID, 'Home' from students where homePhone is not null and length(homePhone) > 0;")
                database.execSQL("insert into phone_number (number, student_id, type) select cellPhone, studentID, 'Cell' from students where cellPhone is not null and length(cellPhone) > 0;")
                database.execSQL("insert into phone_number (number, student_id, type) select workPhone, studentID, 'Work' from students where workPhone is not null and length(workPhone) > 0;")
                database.execSQL("insert into lesson (weekday, student_id, time_from, time_to) select weekday, studentID,  (select strftime('%s', '1970-01-01 ' || timeFrom) * 1000), (select strftime('%s', '1970-01-01 ' || timeTo) * 1000) from schedule;")

                database.execSQL("drop table if exists students;")
                database.execSQL("drop table if exists schedule;")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table if not exists student3 (s_id integer primary key autoincrement not null, first_name text not null, last_name text not null, email text not null, notes text not null);")
                database.execSQL("create table if not exists phone_number3 (phone_number_id integer primary key autoincrement not null, number text not null, type text not null, student_id integer not null, foreign key(student_id) references student(s_id) on update no action on delete cascade);")
                database.execSQL("create index index_phone_number3_student_id on phone_number3 (student_id);")
                database.execSQL("create table if not exists lesson3 (lesson_id integer primary key autoincrement not null, weekday text not null, time_from integer not null, time_to integer not null, student_id integer not null, foreign key(student_id) references student(s_id) on update no action on delete cascade);")
                database.execSQL("create index index_lesson3_student_id on lesson3 (student_id);")

                database.execSQL("insert into student3 (first_name, last_name, email, notes) select case when first_name is null then '' else first_name end, case when last_name is null then '' else last_name end, case when email is null then '' else email end, case when notes is null then '' else notes end from student;")
                database.execSQL("insert into phone_number3 (number, student_id, type) select number, student_id, type from phone_number;")
                database.execSQL("insert into lesson3 (weekday, student_id, time_from, time_to) select weekday, student_id,  time_from, time_to from lesson;")

                database.execSQL("drop table if exists student;")
                database.execSQL("drop table if exists phone_number;")
                database.execSQL("drop table if exists lesson;")

                database.execSQL("alter table student3 rename to student;")
                database.execSQL("alter table phone_number3 rename to phone_number;")
                database.execSQL("alter table lesson3 rename to lesson;")
            }
        }
    }
}