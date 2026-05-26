package com.mstoyanov.myapplication.dao

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mstoyanov.myapplication.MusicLessonsApplication.Companion.db
import com.mstoyanov.myapplication.entity.Student
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val studentId: Long? = savedStateHandle.get<Long>("studentId")

    // do not turn this into fun, it stops working:
    val student: StateFlow<Student?> = db.studentDao().findById(studentId ?: 0).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // do not turn this into fun, it stops working:
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

    fun update(student: Student) {
        viewModelScope.launch {
            db.studentDao().update(student)
        }
    }

    fun delete(student: Student) {
        viewModelScope.launch {
            db.studentDao().delete(student)
        }
    }
}
