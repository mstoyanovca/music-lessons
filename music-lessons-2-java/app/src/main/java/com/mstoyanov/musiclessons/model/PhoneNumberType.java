package com.mstoyanov.musiclessons.model;

public enum PhoneNumberType {

    HOME("Home"),
    CELL("Cell"),
    WORK("Work"),
    OTHER("Other");

    private final String displayValue;

    PhoneNumberType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}