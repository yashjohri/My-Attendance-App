package com.johri.myattendance.models;

public class User {
    Admin admin;
    Teacher teacher;
    Student student;

    public User() {
    }

    public User(Admin admin, Teacher teacher, Student student) {
        this.admin = admin;
        this.teacher = teacher;
        this.student = student;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
