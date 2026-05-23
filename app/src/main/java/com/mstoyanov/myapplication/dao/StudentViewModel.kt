package com.mstoyanov.myapplication.dao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mstoyanov.myapplication.MusicLessonsApplication
import com.mstoyanov.myapplication.entity.Student
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {
    fun findById(studentId: Long): StateFlow<Student> {
        return MusicLessonsApplication.db.studentDao().findById(studentId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Student()
            )
    }

    fun findAll(): StateFlow<List<Student>> {
        return MusicLessonsApplication.db.studentDao().findAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

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
