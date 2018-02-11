package com.mstoyanov.musiclessons.model;

import java.io.Serializable;

public enum Weekday implements Serializable {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    String value;

    Weekday(String value) {
        this.value = value;
    }

    public String displayValue() {
        return value;
    }
}
