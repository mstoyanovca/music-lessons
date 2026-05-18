package com.mstoyanov.myapplication.dao

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mstoyanov.myapplication.entity.Lesson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LessonViewModel(private val lessonDao: LessonDao, private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _lessons = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons: StateFlow<List<Lesson>> = _lessons.asStateFlow()

    fun findByWeekday(weekday: String) {
        viewModelScope.launch {
            lessonDao.findByWeekday(weekday).collect { lessons ->
                _lessons.value = lessons
            }
        }
    }
}
