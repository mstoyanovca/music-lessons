package com.mstoyanov.myapplication.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddIcCall
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mstoyanov.myapplication.dao.StudentViewModel
import com.mstoyanov.myapplication.entity.NanpVisualTransformation
import com.mstoyanov.myapplication.entity.PhoneNumber
import com.mstoyanov.myapplication.entity.PhoneNumberType
import com.mstoyanov.myapplication.entity.Student
import com.mstoyanov.myapplication.function.phoneNumbersAreValid

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddStudent(navigateBack: () -> Unit, studentViewModel: StudentViewModel = viewModel()) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var phoneNumbers = rememberSaveable { mutableStateListOf(PhoneNumber()) }
    var notes by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBarImpl(navigateBack) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = (firstName.isNotEmpty() || lastName.isNotEmpty()) && phoneNumbersAreValid(phoneNumbers),
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(onClick = {
                    val student = Student(
                        studentId = 0L,
                        firstName,
                        lastName,
                        notes
                    ).copy(phoneNumbers = phoneNumbers.filter { it.number.isNotEmpty() })
                    studentViewModel.insert(student)
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
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
            .verticalScroll(scrollState),
    ) {
        FirstName(firstName, onFirstNameChange)
        LastName(lastName, onLastNameChange)
        PhoneNumbers(phoneNumbers, onPhoneNumbersChange)
        AddPhoneNumber(phoneNumbers, onPhoneNumbersChange)
        Notes(notes, onNotesChange)
    }
}

@Composable
private fun FirstName(
    firstName: String,
    onFirstNameChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentType = ContentType.PersonFirstName },
        shape = RoundedCornerShape(8.dp),
        value = firstName,
        onValueChange = { if (it.length <= 24) onFirstNameChange(it) },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        label = { Text("First Name") },
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        singleLine = true
    )
}

@Composable
private fun LastName(
    lastName: String,
    onLastNameChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentType = ContentType.PersonLastName },
        shape = RoundedCornerShape(8.dp),
        value = lastName,
        onValueChange = { if (it.length <= 24) onLastNameChange(it) },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        label = { Text("Last Name") },
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        singleLine = true
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PhoneNumbers(
    phoneNumbers: MutableList<PhoneNumber>,
    onPhoneNumbersChange: (MutableList<PhoneNumber>) -> Unit
) {
    Column {
        phoneNumbers.forEachIndexed { index, phoneNumber ->
            var expanded by rememberSaveable { mutableStateOf(false) }
            var isFocused by rememberSaveable { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(8.dp),
                    value = phoneNumber.number,
                    onValueChange = { it ->
                        val stripped = it
                            .filter { it.isDigit() }
                            .substring(0..(it.length - 1).coerceAtMost(9))
                        phoneNumbers[index] = phoneNumbers[index].copy(number = stripped)
                        onPhoneNumbersChange(phoneNumbers)
                    },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { phoneNumbers.remove(phoneNumber) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.Blue
                            )
                        }
                    },
                    label = { Text("Phone") },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = NanpVisualTransformation()
                )
                ExposedDropdownMenuBox(
                    modifier = Modifier.weight(1f),
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                            .onFocusChanged { isFocused = it.isFocused },
                        shape = RoundedCornerShape(8.dp),
                        readOnly = true,
                        value = phoneNumber.type.displayValue(),
                        onValueChange = { },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        label = {
                            if (isFocused) {
                                Text("Type")
                            }
                        },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        PhoneNumberType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayValue()) },
                                onClick = {
                                    phoneNumbers[index] = phoneNumbers[index].copy(type = type)
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddPhoneNumber(
    phoneNumbers: MutableList<PhoneNumber>,
    onPhoneNumbersChange: (MutableList<PhoneNumber>) -> Unit
) {
    IconButton(
        modifier = Modifier.padding(top = 8.dp),
        onClick = {
            phoneNumbers.add(PhoneNumber())
            onPhoneNumbersChange(phoneNumbers)
        },
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = Color.Blue,
            disabledContentColor = Color.Gray
        ),
        enabled = phoneNumbers.isEmpty() || phoneNumbers.map { it.number.length }.all { it == 10 }
    ) {
        Icon(
            imageVector = Icons.Default.AddIcCall,
            contentDescription = null
        )
    }
}

@Composable
private fun Notes(
    notes: String,
    onNotesChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        value = notes,
        onValueChange = { if (it.length <= 128) onNotesChange(it) },
        leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null) },
        label = { Text("Notes") },
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        maxLines = 4
    )
}
