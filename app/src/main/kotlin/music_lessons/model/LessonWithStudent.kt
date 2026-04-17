package music_lessons.model

import androidx.room.Embedded

data class LessonWithStudent(@Embedded val student: Student, @Embedded val lesson: Lesson)
