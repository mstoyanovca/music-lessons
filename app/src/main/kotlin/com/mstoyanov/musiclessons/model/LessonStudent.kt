package com.mstoyanov.musiclessons.model

import android.arch.persistence.room.Embedded

class LessonStudent {
    @Embedded
    lateinit var lesson: Lesson
    @Embedded
    lateinit var student: Student
}