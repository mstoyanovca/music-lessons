package com.mstoyanov.musiclessons

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mstoyanov.musiclessons.repository.AppDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val testDb = "migration_test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory())

    private val migration3To4 = object : Migration(3, 4) {
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

    @Test
    @Throws(IOException::class)
    fun migrate3To4() {
        helper.createDatabase(testDb, 3).apply {
            execSQL("insert into student (s_id, first_name, last_name, email, notes) values (1, 'Aaaa', 'Bbbb', 'aaabbb@gmail.com', 'notes aaa');")
            execSQL("insert into student (s_id, first_name, last_name, email, notes) values (2, 'Cccc', 'Dddd', 'cccddd@gmail.com', 'notes ccc');")
            execSQL("insert into student (s_id, first_name, last_name, email, notes) values (3, 'Eeee', 'Ffff', 'eeefff@gmail.com', 'notes eee');")

            execSQL("insert into phone_number (number, student_id, type) values ('(123) 456-7890', 1, 'Home');")
            execSQL("insert into phone_number (number, student_id, type) values ('(456) 789-0000', 1, 'Cell');")
            execSQL("insert into phone_number (number, student_id, type) values ('(789) 000-1234', 1, 'Work');")
            execSQL("insert into phone_number (number, student_id, type) values ('(000) 123-4444', 2, 'Home');")
            execSQL("insert into phone_number (number, student_id, type) values ('(111) 222-3456', 3, 'Cell');")

            execSQL("insert into lesson (weekday, student_id, time_from, time_to) values('Monday', 1, (select strftime('%s', '1970-01-01 15:30') * 1000), (select strftime('%s', '1970-01-01 16:00') * 1000));")
            execSQL("insert into lesson (weekday, student_id, time_from, time_to) values('Monday', 2, (select strftime('%s', '1970-01-01 16:00') * 1000), (select strftime('%s', '1970-01-01 16:30') * 1000));")
            execSQL("insert into lesson (weekday, student_id, time_from, time_to) values('Monday', 3, (select strftime('%s', '1970-01-01 16:30') * 1000), (select strftime('%s', '1970-01-01 17:00') * 1000));")
            execSQL("insert into lesson (weekday, student_id, time_from, time_to) values('Tuesday', 1, (select strftime('%s', '1970-01-01 15:30') * 1000), (select strftime('%s', '1970-01-01 16:00') * 1000));")

            close()
        }

        helper.runMigrationsAndValidate(testDb, 4, true, migration3To4)
    }
}
