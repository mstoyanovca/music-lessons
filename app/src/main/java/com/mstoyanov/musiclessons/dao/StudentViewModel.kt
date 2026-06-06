package com.mstoyanov.musiclessons.dao

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mstoyanov.musiclessons.MusicLessonsApplication.Companion.db
import com.mstoyanov.musiclessons.entity.Student
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val id: Long? = savedStateHandle.get<Long>("id")

    val student: StateFlow<Student?> = db.studentDao().findById(id ?: 0).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val students: StateFlow<List<Student>> = db.studentDao().findAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insert(student: Student) {
        viewModelScope.launch {
            db.studentDao().insert(student)
        }
    }

    fun update(student: Student, phoneNumberIdsBeforeEditing: List<Long>) {
        viewModelScope.launch {
            db.studentDao().update(student, phoneNumberIdsBeforeEditing)
        }
    }

    fun delete(student: Student) {
        viewModelScope.launch {
            db.studentDao().delete(student)
        }
    }
}
