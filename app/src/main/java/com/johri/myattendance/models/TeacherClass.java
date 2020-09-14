package com.johri.myattendance.models;

public class TeacherClass {
    String subjectName, subjectID, batchName;

    public TeacherClass() {
    }

    public TeacherClass(String subjectName, String subjectID, String batchName) {
        this.subjectName = subjectName;
        this.subjectID = subjectID;
        this.batchName = batchName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }
}
