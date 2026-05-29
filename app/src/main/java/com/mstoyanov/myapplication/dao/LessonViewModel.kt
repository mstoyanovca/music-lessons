package com.mstoyanov.myapplication.dao

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mstoyanov.myapplication.MusicLessonsApplication.Companion.db
import com.mstoyanov.myapplication.entity.Lesson
import com.mstoyanov.myapplication.entity.Student
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LessonViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val lessonId: Long? = savedStateHandle.get<Long>("lessonId")

    val lesson: StateFlow<Lesson?> = db.lessonDao().findById(lessonId ?: 0).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // TODO: turn into val
    fun findByWeekday(weekday: String): StateFlow<List<Lesson>> = db.lessonDao().findByWeekday(weekday).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insert(lesson: Lesson) {
        viewModelScope.launch {
            db.lessonDao().insert(lesson)
        }
    }

    fun update(lesson: Lesson) {
        viewModelScope.launch {
            db.lessonDao().update(lesson)
        }
    }

    fun delete(lesson: Lesson) {
        viewModelScope.launch {
            db.lessonDao().delete(lesson)
        }
    }
}
