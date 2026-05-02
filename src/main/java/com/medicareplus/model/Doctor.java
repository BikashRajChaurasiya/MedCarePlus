package com.medicareplus.model;

/**
 * Doctor Model Class - Stores doctor specific information
 */
public class Doctor {
    private int doctorId;
    private int userId;
    private String name;
    private String specialization;
    private String qualification;
    private int experienceYears;
    private double consultationFee;
    private String availability;
    private String contact;
    
    // Default Constructor
    public Doctor() {}
    
    // Parameterized Constructor
    public Doctor(int doctorId, int userId, String name, String specialization, 
                  String qualification, int experienceYears, double consultationFee, 
                  String availability, String contact) {
        this.doctorId = doctorId;
        this.userId = userId;
        this.name = name;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experienceYears = experienceYears;
        this.consultationFee = consultationFee;
        this.availability = availability;
        this.contact = contact;
    }
    
    // Constructor without doctorId
    public Doctor(int userId, String name, String specialization, String qualification, 
                  int experienceYears, double consultationFee, String availability, String contact) {
        this.userId = userId;
        this.name = name;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experienceYears = experienceYears;
        this.consultationFee = consultationFee;
        this.availability = availability;
        this.contact = contact;
    }
    
    // Getters and Setters
    public int getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public String getQualification() {
        return qualification;
    }
    
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
    
    public int getExperienceYears() {
        return experienceYears;
    }
    
    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }
    
    public double getConsultationFee() {
        return consultationFee;
    }
    
    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }
    
    public String getAvailability() {
        return availability;
    }
    
    public void setAvailability(String availability) {
        this.availability = availability;
    }
    
    public String getContact() {
        return contact;
    }
    
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    @Override
    public String toString() {
        return "Doctor{" +
                "doctorId=" + doctorId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", specialization='" + specialization + '\'' +
                ", qualification='" + qualification + '\'' +
                ", experienceYears=" + experienceYears +
                ", consultationFee=" + consultationFee +
                ", availability='" + availability + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}