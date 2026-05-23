package com.mstoyanov.myapplication.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mstoyanov.myapplication.dao.StudentViewModel
import com.mstoyanov.myapplication.entity.Student
import com.mstoyanov.myapplication.function.validatePhoneNumbers

@Composable
fun EditStudent(studentId: Long, navigateBack: () -> Unit, studentViewModel: StudentViewModel = viewModel()) {
    val student = studentViewModel.findById(studentId).collectAsState()
    var firstName by rememberSaveable { mutableStateOf(student.value?.firstName.orEmpty()) }
    var lastName by rememberSaveable { mutableStateOf(student.value?.lastName.orEmpty()) }
    var phoneNumbers = rememberSaveable { student.value?.phoneNumbers?.toMutableList() ?: mutableListOf() }
    var notes by rememberSaveable { mutableStateOf(student.value?.notes.orEmpty()) }
    var phoneNumbersAreValid by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBarImpl(navigateBack) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = (firstName.isNotEmpty() || lastName.isNotEmpty()) && phoneNumbersAreValid,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                FloatingActionButton(onClick = {
                    val student = Student(
                        studentId = 0L,
                        firstName,
                        lastName,
                        notes
                    ).copy(phoneNumbers = phoneNumbers.filter { it.number.isNotEmpty() })
                    studentViewModel.insert(student)
                    navigateBack()
                }) {
                    Icon(Icons.Default.Save, contentDescription = null)
                }
            }
        }) { innerPadding ->
        StudentContent(
            innerPadding,
            firstName,
            lastName,
            notes,
            phoneNumbers,
            onFirstNameChange = { firstName = it },
            onLastNameChange = { lastName = it },
            onNotesChange = { notes = it },
            onPhoneNumbersChange = {
                phoneNumbers = it.toMutableStateList()
                phoneNumbersAreValid = validatePhoneNumbers(it)
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopAppBarImpl(navigateBack: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        title = { Text("Edit Student") },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
    )
}
