package com.johri.myattendance.models;

public class OrgDetail {
    String id, name, adminUid, adminEmail, adminOcc;

    public OrgDetail() {
    }

    public OrgDetail(String id, String name, String adminUid, String adminEmail, String adminOcc) {
        this.id = id;
        this.name = name;
        this.adminUid = adminUid;
        this.adminEmail = adminEmail;
        this.adminOcc = adminOcc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminUid() {
        return adminUid;
    }

    public void setAdminUid(String adminUid) {
        this.adminUid = adminUid;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminOcc() {
        return adminOcc;
    }

    public void setAdminOcc(String adminOcc) {
        this.adminOcc = adminOcc;
    }
}
