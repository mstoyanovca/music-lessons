package com.mstoyanov.myapplication.component

import android.os.Bundle
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
import com.mstoyanov.myapplication.dao.LessonViewModel
import com.mstoyanov.myapplication.entity.Lesson
import com.mstoyanov.myapplication.function.weekdayFromPage

@Composable
fun Schedule(
    page: Int,
    onEditLessonClick: (lessonId: Long) -> Unit,
) {
    val lessonViewModel: LessonViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                LessonViewModel(savedStateHandle = createSavedStateHandle())
            }
        },
        extras = MutableCreationExtras().apply {
            // if manually setting CreationExtras, explicitly set VIEW_MODEL_STORE_OWNER_KEY and SAVED_STATE_REGISTRY_OWNER_KEY:
            set(VIEW_MODEL_STORE_OWNER_KEY, LocalViewModelStoreOwner.current as ViewModelStoreOwner)
            set(SAVED_STATE_REGISTRY_OWNER_KEY, LocalLifecycleOwner.current as SavedStateRegistryOwner)
            set(DEFAULT_ARGS_KEY, Bundle().apply { putString("weekday", weekdayFromPage(page)!!.value) })
        }
    )

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
            key = { it.lessonId }
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
    onEditLessonClick: (lessonId: Long) -> Unit,
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
private fun ColumnScope.LessonContent(
    lesson: Lesson,
    expanded: Boolean,
    expandedId: Long,
    onExpandedChange: (Boolean) -> Unit,
    onEditLessonClick: (lessonId: Long) -> Unit,
) {
    Column(
        Modifier
            .weight(1f)
            .padding(8.dp),
    ) {
        LessonSummary(lesson)
        if (expanded && expandedId == lesson.lessonId) {
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
    onEditLessonClick: (lessonId: Long) -> Unit
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
                onEditLessonClick(lesson.lessonId)
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
            showDialog,
            onShowDialogChange = { showDialog = it })
    }
}
