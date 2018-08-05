package com.mstoyanov.musiclessons

import com.mstoyanov.musiclessons.model.Student
import org.junit.Assert
import org.junit.Test

class StudentTest {

    @Test
    fun compare_students_by_first_name() {
        val student1 = Student(1L, "Johm", "Smith", "", "", listOf())
        val student2 = Student(2L, "John", "Smith", "", "", listOf())

        Assert.assertEquals(student1.compareTo(student2), -1)
    }

    @Test
    fun compare_students_by_last_name() {
        val student1 = Student(1L, "John", "Smitg", "", "", listOf())
        val student2 = Student(1L, "John", "Smith", "", "", listOf())

        Assert.assertEquals(student1.compareTo(student2), -1)
    }
}