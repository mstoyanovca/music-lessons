package com.mstoyanov.myapplication.dao

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mstoyanov.myapplication.MusicLessonsApplication.Companion.db
import com.mstoyanov.myapplication.entity.Lesson
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LessonViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val id: Long? = savedStateHandle.get<Long>("id")
    private val weekday: String? = savedStateHandle.get<String>("weekday")

    val lesson: StateFlow<Lesson?> = db.lessonDao().findById(id ?: 0).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val lessons: StateFlow<List<Lesson>> = db.lessonDao().findByWeekday(weekday ?: "").stateIn(
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
