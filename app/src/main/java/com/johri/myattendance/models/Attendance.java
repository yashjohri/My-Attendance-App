package com.johri.myattendance.models;

import java.util.ArrayList;

public class Attendance {

    String name;
    int count, total;

    public Attendance() {
    }

    public Attendance(String name, int count, int total) {
        this.name = name;
        this.count = count;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
