package com.mstoyanov.myapplication.component

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mstoyanov.myapplication.AddLessonRoute
import com.mstoyanov.myapplication.AddStudentRoute
import com.mstoyanov.myapplication.EditStudentRoute
import com.mstoyanov.myapplication.HomeRoute
import com.mstoyanov.myapplication.R
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            MainScreenContent(
                onAddStudentClick = { navController.navigate(AddStudentRoute) },
                onEditStudentClick = { navController.navigate(EditStudentRoute(studentId = it)) },
                onAddLessonClick = { navController.navigate(AddLessonRoute(page = it)) })
        }
        composable<AddStudentRoute> {
            AddStudent(navigateBack = {
                // avoid freeze after two rapid back icon clicks:
                if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                    navController.popBackStack()
                }
            })
        }
        composable<EditStudentRoute> { backStackEntry ->
            val studentId: Long = backStackEntry.toRoute<EditStudentRoute>().studentId
            EditStudentProgressIndicator(
                studentId,
                navigateBack = {
                    // avoid freeze after two rapid back icon clicks:
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                }
            )
        }
        composable<AddLessonRoute> { backStackEntry ->
            val addLessonRoute: AddLessonRoute = backStackEntry.toRoute<AddLessonRoute>()
            AddLesson(
                page = addLessonRoute.page,
                navigateBack = {
                    // avoid freeze after two rapid back icon clicks:
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                })
        }
    }
}

@Composable
private fun MainScreenContent(
    onAddLessonClick: (page: Int) -> Unit,
    onAddStudentClick: () -> Unit,
    onEditStudentClick: (studentId: Long) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 7 })
    var studentIsAtTop by rememberSaveable { mutableStateOf(true) }
    var isAtTop2 by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBarImpl()
                PrimaryScrollableTabRowImpl(pagerState)
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = studentIsAtTop || pagerState.currentPage < 6,
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
            if (page == 6) Students(onStudentReachedTop = { studentIsAtTop = it }, onEditStudentClick)
            else Schedule(page, onReachedTop2 = { isAtTop2 = it })
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopAppBarImpl() {
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
    )
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
