package com.mstoyanov.musiclessons.model

import java.io.Serializable

enum class Weekday constructor(internal var value: String) : Serializable {
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