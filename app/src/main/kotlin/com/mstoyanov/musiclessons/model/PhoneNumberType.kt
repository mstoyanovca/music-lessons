package com.mstoyanov.musiclessons.model

enum class PhoneNumberType constructor(val value: String) {
    HOME("Home"),
    CELL("Cell"),
    WORK("Work"),
    OTHER("Other");

    fun displayValue(): String {
        return value
    }
}
