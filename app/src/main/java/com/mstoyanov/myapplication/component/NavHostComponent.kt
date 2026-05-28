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
