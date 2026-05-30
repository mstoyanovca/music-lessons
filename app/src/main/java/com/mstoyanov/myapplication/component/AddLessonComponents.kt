package com.mstoyanov.myapplication.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mstoyanov.myapplication.dao.LessonViewModel
import com.mstoyanov.myapplication.dao.StudentViewModel
import com.mstoyanov.myapplication.entity.Lesson
import com.mstoyanov.myapplication.entity.Student
import com.mstoyanov.myapplication.entity.Weekday
import com.mstoyanov.myapplication.function.weekdayFromPage
import java.time.LocalTime

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddLesson(
    page: Int,
    navigateBack: () -> Unit,
    studentViewModel: StudentViewModel = viewModel(),
    lessonViewModel: LessonViewModel = viewModel()
) {
    val students by studentViewModel.students.collectAsStateWithLifecycle()

    val weekday = weekdayFromPage(page)
    var student by rememberSaveable { mutableStateOf(students.firstOrNull()) }
    var timeFrom by rememberSaveable { mutableStateOf(if (weekday == Weekday.SATURDAY) LocalTime.of(9, 0) else LocalTime.of(16, 0)) }
    var timeTo by rememberSaveable { mutableStateOf(if (weekday == Weekday.SATURDAY) LocalTime.of(9, 30) else LocalTime.of(16, 30)) }

    Scaffold(
        topBar = { TopAppBarImpl(navigateBack) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = student != null,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        if (student != null)
                            lessonViewModel.insert(Lesson(lessonId = 0L, weekday!!, timeFrom, timeTo, student!!.studentId, student!!))
                        navigateBack()
                    }
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                }
            }
        })
    { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding + PaddingValues(all = 8.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { WeekdayContent(weekday) }
            item { StudentPicker(students, student, onStudentSelect = { student = it }) }
            item { TimePicker(timeFrom, timeTo, onTimeFromSelect = { timeFrom = it }, onTimeToSelect = { timeTo = it }) }
        }
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
        title = { Text("Add Lesson") },
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
@OptIn(ExperimentalMaterial3Api::class)
private fun StudentPicker(students: List<Student>, student: Student?, onStudentSelect: (student: Student) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val textFieldState = if (student != null) {
        rememberTextFieldState(students[0].firstName + " " + students[0].lastName)
    } else {
        rememberTextFieldState("")
    }

    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { expanded = it }) {
        TextField(
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            readOnly = true,
            state = textFieldState,
            lineLimits = TextFieldLineLimits.SingleLine,
            textStyle = MaterialTheme.typography.bodyLarge,
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
        )
        ExposedDropdownMenu(
            modifier = Modifier.crop(vertical = 8.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(8.dp)
        ) {
            students.forEachIndexed { index, student ->
                val name = student.firstName + " " + student.lastName
                DropdownMenuItem(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    onClick = {
                        textFieldState.setTextAndPlaceCursorAtEnd(name)
                        onStudentSelect(student)
                        expanded = false
                    },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    text = {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                )
                if (index < students.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

private fun Modifier.crop(vertical: Dp): Modifier = this.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(
        placeable.width,
        placeable.height - (vertical * 2).toPx().toInt()
    ) {
        placeable.placeRelative(0, -vertical.toPx().toInt())
    }
}
