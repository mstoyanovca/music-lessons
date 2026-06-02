package com.mstoyanov.musiclessons.component

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.savedstate.SavedStateRegistryOwner
import com.mstoyanov.musiclessons.MusicLessonsApplication.Companion.db
import com.mstoyanov.musiclessons.R
import com.mstoyanov.musiclessons.dao.LessonViewModel
import com.mstoyanov.musiclessons.function.weekdayFromPage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun MainScreen(
    onAddLessonClick: (page: Int) -> Unit,
    onAddStudentClick: () -> Unit,
    onEditStudentClick: (id: Long) -> Unit,
    onEditLessonClick: (id: Long) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 7 })
    val isFabVisible = rememberSaveable { mutableStateOf(true) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // hide FAB when, scrolling down:
                if (available.y < -1) isFabVisible.value = false
                // show FAB when, scrolling up:
                if (available.y > 1) isFabVisible.value = true
                return Offset.Zero
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        topBar = {
            Column {
                TopAppBarImpl()
                PrimaryScrollableTabRowImpl(pagerState)
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisible.value,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(onClick = {
                    if (pagerState.currentPage == 6) onAddStudentClick()
                    else onAddLessonClick(pagerState.currentPage)
                }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding),
            beyondViewportPageCount = 6
        ) { page ->
            when (page) {
                6 -> {
                    Students(onEditStudentClick)
                }

                else -> {
                    val lessonViewModel: LessonViewModel = viewModel(
                        key = "tab_$page",
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
                    Schedule(lessonViewModel, onEditLessonClick)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopAppBarImpl() {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName ?: "Unknown"

    var content by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(Unit) {
        content = createContent()
    }
    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }
        }
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        title = { Text("Music Lessons") },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        },
        actions = {
            IconButton(onClick = {
                menuExpanded = !menuExpanded
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "null"
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Export Students") },
                    onClick = {
                        saveFileLauncher.launch(createFileName())
                        menuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Version: $versionName") },
                    onClick = { }
                )
            }
        },
    )
}

private suspend fun createContent(): String {
    val students = db.studentDao().findAll().first()
    val builder = StringBuilder()
    students.forEach { s ->
        builder.append("${s.firstName} ${s.lastName}\n")
        s.phoneNumbers.forEach {
            builder.append("${it.number} ${it.type.displayValue()}\n")
        }
        builder.append("${s.notes}\n\n")
    }
    return builder.toString()
}

private fun createFileName(): String {
    val now = LocalDateTime.now()
    return "students_export_${now.dayOfMonth}_${now.monthValue}_${now.year}_${now.hour}_${now.minute}"
}

@Composable
private fun PrimaryScrollableTabRowImpl(pagerState: PagerState) {
    val titles = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Students")
    val coroutineScope = rememberCoroutineScope()

    PrimaryScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        titles.forEachIndexed { index, title ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = { Text(text = title, style = MaterialTheme.typography.titleLarge) }
            )
        }
    }
}
