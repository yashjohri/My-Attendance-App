package com.johri.myattendance.models;

public class ClassAttendanceItem implements Comparable<ClassAttendanceItem>{

    private String studentName, studentID;
    private int percent;

    public ClassAttendanceItem() {
    }

    public ClassAttendanceItem(String studentName, String studentID, int percent) {
        this.studentName = studentName;
        this.studentID = studentID;
        this.percent = percent;
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

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    @Override
    public int compareTo(ClassAttendanceItem o) {
        return Integer.parseInt(this.studentID) - Integer.parseInt(o.studentID);
    }
}
