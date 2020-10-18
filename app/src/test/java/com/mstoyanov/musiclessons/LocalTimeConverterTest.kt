package com.mstoyanov.musiclessons

import com.mstoyanov.musiclessons.model.LocalTimeConverter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LocalTimeConverterTest {
    private lateinit var formatter: DateTimeFormatter
    private lateinit var converter: LocalTimeConverter

    @Before
    fun setUp() {
        formatter = DateTimeFormatter.ofPattern("HH:mm")
        converter = LocalTimeConverter()
    }

    @Test
    fun fromStringTest() {
        assertEquals(LocalTime.parse("16:00", formatter), converter.fromString("16:00"))
    }

    @Test
    fun toStringTest() {
        val time: LocalTime = LocalTime.now()
        assertEquals(time.format(formatter), converter.toString(time))
    }
}