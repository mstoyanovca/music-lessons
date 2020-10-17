package com.mstoyanov.musiclessons

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.mstoyanov.musiclessons.repository.AppDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    @Rule
    @JvmField
    // this is a Room persistence helper:
    val helper: MigrationTestHelper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), AppDatabase::class.java.canonicalName, FrameworkSQLiteOpenHelperFactory())

    @Test
    @Throws(IOException::class)
    fun migrate_1_to_3_test() {
        // this is an SQLite helper:
        val sqLiteHelper = TestSQLiteOpenHelper(InstrumentationRegistry.getTargetContext())
        val db = sqLiteHelper.writableDatabase

        db.execSQL("create table if not exists students (studentID integer primary key autoincrement not null, firstName text, lastName text, homePhone text, cellPhone text, workPhone text, email text);")
        db.execSQL("create table if not exists schedule (lessonID integer primary key autoincrement not null, weekday text, timeFrom text, timeTo text, studentID integer);")

        db.execSQL("insert into students (firstName, lastName, homePhone, cellPhone, workPhone, email) values ('Aaaa', 'Bbbb', '123-456-7890', '456-789-0000', '789-000-1234', 'aaabbb@gmail.com');")
        db.execSQL("insert into students (firstName, lastName, homePhone, cellPhone) values ('Cccc', 'Cc', '000-123-4444', '111-222-3456');")
        db.execSQL("insert into students (firstName, lastName, homePhone) values ('Dddd', 'Dd', '222-3456-7777');")

        db.execSQL("insert into schedule (weekday, timeFrom, timeTo, studentID) values ('Monday', '15:30', '16:00', '1');")
        db.execSQL("insert into schedule (weekday, timeFrom, timeTo, studentID) values ('Monday', '16:00', '16:30', '2');")
        db.execSQL("insert into schedule (weekday, timeFrom, timeTo, studentID) values ('Monday', '16:30', '17:00', '3');")
        db.execSQL("insert into schedule (weekday, timeFrom, timeTo, studentID) values ('Tuesday', '15:30', '16:00', '1');")

        db.close()

        helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_1_3)
    }

    @Test
    @Throws(IOException::class)
    fun migrate_2_to_3_test() {
        val db = helper.createDatabase(TEST_DB, 2)

        db.execSQL("insert into student (first_name, last_name, email, notes) values ('Aaaa', 'Bbbb', 'aaabbb@gmail.com', 'notes');")
        db.execSQL("insert into student (first_name) values ('Cccc');")
        db.execSQL("insert into student (last_name) values ('Dddd');")

        db.execSQL("insert into phone_number (number, student_id, type) values ('123-456-7890', 1, 'Home');")
        db.execSQL("insert into phone_number (number, student_id, type) values ('456-789-0000', 1, 'Cell');")
        db.execSQL("insert into phone_number (number, student_id, type) values ('789-000-1234', 1, 'Work');")
        db.execSQL("insert into phone_number (number, student_id, type) values ('000-123-4444', 2, 'Home');")
        db.execSQL("insert into phone_number (number, student_id, type) values ('111-222-3456', 2, 'Cell');")

        db.execSQL("insert into lesson (weekday, student_id, time_from, time_to) values('Monday', 1, (select strftime('%s', '1970-01-01 15:30') * 1000), (select strftime('%s', '1970-01-01 16:00') * 1000));")
        db.execSQL("insert into lesson (weekday, student_id, time_from, time_to) values('Monday', 2, (select strftime('%s', '1970-01-01 16:00') * 1000), (select strftime('%s', '1970-01-01 16:30') * 1000));")
        db.execSQL("insert into lesson (weekday, student_id, time_from, time_to) values('Monday', 3, (select strftime('%s', '1970-01-01 16:30') * 1000), (select strftime('%s', '1970-01-01 17:00') * 1000));")
        db.execSQL("insert into lesson (weekday, student_id, time_from, time_to) values('Tuesday', 1, (select strftime('%s', '1970-01-01 15:30') * 1000), (select strftime('%s', '1970-01-01 16:00') * 1000));")

        db.close()

        helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3)
    }

    companion object {
        private const val TEST_DB = "migration_test"

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

    private inner class TestSQLiteOpenHelper(context: Context) : SQLiteOpenHelper(context, TEST_DB, null, 1) {

        override fun onCreate(db: SQLiteDatabase) {
            // do nothing;
        }

        override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // do nothing;
        }

        override fun onDowngrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if (oldVersion == 1) {
                sqLiteDatabase.execSQL("drop table if exists student;")
                sqLiteDatabase.execSQL("drop table if exists phone_number;")
                sqLiteDatabase.execSQL("drop table if exists lesson;")
            }
        }
    }
}