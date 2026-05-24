package com.mstoyanov.myapplication.dao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mstoyanov.myapplication.MusicLessonsApplication
import com.mstoyanov.myapplication.entity.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {
    private val _studentState = MutableStateFlow<Student?>(null)
    val studentState: StateFlow<Student?> = _studentState.asStateFlow()

    fun findById(studentId: Long) {
        viewModelScope.launch {
            _studentState.value = MusicLessonsApplication.db.studentDao().findById(studentId)
        }
    }

    // do not turn this into fun, it stops working:
    val students: StateFlow<List<Student>> = MusicLessonsApplication.db.studentDao().findAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insert(student: Student) {
        viewModelScope.launch {
            MusicLessonsApplication.db.studentDao().insert(student)
        }
    }

    fun update(student: Student) {
        viewModelScope.launch {
            MusicLessonsApplication.db.studentDao().update(student)
        }
    }

    fun delete(student: Student) {
        viewModelScope.launch {
            MusicLessonsApplication.db.studentDao().delete(student)
        }
    }
}
