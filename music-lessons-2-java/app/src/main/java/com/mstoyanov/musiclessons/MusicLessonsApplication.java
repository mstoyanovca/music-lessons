package com.mstoyanov.musiclessons;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;

import com.mstoyanov.musiclessons.repository.AppDatabase;

public class MusicLessonsApplication extends Application {
    private static AppDatabase DB;
    private static final String DB_NAME = "school";

    @Override
    public void onCreate() {
        super.onCreate();
        DB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).addMigrations(MIGRATION_1_2).build();
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("create table if not exists student (_id integer primary key autoincrement not null, first_name text, last_name text, email text, notes text);");
            database.execSQL("create table if not exists phone_number (phone_number_id integer primary key autoincrement not null, number text, type text, student_id integer not null, foreign key(student_id) references student(_id) on update no action on delete cascade);");
            database.execSQL("create index index_phone_number_student_id on phone_number (student_id);");
            database.execSQL("create table if not exists lesson (lesson_id integer primary key autoincrement not null, weekday text, time_from integer, time_to integer, student_id integer not null, foreign key(student_id) references student(_id) on update no action on delete cascade);");
            database.execSQL("create index index_lesson_student_id on lesson (student_id);");

            database.execSQL("insert into student (first_name, last_name, email) select firstName, lastName, email from students;");
            database.execSQL("update student set first_name = '' where first_name is null;");
            database.execSQL("update student set last_name = '' where last_name is null;");
            database.execSQL("update student set email = '' where email is null;");
            database.execSQL("update student set notes = '';");

            database.execSQL("insert into phone_number (number, student_id) select homePhone, studentID from students where length(homePhone) > 0;");
            database.execSQL("update phone_number set type = 'Home' where length(number) > 0 and type is null;");
            database.execSQL("insert into phone_number (number, student_id) select cellPhone, studentID from students where length(cellPhone) > 0;");
            database.execSQL("update phone_number set type = 'Cell' where length(number) > 0 and type is null;");
            database.execSQL("insert into phone_number (number, student_id) select workPhone, studentID from students where length(workPhone) > 0;");
            database.execSQL("update phone_number set type = 'Work' where length(number) > 0 and type is null;");

            database.execSQL("insert into lesson (lesson_id, weekday, student_id) select lessonID, weekday, studentID from schedule;");
            database.execSQL("update lesson set time_from = (select strftime('%s', '1970-01-01 ' || (select timeFrom from schedule where schedule.lessonID = lesson.lesson_id)) * 1000);");
            database.execSQL("update lesson set time_to = (select strftime('%s', '1970-01-01 ' || (select timeTo from schedule where schedule.lessonID = lesson.lesson_id)) * 1000);");

            database.execSQL("drop table if exists students;");
            database.execSQL("drop table if exists schedule;");
        }
    };

    public static AppDatabase getDB() {
        return DB;
    }
}