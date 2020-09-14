package com.johri.myattendance.models;

public class Admin {
    String name, email, occupation, org, org_id;

    public Admin() {
    }

    public Admin(String name, String email, String occupation, String org, String org_id) {
        this.name = name;
        this.email = email;
        this.occupation = occupation;
        this.org = org;
        this.org_id = org_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
