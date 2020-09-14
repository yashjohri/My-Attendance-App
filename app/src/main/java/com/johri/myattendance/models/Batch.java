package com.johri.myattendance.models;

public class Batch {
    String name, branch, sem, org_id;

    public Batch() {
    }

    public Batch(String name, String branch, String sem, String org_id) {
        this.name = name;
        this.branch = branch;
        this.sem = sem;
        this.org_id = org_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getOrg_id() {
        return org_id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }
}
