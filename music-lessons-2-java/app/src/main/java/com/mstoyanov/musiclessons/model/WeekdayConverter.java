package com.mstoyanov.musiclessons.model;

import android.arch.persistence.room.TypeConverter;

public class WeekdayConverter {
    @TypeConverter
    public Weekday toWeekdayName(String value) {
        switch (value) {
            case "Monday":
                return Weekday.MONDAY;
            case "Tuesday":
                return Weekday.TUESDAY;
            case "Wednesday":
                return Weekday.WEDNESDAY;
            case "Thursday":
                return Weekday.THURSDAY;
            case "Friday":
                return Weekday.FRIDAY;
            case "Saturday":
                return Weekday.SATURDAY;
            case "Sunday":
                return Weekday.SUNDAY;
            default:
                return null;
        }
    }

    @TypeConverter
    public String toString(Weekday weekday) {
        return weekday.displayValue();
    }
}