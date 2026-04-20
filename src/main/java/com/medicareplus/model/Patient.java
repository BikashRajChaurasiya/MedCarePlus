package com.medicareplus.model;

/**
 * Patient Model Class - Stores patient specific information
 */
public class Patient {
    private int patientId;
    private int userId;
    private int age;
    private String gender;
    private String contact;
    private String address;
    
    // Default Constructor
    public Patient() {}
    
    // Parameterized Constructor
    public Patient(int patientId, int userId, int age, String gender, String contact, String address) {
        this.patientId = patientId;
        this.userId = userId;
        this.age = age;
        this.gender = gender;
        this.contact = contact;
        this.address = address;
    }
    
    // Constructor for creating new patient
    public Patient(int userId, int age, String gender, String contact, String address) {
        this.userId = userId;
        this.age = age;
        this.gender = gender;
        this.contact = contact;
        this.address = address;
    }
    
    // Getters and Setters
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}