package com.johri.myattendance.models;

public class Subject {
    String name, id, org_id, sem, branch;

    public Subject() {
    }

    public Subject(String name, String id, String org_id, String sem, String branch) {
        this.name = name;
        this.id = id;
        this.org_id = org_id;
        this.sem = sem;
        this.branch = branch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrg_id() {
        return org_id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }

    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
