package com.mstoyanov.musiclessons.model;

import java.io.Serializable;

public class Cell implements Serializable {
    private String weekDay;
    private String timeFrom;
    private String timeTo;
    private String studentName;
    private String phoneNumber;
    private static final long serialVersionUID = 1L;

    public Cell(String weekDay, String timeFrom, String timeTo, String studentName, String phoneNumber) {
        this.weekDay = weekDay;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.studentName = studentName;
        this.phoneNumber = phoneNumber;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}