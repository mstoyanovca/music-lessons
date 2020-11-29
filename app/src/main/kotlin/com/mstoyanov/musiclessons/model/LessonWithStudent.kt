package com.mstoyanov.musiclessons.model

import androidx.room.Embedded

data class LessonWithStudent(@Embedded val student: Student, @Embedded val lesson: Lesson)
