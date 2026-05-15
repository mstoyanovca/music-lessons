package com.mstoyanov.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mstoyanov.myapplication.dao.LessonDao
import com.mstoyanov.myapplication.function.weekdayFromPage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import model.Lesson

class LessonViewModel : ViewModel() {
    fun lessonsState(page: Int): StateFlow<List<Lesson>> = LessonDao
        .lessonsFlowByWeekday(weekdayFromPage(page))
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
