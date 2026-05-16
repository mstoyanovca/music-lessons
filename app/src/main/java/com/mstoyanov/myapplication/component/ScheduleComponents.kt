package com.mstoyanov.myapplication.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mstoyanov.myapplication.LessonViewModel
import com.mstoyanov.myapplication.dao.LessonDao
import model.Lesson
import model.PhoneNumber
import model.PhoneNumberType
import model.PhoneNumberVisualTransformation

@Composable
fun Schedule(page: Int, viewModel: LessonViewModel = viewModel()) {
    val lessons by viewModel.lessonsState(page).collectAsState()
    var expanded by rememberSaveable { mutableStateOf(false) }
    var expandedId by rememberSaveable { mutableLongStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(lessons) { lesson ->
            CardContent(
                lesson,
                expanded,
                expandedId,
                onExpandedChange = { expanded = it },
                onExpandedIdChange = { expandedId = it }
            )
        }
    }
}

@Composable
private fun CardContent(
    lesson: Lesson,
    expanded: Boolean,
    expandedId: Long,
    onExpandedChange: (Boolean) -> Unit,
    onExpandedIdChange: (Long) -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ),
    ) {
        Row(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            this@ElevatedCard.LessonContent(lesson, expanded, expandedId)
            IconButton(onClick = {
                if (expandedId == lesson.lessonId || !expanded) onExpandedChange(!expanded)
                onExpandedIdChange(lesson.lessonId)
            }) {
                Icon(
                    imageVector = if (expanded && expandedId == lesson.lessonId) Filled.ExpandLess else Filled.ExpandMore,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.LessonContent(lesson: Lesson, expanded: Boolean, expandedId: Long) {
    Column(
        Modifier
            .weight(1f)
            .padding(8.dp),
    ) {
        LessonSummary(lesson)
        if (expanded && expandedId == lesson.lessonId) {
            PhoneNumbers(lesson.student.phoneNumbers)
            Fabs(lesson)
        }
    }
}

@Composable
private fun LessonSummary(lesson: Lesson) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            tint = Color.Blue
        )
        Text("${lesson.timeFrom}-${lesson.timeTo}", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.width(4.dp))
        Text(
            text = "${lesson.student.firstName} ${lesson.student.lastName}",
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PhoneNumbers(phoneNumbers: MutableList<PhoneNumber>) {
    phoneNumbers.forEach { phoneNumber ->
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                tint = Color.Blue
            )
            Spacer(Modifier.width(4.dp))
            val formattedPhoneNumber = PhoneNumberVisualTransformation()
                .filter(AnnotatedString(phoneNumber.number))
                .text
            Text(
                text = "$formattedPhoneNumber ${phoneNumber.type.displayValue()}",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(24.dp))
            if (phoneNumber.type == PhoneNumberType.CELL) {
                Icon(
                    imageVector = Icons.Default.Sms,
                    contentDescription = null,
                    tint = Color.Blue
                )
            }
        }
    }
}

@Composable
private fun Fabs(lesson: Lesson) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        SmallFloatingActionButton(
            onClick = { /* edit lesson */ }
        ) {
            Icon(Filled.Edit, contentDescription = null)
        }
        Spacer(Modifier.width(8.dp))
        SmallFloatingActionButton(
            onClick = { showDialog = true }
        ) {
            Icon(Filled.Delete, contentDescription = null)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = {
                Text(
                    text = "Are you sure you want to delete the lesson with ${lesson.student.firstName} ${lesson.student.lastName}?",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        LessonDao.delete(lesson)
                        showDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }
}
