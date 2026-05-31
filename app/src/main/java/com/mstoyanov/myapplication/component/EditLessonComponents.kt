package com.mstoyanov.myapplication.component

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun EditLessonProgressIndicator(id: Long, navigateBack: () -> Unit) {
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
            set(DEFAULT_ARGS_KEY, Bundle().apply { putLong("id", id) })
        }
    )
    val lessonState by lessonViewModel.lesson.collectAsStateWithLifecycle()

    when (val lesson = lessonState) {
        null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        else -> {
            EditLesson(
                lesson,
                navigateBack,
                onUpdateLessonClick = { lessonViewModel.update(it) })
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EditLesson(
    lesson: Lesson,
    navigateBack: () -> Unit,
    onUpdateLessonClick: (Lesson) -> Unit,
) {
    var timeFrom by rememberSaveable { mutableStateOf(lesson.timeFrom) }
    var timeTo by rememberSaveable { mutableStateOf(lesson.timeTo) }

    Scaffold(
        topBar = { TopAppBarImpl(navigateBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onUpdateLessonClick(lesson.copy(timeFrom = timeFrom, timeTo = timeTo))
                    navigateBack()
                }
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
            }
        })
    { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding + PaddingValues(all = 8.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { WeekdayContent(lesson.weekday) }
            item { StudentContent("${lesson.student.firstName} ${lesson.student.lastName}") }
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
        title = { Text("Edit Lesson") },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun StudentContent(studentName: String) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(start = 12.dp),
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Color.Blue
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = studentName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
