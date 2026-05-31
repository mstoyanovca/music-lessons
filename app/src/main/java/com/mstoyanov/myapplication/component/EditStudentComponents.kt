package com.mstoyanov.myapplication.component

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.savedstate.SavedStateRegistryOwner
import com.mstoyanov.myapplication.dao.StudentViewModel
import com.mstoyanov.myapplication.entity.PhoneNumber
import com.mstoyanov.myapplication.entity.Student
import com.mstoyanov.myapplication.function.validatePhoneNumbers

@Composable
fun EditStudentProgressIndicator(id: Long, navigateBack: () -> Unit) {
    val studentViewModel: StudentViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                StudentViewModel(savedStateHandle = createSavedStateHandle())
            }
        },
        extras = MutableCreationExtras().apply {
            // if manually setting CreationExtras, explicitly set VIEW_MODEL_STORE_OWNER_KEY and SAVED_STATE_REGISTRY_OWNER_KEY:
            set(VIEW_MODEL_STORE_OWNER_KEY, LocalViewModelStoreOwner.current as ViewModelStoreOwner)
            set(SAVED_STATE_REGISTRY_OWNER_KEY, LocalLifecycleOwner.current as SavedStateRegistryOwner)
            set(DEFAULT_ARGS_KEY, Bundle().apply { putLong("id", id) })
        }
    )
    val studentState by studentViewModel.student.collectAsStateWithLifecycle()

    when (val student = studentState) {
        null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        else -> {
            EditStudent(
                id,
                student.firstName,
                student.lastName,
                student.phoneNumbers,
                student.notes,
                onUpdateStudentClick = { student, phoneNumberIdsBeforeEditing -> studentViewModel.update(student, phoneNumberIdsBeforeEditing) },
                navigateBack
            )
        }
    }
}

@Composable
private fun EditStudent(
    id: Long,
    firstName: String,
    lastName: String,
    phoneNumbers: List<PhoneNumber>,
    notes: String,
    onUpdateStudentClick: (Student, List<Long>) -> Unit,
    navigateBack: () -> Unit
) {
    val phoneNumberIdsBeforeEditing = rememberSaveable { phoneNumbers.map { it.id } }
    var phoneNumbersAreValid by rememberSaveable { mutableStateOf(true) }

    var id by rememberSaveable { mutableLongStateOf(id) }
    var firstName by rememberSaveable { mutableStateOf(firstName) }
    var lastName by rememberSaveable { mutableStateOf(lastName) }
    val phoneNumbers = rememberSaveable { mutableStateListOf(phoneNumbers) }
    var notes by rememberSaveable { mutableStateOf(notes) }

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
                        id = id,
                        firstName = firstName,
                        lastName = lastName,
                        notes = notes
                    )
                    student.phoneNumbers = phoneNumbers.flatten()
                    onUpdateStudentClick(student, phoneNumberIdsBeforeEditing)
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
            phoneNumbers.flatten().toMutableList(),
            notes,
            onFirstNameChange = { firstName = it },
            onLastNameChange = { lastName = it },
            onPhoneNumbersChange = {
                phoneNumbers.clear()
                phoneNumbers.addAll(mutableListOf(it))
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
