package com.mstoyanov.myapplication.dao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mstoyanov.myapplication.entity.StudentWithPhoneNumbers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentViewModel(private val studentDao: StudentDao) : ViewModel() {
    private val _students = MutableStateFlow<List<StudentWithPhoneNumbers>>(emptyList())
    val students: StateFlow<List<StudentWithPhoneNumbers>> = _students.asStateFlow()

    fun findAll() {
        viewModelScope.launch {
            studentDao.findAllWithPhoneNumbers().collect { students ->
                _students.value = students
            }
        }
    }
}
