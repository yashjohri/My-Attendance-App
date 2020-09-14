package com.johri.myattendance.models;

import java.util.ArrayList;
import java.util.HashMap;

public class TeacherSubject {
    String teacherName, teacherID;
    HashMap<String, ArrayList<String>> teacher_subjects;

    public TeacherSubject() {
    }

    public TeacherSubject(String teacherName, String teacherID, HashMap<String, ArrayList<String>> teacher_subjects) {
        this.teacherName = teacherName;
        this.teacherID = teacherID;
        this.teacher_subjects = teacher_subjects;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public HashMap<String, ArrayList<String>> getTeacher_subjects() {
        return teacher_subjects;
    }

    public void setTeacher_subjects(HashMap<String, ArrayList<String>> teacher_subjects) {
        this.teacher_subjects = teacher_subjects;
    }
}
