package com.mstoyanov.musiclessons

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.migration.Migration
import android.arch.persistence.room.testing.MigrationTestHelper
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.mstoyanov.musiclessons.repository.AppDatabase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    @Rule
    @JvmField
    var helper: MigrationTestHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory())

    @Before
    @Throws(IOException::class)
    fun setup() {
        val helper = TestSQLiteOpenHelper(InstrumentationRegistry.getTargetContext())
        val db = helper.writableDatabase

        db.execSQL("create table if not exists students (studentID integer primary key autoincrement not null, firstName text, lastName text, homePhone text, cellPhone text, workPhone text, email text);")
        db.execSQL("create table if not exists schedule (lessonID integer primary key autoincrement not null, weekday text, timeFrom text, timeTo text, studentID integer);")

        db.execSQL("insert into students (firstName, lastName, homePhone, cellPhone, workPhone, email) values ('Aaaa', 'Bbbb', '123-456-7890', '456-789-0000', '789-000-0000', 'aaabbb@gmail.com');")
        db.execSQL("insert into students (firstName, homePhone, cellPhone) values ('Cccc', '000-123-4444', '111-222-3456');")
        db.execSQL("insert into students (firstName, homePhone) values ('Dddd', '');")
        db.execSQL("insert into schedule (weekday, timeFrom, timeTo, studentID) values ('Monday', '15:30', '16:00', '1');")
        db.execSQL("insert into schedule (weekday, timeFrom, timeTo, studentID) values ('Monday', '16:00', '16:30', '2');")
        db.execSQL("insert into schedule (weekday, timeFrom, timeTo, studentID) values ('Monday', '16:30', '17:00', '3');")
        db.execSQL("insert into schedule (weekday, timeFrom, timeTo, studentID) values ('Tuesday', '15:30', '16:00', '1');")

        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrate_1_to_2_test() {
        helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)
    }

    private inner class TestSQLiteOpenHelper(context: Context) : SQLiteOpenHelper(context, TEST_DB, null, 1) {

        override fun onCreate(db: SQLiteDatabase) {
            // do nothing;
        }

        override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // do nothing;
        }

        override fun onDowngrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            sqLiteDatabase.execSQL("drop table if exists student;")
            sqLiteDatabase.execSQL("drop table if exists phone_number;")
            sqLiteDatabase.execSQL("drop table if exists lesson;")
        }
    }

    companion object {
        private const val TEST_DB = "school"
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table if not exists student (s_id integer primary key autoincrement not null, first_name text not null, last_name text not null, email text not null, notes text not null);")
                database.execSQL("create table if not exists phone_number (phone_number_id integer primary key autoincrement not null, number text not null, type text not null, student_id integer not null, foreign key(student_id) references student(s_id) on update no action on delete cascade);")
                database.execSQL("create index index_phone_number_student_id on phone_number (student_id);")
                database.execSQL("create table if not exists lesson (lesson_id integer primary key autoincrement not null, weekday text not null, time_from integer not null, time_to integer not null, student_id integer not null, foreign key(student_id) references student(s_id) on update no action on delete cascade);")
                database.execSQL("create index index_lesson_student_id ON lesson (student_id);")

                database.execSQL("insert into student (first_name, last_name, email, notes) select case when firstName is null then '' else firstName end, case when lastName is null then '' else lastName end, case when email is null then '' else email end, '' from students;")
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