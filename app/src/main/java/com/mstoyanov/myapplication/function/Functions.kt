package com.mstoyanov.myapplication.function

import com.mstoyanov.myapplication.entity.PhoneNumber
import com.mstoyanov.myapplication.entity.Weekday

fun weekdayFromPage(page: Int): Weekday? {
    return when (page) {
        0 -> Weekday.MONDAY
        1 -> Weekday.TUESDAY
        2 -> Weekday.WEDNESDAY
        3 -> Weekday.THURSDAY
        4 -> Weekday.FRIDAY
        5 -> Weekday.SATURDAY
        else -> null
    }
}

fun validatePhoneNumbers(phoneNumbers: List<PhoneNumber>): Boolean {
    return phoneNumbers.isEmpty() || phoneNumbers.map { it.number.length }.all { it == 10 }
}
