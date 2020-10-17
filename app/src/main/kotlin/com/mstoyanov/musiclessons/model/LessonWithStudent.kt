package com.mstoyanov.musiclessons.model

import androidx.room.Embedded

class LessonWithStudent {
    @Embedded
    lateinit var lesson: Lesson
    @Embedded
    lateinit var student: Student
}