package com.mstoyanov.myapplication

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute


@Serializable
data class AddLessonRoute(val page: Int)

@Serializable
object AddStudentRoute
