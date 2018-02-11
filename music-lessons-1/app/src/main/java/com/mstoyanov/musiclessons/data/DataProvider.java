package com.mstoyanov.musiclessons.data;

import android.app.backup.BackupManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.mstoyanov.musiclessons.data.SchoolContract.Schedule;
import com.mstoyanov.musiclessons.data.SchoolContract.Students;

public class DataProvider extends ContentProvider {
    public static final int STUDENTS_QUERY = 0;
    public static final int LESSONS_QUERY = 1;
    public static final int LESSONS_INNER_JOIN_STUDENTS = 2;
    private DatabaseHelper dbHelper;
    private BackupManager backupManager;
    public static final Object sDataLock = new Object();
    private static final UriMatcher sUriMatcher;
    private static final String innerJoin = " INNER JOIN "
            + Students.TABLE_NAME + " ON " + "schedule."
            + Schedule.COLUMN_NAME_STUDENT_ID + " = " + "students."
            + Students.COLUMN_NAME_STUDENT_ID;

    static {
        sUriMatcher = new UriMatcher(0);
        sUriMatcher.addURI(SchoolContract.AUTHORITY, Students.TABLE_NAME, STUDENTS_QUERY);
        sUriMatcher.addURI(SchoolContract.AUTHORITY, Schedule.TABLE_NAME, LESSONS_QUERY);
        sUriMatcher.addURI(SchoolContract.AUTHORITY, Schedule.TABLE_NAME + "/*", LESSONS_INNER_JOIN_STUDENTS);
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case STUDENTS_QUERY:
                return "vnd.android.cursor.dir/vnd." + SchoolContract.AUTHORITY + "." + Students.TABLE_NAME;
            case LESSONS_QUERY:
                return "vnd.android.cursor.dir/vnd." + SchoolContract.AUTHORITY + "." + Schedule.TABLE_NAME;
            case LESSONS_INNER_JOIN_STUDENTS:
                return "vnd.android.cursor.item/vnd." + SchoolContract.AUTHORITY + "." + Schedule.TABLE_NAME + innerJoin + Students.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        backupManager = new BackupManager(getContext());
        return true;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int row;
        switch (match) {
            case STUDENTS_QUERY:
                synchronized (DataProvider.sDataLock) {
                    row = db.delete(Students.TABLE_NAME, selection, selectionArgs);
                }
                if (row != 0) {
                    backupManager.dataChanged();
                    return row;
                } else {
                    throw new SQLException("Failed to delete a student: " + uri);
                }
            case LESSONS_QUERY:
                synchronized (sDataLock) {
                    row = db.delete(Schedule.TABLE_NAME, selection, selectionArgs);
                    if (row != 0) {
                        backupManager.dataChanged();
                        return row;
                    } else {
                        throw new SQLException("Failed to delete a lesson: " + uri);
                    }
                }
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int row;
        switch (match) {
            case STUDENTS_QUERY:
                synchronized (sDataLock) {
                    row = (int) db.insert(SchoolContract.Students.TABLE_NAME, null, values);
                }
                if (row != 0) {
                    backupManager.dataChanged();
                    return Uri.withAppendedPath(uri, Long.toString(row));
                } else {
                    throw new SQLException("Failed to insert a student: " + uri);
                }
            case LESSONS_QUERY:
                synchronized (sDataLock) {
                    row = (int) db.insert(SchoolContract.Schedule.TABLE_NAME, null, values);
                }
                if (row != 0) {
                    backupManager.dataChanged();
                    return Uri.withAppendedPath(uri, Long.toString(row));
                } else {
                    throw new SQLException("Failed to insert a lesson: " + uri);
                }
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case STUDENTS_QUERY:
                Cursor cursor = db.query(Students.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case LESSONS_QUERY:
                cursor = db.query(Schedule.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case LESSONS_INNER_JOIN_STUDENTS:
                cursor = db.query(Schedule.TABLE_NAME + innerJoin, projection, selection, selectionArgs, null, null, sortOrder, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int row;
        switch (match) {
            case STUDENTS_QUERY:
                synchronized (sDataLock) {
                    row = db.update(SchoolContract.Students.TABLE_NAME, values, selection, selectionArgs);
                }
                if (row != 0) {
                    backupManager.dataChanged();
                    return row;
                } else {
                    throw new SQLException("Failed to update a student: " + uri);
                }
            case LESSONS_QUERY:
                synchronized (sDataLock) {
                    row = db.update(SchoolContract.Schedule.TABLE_NAME, values, selection, selectionArgs);
                }
                if (row != 0) {
                    backupManager.dataChanged();
                    return row;
                } else {
                    throw new SQLException("Failed to update a lesson: " + uri);
                }
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
}