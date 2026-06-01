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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MusicNote
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
import com.mstoyanov.musiclessons.dao.LessonViewModel
import com.mstoyanov.musiclessons.entity.Lesson

@Composable
fun Schedule(
    lessonViewModel: LessonViewModel,
    onEditLessonClick: (id: Long) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var expandedId by rememberSaveable { mutableLongStateOf(0) }
    val lessons by lessonViewModel.lessons.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = lessons,
            key = { it.id }
        ) { lesson ->
            CardContent(
                lesson,
                expanded,
                expandedId,
                onExpandedChange = { expanded = it },
                onExpandedIdChange = { expandedId = it },
                onEditLessonClick
            )
        }
    }
}

@Composable
private fun LazyItemScope.CardContent(
    lesson: Lesson,
    expanded: Boolean,
    expandedId: Long,
    onExpandedChange: (Boolean) -> Unit,
    onExpandedIdChange: (Long) -> Unit,
    onEditLessonClick: (id: Long) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.animateItem(),
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
            this@ElevatedCard.LessonContent(
                lesson,
                expanded,
                expandedId,
                onExpandedChange,
                onEditLessonClick
            )
            IconButton(onClick = {
                if (expandedId == lesson.id || !expanded) onExpandedChange(!expanded)
                onExpandedIdChange(lesson.id)
            }) {
                Icon(
                    imageVector = if (expanded && expandedId == lesson.id) Filled.ExpandLess else Filled.ExpandMore,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.LessonContent(
    lesson: Lesson,
    expanded: Boolean,
    expandedId: Long,
    onExpandedChange: (Boolean) -> Unit,
    onEditLessonClick: (id: Long) -> Unit,
) {
    Column(
        Modifier
            .weight(1f)
            .padding(8.dp),
    ) {
        LessonSummary(lesson)
        if (expanded && expandedId == lesson.id) {
            PhoneNumbers(lesson.student.phoneNumbers)
            Fabs(lesson, onExpandedChange, onEditLessonClick)
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
private fun Fabs(
    lesson: Lesson,
    onExpandedChange: (Boolean) -> Unit,
    onEditLessonClick: (id: Long) -> Unit
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
            onClick = {
                onEditLessonClick(lesson.id)
            }
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
        DeleteLessonAlertDialog(
            lesson,
            onExpandedChange,
            onShowDialogChange = { showDialog = it })
    }
}
