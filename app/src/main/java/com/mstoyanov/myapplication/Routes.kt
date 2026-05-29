package com.mstoyanov.myapplication

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object AddStudentRoute

@Serializable
data class EditStudentRoute(val studentId: Long)

@Serializable
data class AddLessonRoute(val page: Int)

@Serializable
data class EditLessonRoute(val lessonId: Long)
