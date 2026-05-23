package com.mstoyanov.myapplication

import com.mstoyanov.myapplication.entity.Student
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute


@Serializable
data class AddLessonRoute(val page: Int)

@Serializable
object AddStudentRoute

@Serializable
data class EditStudentRoute(val student: Student)
