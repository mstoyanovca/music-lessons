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
        db.execSQL("create table if not exists students (studentID integer primary key autoincrement, firstName text, lastName text, homePhone text, cellPhone text, workPhone text, email text);")
        db.execSQL("create table if not exists schedule (lessonID integer primary key autoincrement, weekday text, timeFrom text, timeTo text, studentID integer);")
        db.execSQL("insert into students (firstName, lastName, homePhone, cellPhone, workPhone, email) values ('Aaaa', 'Test', '123-456-7890', '456-789-0000', '789-456-7890', 'aaabbb@gmail.com');")
        db.execSQL("insert into students (firstName, homePhone) values ('Bbbb', '123-456-0000');")
        db.execSQL("insert into schedule (weekday, timeFrom, timeTo, studentID) values ('Monday', '15:30', '16:00', '1');")
        db.execSQL("insert into schedule (weekday, timeFrom, timeTo, studentID) values ('Monday', '16:00', '16:30', '2');")
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
                database.execSQL("create table if not exists student (_id integer primary key autoincrement not null, first_name text, last_name text, email text, notes text);")
                database.execSQL("create table if not exists phone_number (phone_number_id integer primary key autoincrement not null, number text, type text, student_id integer not null, foreign key(student_id) references student(_id) on update no action on delete cascade);")
                database.execSQL("create index index_phone_number_student_id on phone_number (student_id);")
                database.execSQL("create table if not exists lesson (lesson_id integer primary key autoincrement not null, weekday text, time_from integer, time_to integer, student_id integer not null, foreign key(student_id) references student(_id) on update no action on delete cascade);")
                database.execSQL("create index index_lesson_student_id ON lesson (student_id);")

                database.execSQL("insert into student (first_name, last_name, email) select firstName, lastName, email from students;")
                database.execSQL("update student set first_name = '' where first_name is null;")
                database.execSQL("update student set last_name = '' where last_name is null;")
                database.execSQL("update student set email = '' where email is null;")
                database.execSQL("update student set notes = '';")

                database.execSQL("insert into phone_number (number, student_id) select homePhone, studentID from students where homePhone is not null and length(homePhone) > 0;")
                database.execSQL("update phone_number set type = 'Home' where number is not null and type is null;")
                database.execSQL("insert into phone_number (number, student_id) select cellPhone, studentID from students where cellPhone is not null and length(cellPhone) > 0;")
                database.execSQL("update phone_number set type = 'Cell' where number is not null and type is null;")
                database.execSQL("insert into phone_number (number, student_id) select workPhone, studentID from students where workPhone is not null and length(workPhone) > 0;")
                database.execSQL("update phone_number set type = 'Work' where number is not null and type is null;")

                database.execSQL("insert into lesson (lesson_id, weekday, student_id) select lessonID, weekday, studentID from schedule;")
                database.execSQL("update lesson set time_from = (select strftime('%s', '1970-01-01 ' || (select timeFrom from schedule where schedule.lessonID = lesson.lesson_id)) * 1000);")
                database.execSQL("update lesson set time_to = (select strftime('%s', '1970-01-01 ' || (select timeTo from schedule where schedule.lessonID = lesson.lesson_id)) * 1000);")

                database.execSQL("drop table if exists students;")
                database.execSQL("drop table if exists schedule;")
            }
        }
    }
}