package com.medicareplus.model;

/**
 * Patient Model Class - Stores patient specific information
 */
public class Patient {
    private int patientId;
    private int userId;
    private String dateOfBirth;
    private int age;
    private String gender;
    private String bloodGroup;
    private String contact;
    private String address;
    private String emergencyContact;
    
    // Default Constructor
    public Patient() {}
    
    // Full Parameterized Constructor
    public Patient(int patientId, int userId, String dateOfBirth, int age, String gender, 
                   String bloodGroup, String contact, String address, String emergencyContact) {
        this.patientId = patientId;
        this.userId = userId;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.contact = contact;
        this.address = address;
        this.emergencyContact = emergencyContact;
    }
    
    // Constructor for creating new patient (without patientId)
    public Patient(int userId, String dateOfBirth, int age, String gender, 
                   String bloodGroup, String contact, String address, String emergencyContact) {
        this.userId = userId;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.contact = contact;
        this.address = address;
        this.emergencyContact = emergencyContact;
    }
    
    // Simplified Constructor for basic patient info
    public Patient(int userId, int age, String gender, String contact, String address) {
        this.userId = userId;
        this.age = age;
        this.gender = gender;
        this.contact = contact;
        this.address = address;
    }
    
    // Getters and Setters
    public int getPatientId() { 
        return patientId; 
    }
    
    public void setPatientId(int patientId) { 
        this.patientId = patientId; 
    }
    
    public int getUserId() { 
        return userId; 
    }
    
    public void setUserId(int userId) { 
        this.userId = userId; 
    }
    
    public String getDateOfBirth() { 
        return dateOfBirth; 
    }
    
    public void setDateOfBirth(String dateOfBirth) { 
        this.dateOfBirth = dateOfBirth; 
    }
    
    public int getAge() { 
        return age; 
    }
    
    public void setAge(int age) { 
        this.age = age; 
    }
    
    public String getGender() { 
        return gender; 
    }
    
    public void setGender(String gender) { 
        this.gender = gender; 
    }
    
    public String getBloodGroup() { 
        return bloodGroup; 
    }
    
    public void setBloodGroup(String bloodGroup) { 
        this.bloodGroup = bloodGroup; 
    }
    
    public String getContact() { 
        return contact; 
    }
    
    public void setContact(String contact) { 
        this.contact = contact; 
    }
    
    public String getAddress() { 
        return address; 
    }
    
    public void setAddress(String address) { 
        this.address = address; 
    }
    
    public String getEmergencyContact() { 
        return emergencyContact; 
    }
    
    public void setEmergencyContact(String emergencyContact) { 
        this.emergencyContact = emergencyContact; 
    }
    
    @Override
    public String toString() {
        return "Patient{" +
                "patientId=" + patientId +
                ", userId=" + userId +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", contact='" + contact + '\'' +
                ", address='" + address + '\'' +
                ", emergencyContact='" + emergencyContact + '\'' +
                '}';
    }
}