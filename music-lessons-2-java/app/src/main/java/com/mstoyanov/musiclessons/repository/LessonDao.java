package com.mstoyanov.musiclessons.repository;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mstoyanov.musiclessons.model.Lesson;
import com.mstoyanov.musiclessons.model.LessonWithStudent;

import java.util.List;

@Dao
public interface LessonDao {

    @Query("select lesson.*, student.* " +
            "from lesson " +
            "inner join student on lesson.student_id = student._id " +
            "where lesson.weekday = :weekday")
    List<LessonWithStudent> findAllWithStudentByWeekday(String weekday);

    @Insert
    void insert(Lesson lesson);

    @Update
    void update(Lesson lesson);

    @Delete
    void delete(Lesson lesson);
}