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
            database.execSQL("CREATE TABLE IF NOT EXISTS student_4 (student_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, first_name TEXT NOT NULL, last_name TEXT NOT NULL, email TEXT NOT NULL, notes TEXT NOT NULL);")
            database.execSQL("CREATE TABLE IF NOT EXISTS phone_number_4 (phone_number_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, number TEXT NOT NULL, type TEXT NOT NULL, student_owner_id INTEGER NOT NULL, FOREIGN KEY(student_owner_id) REFERENCES student(student_id) ON UPDATE NO ACTION ON DELETE CASCADE);")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_phone_number_4_student_owner_id ON phone_number_4(student_owner_id);")
            database.execSQL("CREATE TABLE IF NOT EXISTS lesson_4 (lesson_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, weekday TEXT NOT NULL, time_from TEXT NOT NULL, time_to TEXT NOT NULL, student_owner_id INTEGER NOT NULL, FOREIGN KEY(student_owner_id) REFERENCES student(student_id) ON UPDATE NO ACTION ON DELETE CASCADE );")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_lesson_4_student_owner_id ON lesson_4(student_owner_id);")

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
