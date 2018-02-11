package com.mstoyanov.musiclessons.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class SchoolContract {
    public static final String DATABASE_NAME = "school";
    public static final int DATABASE_VERSION = 1;
    private static final String SCHEME = "content";
    public static final String AUTHORITY = "com.mstoyanov.music_lessons.data";
    private static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY);
    public static final Uri STUDENTS_TABLE_CONTENTURI = Uri.withAppendedPath(CONTENT_URI, Students.TABLE_NAME);
    public static final Uri SCHEDULE_TABLE_CONTENTURI = Uri.withAppendedPath(CONTENT_URI, Schedule.TABLE_NAME);

    public static abstract class Students implements BaseColumns {
        public static final String TABLE_NAME = "students";
        public static final String COLUMN_NAME_STUDENT_ID = "studentID";
        public static final String COLUMN_NAME_FIRST_NAME = "firstName";
        public static final String COLUMN_NAME_LAST_NAME = "lastName";
        public static final String COLUMN_NAME_HOME_PHONE = "homePhone";
        public static final String COLUMN_NAME_CELL_PHONE = "cellPhone";
        public static final String COLUMN_NAME_WORK_PHONE = "workPhone";
        public static final String COLUMN_NAME_EMAIL = "email";
    }

    public static abstract class Schedule implements BaseColumns {
        public static final String TABLE_NAME = "schedule";
        public static final String COLUMN_NAME_LESSON_ID = "lessonID";
        public static final String COLUMN_NAME_WEEKDAY = "weekday";
        public static final String COLUMN_NAME_TIME_FROM = "timeFrom";
        public static final String COLUMN_NAME_TIME_TO = "timeTo";
        public static final String COLUMN_NAME_STUDENT_ID = "studentID";
    }
}