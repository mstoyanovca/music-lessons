package com.mstoyanov.musiclessons

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object AddStudentRoute

@Serializable
data class EditStudentRoute(val id: Long)

@Serializable
data class AddLessonRoute(val page: Int)

@Serializable
data class EditLessonRoute(val id: Long)
