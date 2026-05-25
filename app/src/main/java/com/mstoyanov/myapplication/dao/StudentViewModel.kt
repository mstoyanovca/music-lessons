package com.mstoyanov.myapplication.dao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mstoyanov.myapplication.MusicLessonsApplication.Companion.db
import com.mstoyanov.myapplication.entity.Student
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel(studentId: Long) : ViewModel() {
    // private val studentId: Long = checkNotNull(savedStateHandle["studentId"])
    // val x: Long? = savedStateHandle["studentId"]
    // private val studentId = savedStateHandle.toRoute<EditStudentRoute>().studentId

    // do not turn this into fun, it stops working:
    val student: StateFlow<Student?> = db.studentDao().findById(5L).stateIn(
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

    companion object {
        fun provideFactory(studentId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // You can pull arguments from the CreationExtras if needed
                //val savedStateHandle = createSavedStateHandle()
                StudentViewModel(studentId)
            }
        }
    }
}
