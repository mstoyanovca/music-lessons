package com.mstoyanov.musiclessons.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.io.Serializable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(
        tableName = "phone_number",
        foreignKeys = {
                @ForeignKey(
                        entity = Student.class,
                        parentColumns = "_id",
                        childColumns = "student_id",
                        onDelete = CASCADE)},
        indices = {@Index(value = "student_id")})
public class PhoneNumber implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "phone_number_id")
    private long phoneNumberId;
    private String number;
    @TypeConverters(PhoneNumberTypeConverter.class)
    private PhoneNumberType type;
    @ColumnInfo(name = "student_id")
    private long studentId;
    @Ignore
    private boolean valid;

    public PhoneNumber() {
    }

    @Ignore
    public PhoneNumber(String number, PhoneNumberType type) {
        this.number = number;
        this.type = type;
    }

    public long getPhoneNumberId() {
        return phoneNumberId;
    }

    public void setPhoneNumberId(long phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public PhoneNumberType getType() {
        return type;
    }

    public void setType(PhoneNumberType type) {
        this.type = type;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        if (phoneNumberId != that.phoneNumberId) return false;
        return studentId == that.studentId &&
                valid == that.valid &&
                number.equals(that.number) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        int result = (int) (phoneNumberId ^ (phoneNumberId >>> 32));
        result = 31 * result + number.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (int) (studentId ^ (studentId >>> 32));
        result = 31 * result + (valid ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PhoneNumber{" +
                "phoneNumberId=" + phoneNumberId +
                ", number='" + number + '\'' +
                ", type=" + type +
                ", studentId=" + studentId +
                ", valid=" + valid +
                '}';
    }
}