package com.johri.myattendance.models;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentSubject {
    String subjectName, subjectID;
    HashMap<String, Boolean> datesMap;
    int percent;

    public StudentSubject() {
    }

    public StudentSubject(String subjectName, String subjectID, HashMap<String, Boolean> datesMap, int percent) {
        this.subjectName = subjectName;
        this.subjectID = subjectID;
        this.datesMap = datesMap;
        this.percent = percent;
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

    public HashMap<String, Boolean> getDatesMap() {
        return datesMap;
    }

    public void setDatesMap(HashMap<String, Boolean> datesMap) {
        this.datesMap = datesMap;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
