package com.mstoyanov.musiclessons.component

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
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mstoyanov.musiclessons.dao.StudentViewModel
import com.mstoyanov.musiclessons.entity.Student

@Composable
fun Students(
    onEditStudentClick: (id: Long) -> Unit,
    studentViewModel: StudentViewModel = viewModel()
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var expandedId by rememberSaveable { mutableLongStateOf(0) }
    val students by studentViewModel.students.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = students,
            key = { it.id }
        ) { student ->
            CardContent(
                student,
                expanded,
                expandedId,
                onExpandedChange = { expanded = it },
                onExpandedIdChange = { expandedId = it },
                onEditStudentClick
            )
        }
    }
}

@Composable
private fun CardContent(
    student: Student,
    expanded: Boolean,
    expandedId: Long,
    onExpandedChange: (Boolean) -> Unit,
    onExpandedIdChange: (Long) -> Unit,
    onEditStudentClick: (id: Long) -> Unit
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
            this@ElevatedCard.StudentContent(
                student,
                expanded,
                expandedId,
                onExpandedChange,
                onEditStudentClick
            )
            IconButton(onClick = {
                if (expandedId == student.id || !expanded) onExpandedChange(!expanded)
                onExpandedIdChange(student.id)
            }) {
                Icon(
                    imageVector = if (expanded && expandedId == student.id) Filled.ExpandLess else Filled.ExpandMore,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.StudentContent(
    student: Student,
    expanded: Boolean,
    expandedId: Long,
    onExpandedChange: (Boolean) -> Unit,
    onEditStudentClick: (id: Long) -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(8.dp)
    ) {
        StudentName(student.firstName, student.lastName)
        if (expanded && expandedId == student.id) {
            PhoneNumbers(student.phoneNumbers)
            Notes(student.notes)
            Fabs(student, onExpandedChange, onEditStudentClick)
        }
    }
}

@Composable
private fun StudentName(firstName: String, lastName: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Color.Blue
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "$firstName $lastName",
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Notes(notes: String) {
    if (notes.isNotEmpty()) {
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.EditNote,
                contentDescription = null,
                tint = Color.Blue
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = notes,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun Fabs(
    student: Student,
    onExpandedChange: (Boolean) -> Unit,
    onEditStudentClick: (id: Long) -> Unit
) {
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
            onClick = { onEditStudentClick(student.id) }
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
        DeleteStudentAlertDialog(
            student,
            onExpandedChange,
            onShowDialogChange = { showDialog = it })
    }
}
