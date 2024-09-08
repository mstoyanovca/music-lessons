package com.mstoyanov.musiclessons.model

import androidx.room.TypeConverter

class PhoneNumberTypeConverter {
    @TypeConverter
    fun toString(phoneNumberType: PhoneNumberType): String {
        return phoneNumberType.displayValue()
    }
}
