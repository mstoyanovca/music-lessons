package com.mstoyanov.musiclessons

import android.app.Application
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.migration.Migration
import com.mstoyanov.musiclessons.repository.AppDatabase

class MusicLessonsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DB_NAME).addMigrations(MIGRATION_1_2).build()
    }

    companion object {
        lateinit var db: AppDatabase
            private set
        private const val DB_NAME = "school"

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // TODO: create a 2_3 migration; the DB in production:
                /*database.execSQL("create table if not exists student (_id integer primary key autoincrement not null, first_name text, last_name text, email text, notes text);");
                database.execSQL("create table if not exists phone_number (phone_number_id integer primary key autoincrement not null, number text, type text, student_id integer not null, foreign key(student_id) references student(_id) on update no action on delete cascade);");
                database.execSQL("create index index_phone_number_student_id on phone_number (student_id);");
                database.execSQL("create table if not exists lesson (lesson_id integer primary key autoincrement not null, weekday text, time_from integer, time_to integer, student_id integer not null, foreign key(student_id) references student(_id) on update no action on delete cascade);");
                database.execSQL("create index index_lesson_student_id on lesson (student_id);");*/

                database.execSQL("create table if not exists student (s_id integer primary key autoincrement not null, first_name text not null, last_name text not null, email text not null, notes text not null);")
                database.execSQL("create table if not exists phone_number (phone_number_id integer primary key autoincrement not null, number text not null, type text not null, student_id integer not null, foreign key(student_id) references student(s_id) on update no action on delete cascade);")
                database.execSQL("create index index_phone_number_student_id on phone_number (student_id);")
                database.execSQL("create table if not exists lesson (lesson_id integer primary key autoincrement not null, weekday text not null, time_from integer not null, time_to integer not null, student_id integer not null, foreign key(student_id) references student(s_id) on update no action on delete cascade);")
                database.execSQL("create index index_lesson_student_id ON lesson (student_id);")

                database.execSQL("insert into student (first_name, last_name, email, notes) select case when firstName = null then '' else firstName end, case when lastName = null then '' else lastName end, case when email = null then '' else email end, '' from students;")
                database.execSQL("insert into phone_number (number, student_id,type) select homePhone, studentID, 'Home' from students where homePhone is not null and length(homePhone) > 0;")
                database.execSQL("insert into phone_number (number, student_id, type) select cellPhone, studentID, 'Cell' from students where cellPhone is not null and length(cellPhone) > 0;")
                database.execSQL("insert into phone_number (number, student_id, type) select workPhone, studentID, 'Work' from students where workPhone is not null and length(workPhone) > 0;")
                database.execSQL("insert into lesson (weekday, student_id, time_from, time_to) select weekday, studentID,  strftime('%s', timeFrom * 1000), strftime('%s', timeTo * 1000) from schedule;")

                database.execSQL("drop table if exists students;")
                database.execSQL("drop table if exists schedule;")
            }
        }
    }
}