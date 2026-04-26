package com.laborapp.model;

public class Worker {
    public int workerId;
    public int shopNumber;
    public int sectionNumber;
    public int professionCode;
    public int grade;
    public String maritalStatus;
    public String fullName;
    
    public Worker(int workerId, int shopNumber, int sectionNumber, int professionCode,
                 int grade, String maritalStatus, String fullName) {
        this.workerId = workerId;
        this.shopNumber = shopNumber;
        this.sectionNumber = sectionNumber;
        this.professionCode = professionCode;
        this.grade = grade;
        this.maritalStatus = maritalStatus;
        this.fullName = fullName;
    }
}