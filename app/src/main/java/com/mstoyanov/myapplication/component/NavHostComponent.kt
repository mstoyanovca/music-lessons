package com.mstoyanov.myapplication.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mstoyanov.myapplication.AddLessonRoute
import com.mstoyanov.myapplication.AddStudentRoute
import com.mstoyanov.myapplication.EditLessonRoute
import com.mstoyanov.myapplication.EditStudentRoute
import com.mstoyanov.myapplication.HomeRoute

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NavHostImpl() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            MainScreen(
                onAddStudentClick = { navController.navigate(AddStudentRoute) },
                onEditStudentClick = { navController.navigate(EditStudentRoute(id = it)) },
                onAddLessonClick = { navController.navigate(AddLessonRoute(page = it)) },
                onEditLessonClick = { navController.navigate(EditLessonRoute(id = it)) })
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
            val id: Long = backStackEntry.toRoute<EditStudentRoute>().id
            EditStudentProgressIndicator(
                id,
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
        composable<EditLessonRoute> { backStackEntry ->
            val id: Long = backStackEntry.toRoute<EditLessonRoute>().id
            EditLessonProgressIndicator(
                id,
                navigateBack = {
                    // avoid freeze after two rapid back icon clicks:
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                })
        }
    }
}
