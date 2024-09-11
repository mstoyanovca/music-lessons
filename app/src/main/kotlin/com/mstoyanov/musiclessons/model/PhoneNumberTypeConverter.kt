package com.mstoyanov.musiclessons.model

import androidx.room.TypeConverter

class PhoneNumberTypeConverter {
    // do not delete this method, IntelliJ doesn't see it being used, but it is:
    @TypeConverter
    fun toPhoneNumberType(value: String): PhoneNumberType? {
        return when (value) {
            "Home" -> PhoneNumberType.HOME
            "Cell" -> PhoneNumberType.CELL
            "Work" -> PhoneNumberType.WORK
            "Other" -> PhoneNumberType.OTHER
            else -> null
        }
    }

    @TypeConverter
    fun toString(phoneNumberType: PhoneNumberType): String {
        return phoneNumberType.displayValue()
    }
}
