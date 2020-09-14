package com.johri.myattendance.models;

public class AttendanceItem {
    String studentName, studentID;
    boolean present;

    public AttendanceItem() {
    }

    public AttendanceItem(String studentName, String studentID, boolean present) {
        this.studentName = studentName;
        this.studentID = studentID;
        this.present = present;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
