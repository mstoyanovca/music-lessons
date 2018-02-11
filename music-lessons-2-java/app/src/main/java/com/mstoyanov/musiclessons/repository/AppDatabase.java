package com.mstoyanov.musiclessons.repository;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.mstoyanov.musiclessons.model.Lesson;
import com.mstoyanov.musiclessons.model.PhoneNumber;
import com.mstoyanov.musiclessons.model.Student;

@Database(entities = {Student.class, PhoneNumber.class, Lesson.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StudentDao getStudentDao();

    public abstract PhoneNumberDao getPhoneNumberDao();

    public abstract LessonDao getLessonDao();
}