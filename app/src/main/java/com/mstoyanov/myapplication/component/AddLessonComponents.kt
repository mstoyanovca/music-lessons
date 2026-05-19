package com.mstoyanov.myapplication.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import java.time.format.DateTimeFormatter

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddLesson(page: Int, navigateBack: () -> Unit, studentViewModel: StudentViewModel = viewModel(), lessonViewModel: LessonViewModel = viewModel()) {
    val weekday = weekdayFromPage(page)
    var timeFrom by remember { mutableStateOf(if (weekday == Weekday.SATURDAY) LocalTime.of(9, 0) else LocalTime.of(16, 0)) }
    var timeTo by remember { mutableStateOf(if (weekday == Weekday.SATURDAY) LocalTime.of(9, 30) else LocalTime.of(16, 30)) }
    val students by studentViewModel.students.collectAsStateWithLifecycle()
    var student = students.firstOrNull()

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
private fun WeekdayContent(weekday: Weekday?) {
    Text(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .height(48.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        text = weekday!!.value,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onPrimaryContainer
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
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TimePicker(timeFrom: LocalTime, timeTo: LocalTime, onTimeFromSelect: (LocalTime) -> Unit, onTimeToSelect: (LocalTime) -> Unit) {
    var timeFromPickerState = rememberTimePickerState(
        is24Hour = true,
        initialHour = timeFrom.hour,
        initialMinute = timeFrom.minute
    )
    var timeToPickerState = rememberTimePickerState(
        is24Hour = true,
        initialHour = timeTo.hour,
        initialMinute = timeTo.minute
    )
    var selectedTimeFrom by rememberSaveable { mutableStateOf(formattedTime(timeFrom.hour, timeFrom.minute)) }
    var selectedTimeTo by rememberSaveable { mutableStateOf(formattedTime(timeTo.hour, timeTo.minute)) }
    var showTimeFromPicker by rememberSaveable { mutableStateOf(false) }
    var showTimeToPicker by rememberSaveable { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
                .clickable(onClick = { showTimeFromPicker = true }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(start = 12.dp),
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = Color.Blue
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Time From: $selectedTimeFrom",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Row(
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
                .clickable(onClick = { showTimeToPicker = true }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(start = 12.dp),
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = Color.Blue
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Time To: $selectedTimeTo",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }

    if (showTimeFromPicker) {
        TimePickerDialog(timeFromPickerState, onConfirm = {
            timeFromPickerState = it

            val timeFrom = LocalTime.of(timeFromPickerState.hour, timeFromPickerState.minute)
            var timeTo = LocalTime.of(timeToPickerState.hour, timeToPickerState.minute)
            if (timeFrom.isAfter(timeTo)) {
                timeTo = timeFrom.plusMinutes(30)
                timeToPickerState.hour = timeTo.hour
                timeToPickerState.minute = timeTo.minute
                selectedTimeTo = formattedTime(timeToPickerState.hour, timeToPickerState.minute)
                onTimeToSelect(timeTo)
            }

            selectedTimeFrom = formattedTime(timeFromPickerState.hour, timeFromPickerState.minute)
            onTimeFromSelect(timeFrom)
            showTimeFromPicker = false
        }, onDismiss = {
            showTimeFromPicker = false
        })
    }

    if (showTimeToPicker) {
        TimePickerDialog(timeToPickerState, onConfirm = {
            timeToPickerState = it

            var timeFrom = LocalTime.of(timeFromPickerState.hour, timeFromPickerState.minute)
            val timeTo = LocalTime.of(timeToPickerState.hour, timeToPickerState.minute)
            if (timeTo.isBefore(timeFrom)) {
                timeFrom = timeTo.minusMinutes(30)
                timeFromPickerState.hour = timeFrom.hour
                timeFromPickerState.minute = timeFrom.minute
                selectedTimeFrom = formattedTime(timeFromPickerState.hour, timeFromPickerState.minute)
                onTimeFromSelect(timeFrom)
            }

            selectedTimeTo = formattedTime(timeToPickerState.hour, timeToPickerState.minute)
            onTimeToSelect(timeTo)
            showTimeToPicker = false
        }, onDismiss = {
            showTimeToPicker = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    timePickerState: TimePickerState,
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(timePickerState) }) {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1B5E20)
                )
            }
        },
        text = { TimePicker(state = timePickerState) }
    )
}

private fun formattedTime(hour: Int, minute: Int): String {
    return LocalTime
        .of(hour, minute)
        .format(DateTimeFormatter.ofPattern("HH:mm"))
}
