package com.mstoyanov.musiclessons.model;

import android.arch.persistence.room.TypeConverter;

public class PhoneNumberTypeConverter {

    @TypeConverter
    public PhoneNumberType toPhoneNumberType(String phoneNumberType) {
        switch (phoneNumberType) {
            case "Home":
                return PhoneNumberType.HOME;
            case "Cell":
                return PhoneNumberType.CELL;
            case "Work":
                return PhoneNumberType.WORK;
            case "Other":
                return PhoneNumberType.OTHER;
            default:
                return null;
        }
    }

    @TypeConverter
    public String toString(PhoneNumberType phoneNumberType) {
        return phoneNumberType.getDisplayValue();
    }
}