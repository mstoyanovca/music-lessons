package com.mstoyanov.myapplication.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddIcCall
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mstoyanov.myapplication.dao.StudentViewModel
import com.mstoyanov.myapplication.entity.NanpVisualTransformation
import com.mstoyanov.myapplication.entity.PhoneNumber
import com.mstoyanov.myapplication.entity.Student

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddStudent(navigateBack: () -> Unit, studentViewModel: StudentViewModel = viewModel()) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var phoneNumbers = rememberSaveable { mutableStateListOf<PhoneNumber>() }
    var studentIsValid by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBarImpl(navigateBack) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = studentIsValid,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(onClick = {
                    studentViewModel.insert(Student(studentId = 0L, firstName, lastName, notes))
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
            onPhoneNumbersChange = { phoneNumbers = it.toMutableStateList() }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopAppBarImpl(navigateBack: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
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

@Composable
private fun StudentContent(
    innerPadding: PaddingValues,
    firstName: String,
    lastName: String,
    notes: String,
    phoneNumbers: MutableList<PhoneNumber>,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onPhoneNumbersChange: (MutableList<PhoneNumber>) -> Unit
) {
    val numericRegex = Regex("[^0-9]")

    Column(
        modifier = Modifier.padding(innerPadding + PaddingValues(horizontal = 8.dp)),
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentType = ContentType.PersonFirstName },
            value = firstName,
            onValueChange = { if (it.length <= 24) onFirstNameChange(it) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("First Name") },
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            singleLine = true
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentType = ContentType.PersonLastName },
            value = lastName,
            onValueChange = { if (it.length <= 24) onLastNameChange(it) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Last Name") },
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            singleLine = true,
            supportingText = { Text("First or last name is required") }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = notes,
            onValueChange = { if (it.length <= 128) onNotesChange(it) },
            leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null) },
            label = { Text("Notes") },
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            maxLines = 4
        )
        Column {
            phoneNumbers.forEachIndexed { index, phoneNumber ->
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = phoneNumber.number,
                    onValueChange = {
                        val stripped = numericRegex.replace(it, "")
                        if (stripped.length >= 10) {
                            phoneNumbers[index] = phoneNumbers[index].copy(number = stripped.substring(0..9))
                        } else {
                            phoneNumbers[index] = phoneNumbers[index].copy(number = stripped)
                        }
                        onPhoneNumbersChange(phoneNumbers)
                    },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { phoneNumbers.remove(phoneNumber) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        }
                    },
                    label = { Text("Phone") },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = NanpVisualTransformation()
                )
            }
        }
        IconButton(
            modifier = Modifier.padding(top = 4.dp),
            onClick = {
                phoneNumbers.add(PhoneNumber())
                onPhoneNumbersChange(phoneNumbers)
            }
        ) {
            Icon(imageVector = Icons.Default.AddIcCall, contentDescription = null)
        }
    }
}
