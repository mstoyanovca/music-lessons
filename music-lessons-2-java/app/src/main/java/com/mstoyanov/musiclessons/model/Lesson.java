package com.mstoyanov.musiclessons.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(
        tableName = "lesson",
        foreignKeys = {
                @ForeignKey(
                        entity = Student.class,
                        parentColumns = "_id",
                        childColumns = "student_id",
                        onDelete = CASCADE)},
        indices = {@Index(value = "student_id")})
public class Lesson implements Comparable<Lesson>, Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "lesson_id")
    private long lessonId;
    @ColumnInfo(name = "weekday")
    @TypeConverters(WeekdayConverter.class)
    private Weekday weekday;
    @ColumnInfo(name = "time_from")
    @TypeConverters(DateConverter.class)
    private Date timeFrom;
    @ColumnInfo(name = "time_to")
    @TypeConverters(DateConverter.class)
    private Date timeTo;
    @ColumnInfo(name = "student_id")
    private long studentId;
    @Ignore
    private Student student;

    public long getLessonId() {
        return lessonId;
    }

    public void setLessonId(long lessonId) {
        this.lessonId = lessonId;
    }

    public Weekday getWeekday() {
        return weekday;
    }

    public void setWeekday(Weekday weekday) {
        this.weekday = weekday;
    }

    public Date getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Date timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Date getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Date timeTo) {
        this.timeTo = timeTo;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public int compareTo(@NonNull Lesson lesson) {
        if (timeFrom.toString().compareToIgnoreCase(lesson.timeFrom.toString()) != 0) {
            return timeFrom.toString().compareToIgnoreCase(lesson.timeFrom.toString());
        } else if (timeTo.toString().compareToIgnoreCase(lesson.timeTo.toString()) != 0) {
            return timeTo.toString().compareToIgnoreCase(lesson.timeTo.toString());
        } else if (student.getFirstName().compareToIgnoreCase(lesson.getStudent().getFirstName()) != 0) {
            return student.getFirstName().compareToIgnoreCase(lesson.getStudent().getFirstName());
        } else {
            return student.getLastName().compareToIgnoreCase(lesson.getStudent().getLastName());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return lessonId == lesson.lessonId &&
                studentId == lesson.studentId &&
                weekday == lesson.weekday &&
                timeFrom.equals(lesson.timeFrom) &&
                timeTo.equals(lesson.timeTo);
    }

    @Override
    public int hashCode() {
        int result = (int) (lessonId ^ (lessonId >>> 32));
        result = 31 * result + weekday.hashCode();
        result = 31 * result + timeFrom.hashCode();
        result = 31 * result + timeTo.hashCode();
        result = 31 * result + (int) (studentId ^ (studentId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "lessonId=" + lessonId +
                ", weekday=" + weekday +
                ", timeFrom=" + timeFrom +
                ", timeTo=" + timeTo +
                ", studentId=" + studentId +
                ", student=" + student +
                '}';
    }
}