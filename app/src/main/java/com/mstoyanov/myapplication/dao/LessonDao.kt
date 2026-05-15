package com.mstoyanov.myapplication.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import model.Lesson
import model.Weekday
import java.time.LocalTime

object LessonDao {
    private val lessons = mutableListOf(
        Lesson(lessonId = 3L, weekday = Weekday.MONDAY, timeFrom = LocalTime.of(17, 0), timeTo = LocalTime.of(17, 30), studentId = 3L, student = StudentDao.findAll()[2]),
        Lesson(lessonId = 2L, weekday = Weekday.MONDAY, timeFrom = LocalTime.of(16, 30), timeTo = LocalTime.of(17, 0), studentId = 2L, student = StudentDao.findAll()[1]),
        Lesson(lessonId = 1L, weekday = Weekday.MONDAY, timeFrom = LocalTime.of(16, 0), timeTo = LocalTime.of(16, 30), studentId = 1L, student = StudentDao.findAll()[0]),
        Lesson(lessonId = 7L, weekday = Weekday.TUESDAY, timeFrom = LocalTime.of(17, 30), timeTo = LocalTime.of(18, 0), studentId = 5L, student = StudentDao.findAll()[4]),
        Lesson(lessonId = 6L, weekday = Weekday.TUESDAY, timeFrom = LocalTime.of(17, 0), timeTo = LocalTime.of(17, 30), studentId = 3L, student = StudentDao.findAll()[2]),
        Lesson(lessonId = 5L, weekday = Weekday.TUESDAY, timeFrom = LocalTime.of(16, 30), timeTo = LocalTime.of(17, 0), studentId = 2L, student = StudentDao.findAll()[1]),
        Lesson(lessonId = 4L, weekday = Weekday.TUESDAY, timeFrom = LocalTime.of(16, 0), timeTo = LocalTime.of(16, 30), studentId = 1L, student = StudentDao.findAll()[0])
    )

    fun lessonsFlowByWeekday(weekday: Weekday?): Flow<List<Lesson>> {
        return if (weekday == Weekday.MONDAY || weekday == Weekday.TUESDAY)
            flow {
                emit(lessons.filter { it.weekday == weekday }.sorted())
            }
        else
            flowOf(listOf())
    }

    fun save(lesson: Lesson) {
        lessons.add(lesson)
    }
}
