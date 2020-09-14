package com.johri.myattendance.models;

public class Student {

    String name, roll_num, email, number, branch, sem, batch, org, org_id;

    public Student() {
    }

    public Student(String name, String roll_num, String email, String number, String branch, String sem, String batch, String org, String org_id) {
        this.name = name;
        this.roll_num = roll_num;
        this.email = email;
        this.number = number;
        this.branch = branch;
        this.sem = sem;
        this.batch = batch;
        this.org = org;
        this.org_id = org_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoll_num() {
        return roll_num;
    }

    public void setRoll_num(String roll_num) {
        this.roll_num = roll_num;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getOrg_id() {
        return org_id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }
}
