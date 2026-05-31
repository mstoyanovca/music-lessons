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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mstoyanov.myapplication.dao.StudentViewModel
import com.mstoyanov.myapplication.entity.PhoneNumber
import com.mstoyanov.myapplication.entity.Student
import com.mstoyanov.myapplication.function.validatePhoneNumbers

@Composable
fun AddStudent(navigateBack: () -> Unit, studentViewModel: StudentViewModel = viewModel()) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var phoneNumbers = rememberSaveable { mutableStateListOf(PhoneNumber()) }
    var notes by rememberSaveable { mutableStateOf("") }
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
                        id = 0L,
                        firstName,
                        lastName,
                        notes
                    ).copy(phoneNumbers = phoneNumbers)
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
            phoneNumbers,
            notes,
            onFirstNameChange = { firstName = it },
            onLastNameChange = { lastName = it },
            onPhoneNumbersChange = {
                phoneNumbers = it.toMutableStateList()
                phoneNumbersAreValid = validatePhoneNumbers(it)
            },
            onNotesChange = { notes = it }
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
        title = { Text("Add Student") },
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
