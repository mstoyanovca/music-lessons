package com.mstoyanov.musiclessons.model

import androidx.room.Embedded
import androidx.room.Relation

data class LessonWithStudent(
        @Embedded val lesson: Lesson,
        @Relation(parentColumn = "lesson_id", entityColumn = "student_id")
        val student: Student
)
