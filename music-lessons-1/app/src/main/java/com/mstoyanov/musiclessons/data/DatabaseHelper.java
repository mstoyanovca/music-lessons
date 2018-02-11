package com.mstoyanov.musiclessons.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    DatabaseHelper(Context context) {
        super(context, SchoolContract.DATABASE_NAME, null, SchoolContract.DATABASE_VERSION);
    }

    private static final String CREATE_TABLE_STUDENTS = "CREATE TABLE IF NOT EXISTS "
            + SchoolContract.Students.TABLE_NAME
            + " ("
            + SchoolContract.Students.COLUMN_NAME_STUDENT_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SchoolContract.Students.COLUMN_NAME_FIRST_NAME
            + " TEXT, "
            + SchoolContract.Students.COLUMN_NAME_LAST_NAME
            + " TEXT, "
            + SchoolContract.Students.COLUMN_NAME_HOME_PHONE
            + " TEXT, "
            + SchoolContract.Students.COLUMN_NAME_CELL_PHONE
            + " TEXT, "
            + SchoolContract.Students.COLUMN_NAME_WORK_PHONE
            + " TEXT, "
            + SchoolContract.Students.COLUMN_NAME_EMAIL + " TEXT)";

    private static final String CREATE_TABLE_SCHEDULE = "CREATE TABLE IF NOT EXISTS "
            + SchoolContract.Schedule.TABLE_NAME
            + " ("
            + SchoolContract.Schedule.COLUMN_NAME_LESSON_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SchoolContract.Schedule.COLUMN_NAME_WEEKDAY
            + " TEXT, "
            + SchoolContract.Schedule.COLUMN_NAME_TIME_FROM
            + " TEXT, "
            + SchoolContract.Schedule.COLUMN_NAME_TIME_TO
            + " TEXT, "
            + SchoolContract.Schedule.COLUMN_NAME_STUDENT_ID + " TEXT)";

    private static final String DROP_TABLE_STUDENTS = "DROP TABLE IF EXISTS "
            + SchoolContract.Students.TABLE_NAME;

    private static final String DROP_TABLE_SCHEDULE = "DROP TABLE IF EXISTS "
            + SchoolContract.Schedule.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STUDENTS);
        db.execSQL(CREATE_TABLE_SCHEDULE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_STUDENTS);
        db.execSQL(DROP_TABLE_SCHEDULE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}