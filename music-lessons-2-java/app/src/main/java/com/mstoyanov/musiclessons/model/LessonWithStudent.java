package com.mstoyanov.musiclessons.model;

import android.arch.persistence.room.Embedded;

public class LessonWithStudent {
    @Embedded
    public Lesson lesson;
    @Embedded
    public Student student;
}