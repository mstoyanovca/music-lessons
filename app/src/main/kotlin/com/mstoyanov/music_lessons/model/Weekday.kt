package com.mstoyanov.music_lessons.model

import java.io.Serializable

enum class Weekday(private var value: String) : Serializable {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    fun displayValue(): String {
        return value
    }
}
