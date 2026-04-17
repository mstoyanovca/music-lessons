package com.mstoyanov.music_lessons

import com.mstoyanov.music_lessons.global.Functions.dateTimeFormatter
import com.mstoyanov.music_lessons.model.LocalTimeConverter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalTime

class LocalTimeConverterTest {
    private lateinit var converter: LocalTimeConverter

    @Before
    fun setUp() {
        converter = LocalTimeConverter()
    }

    @Test
    fun fromStringTest() {
        assertEquals(LocalTime.parse("16:00", dateTimeFormatter), converter.fromString("16:00"))
    }

    @Test
    fun toStringTest() {
        val time: LocalTime = LocalTime.now()
        assertEquals(time.format(dateTimeFormatter), converter.toString(time))
    }
}