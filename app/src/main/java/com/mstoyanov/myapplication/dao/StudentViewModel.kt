package com.mstoyanov.myapplication.dao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mstoyanov.myapplication.MusicLessonsApplication
import com.mstoyanov.myapplication.entity.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    fun findAll() {
        viewModelScope.launch {
            MusicLessonsApplication.db.studentDao().findAll().collect { students ->
                _students.value = students
            }
        }
    }

    /*companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val x = (this[APPLICATION_KEY] as MusicLessonsApplication).db
                StudentViewModel(
                    studentDao = (this[APPLICATION_KEY] as MusicLessonsApplication).db
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }*/
}
