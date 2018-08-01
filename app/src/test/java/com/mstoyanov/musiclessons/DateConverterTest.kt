package com.mstoyanov.musiclessons

import com.mstoyanov.musiclessons.model.DateConverter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class DateConverterTest {
    private lateinit var format: SimpleDateFormat
    private lateinit var dateConverter: DateConverter

    @Before
    fun setUp() {
        format = SimpleDateFormat("HH:mm", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")

        dateConverter = DateConverter()
    }

    @Test
    fun return_date_from_timestamp() {
        val expectedDate: Date = format.parse("16:00")
        val actualDate = dateConverter.fromTimestamp(57_600_000)
        assertEquals(expectedDate, actualDate)
    }

    @Test
    fun return_timestamp_from_date() {
        val date: Date = format.parse("16:00")
        val timestamp: Long = dateConverter.toTimestamp(date)!!
        assertEquals(57_600_000, timestamp)
    }
}