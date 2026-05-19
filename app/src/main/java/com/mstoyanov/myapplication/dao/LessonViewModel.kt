package com.mstoyanov.myapplication.dao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mstoyanov.myapplication.MusicLessonsApplication
import com.mstoyanov.myapplication.entity.Lesson
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class LessonViewModel() : ViewModel() {
    fun findByWeekday(weekday: String): StateFlow<List<Lesson>> = MusicLessonsApplication.db.lessonDao().findByWeekday(weekday).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}
