package com.medicareplus.model;

public class Patient {
    private int patientId;
    private int userId;
    private String fullName;
    private String email;
    private String password;
    private String confirmPassword;
    private String dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String contactPhone;
    private String address;
    private String emergencyContact;
    private String medicalHistory;

    public Patient() {}

    public Patient(int userId, String dateOfBirth, String gender, String bloodGroup,
                   String contactPhone, String address, String emergencyContact) {
        this.userId = userId;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.contactPhone = contactPhone;
        this.address = address;
        this.emergencyContact = emergencyContact;
    }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getName() { return fullName; }
    public void setName(String name) { this.fullName = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getPhone() { return contactPhone; }
    public void setPhone(String phone) { this.contactPhone = phone; }

    public String getContact() { return contactPhone; }
    public void setContact(String contact) { this.contactPhone = contact; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    public int getAge() { return 0; }
    public void setAge(int ignored) {}
}
