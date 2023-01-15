package com.mstoyanov.musiclessons

import com.mstoyanov.musiclessons.global.Functions.formatter
import com.mstoyanov.musiclessons.model.LocalTimeConverter
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
        assertEquals(LocalTime.parse("16:00", formatter), converter.fromString("16:00"))
    }

    @Test
    fun toStringTest() {
        val time: LocalTime = LocalTime.now()
        assertEquals(time.format(formatter), converter.toString(time))
    }
}