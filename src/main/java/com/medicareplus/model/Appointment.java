package com.medicareplus.model;

import java.sql.Timestamp;

/**
 * Appointment Model Class - Manages appointment scheduling
 */
public class Appointment {
    private int appointmentId;
    private int patientId;
    private int doctorId;
    private String patientName;
    private String doctorName;
    private String doctorSpecialization;
    private String appointmentDate;  // matches: appointment_date
    private String appointmentTime;  // matches: appointment_time
    private String status;           // matches: status (pending, approved, rejected, completed)
    private String symptoms;         // matches: symptoms
    private Timestamp createdAt;     // matches: created_at
    
    // Default Constructor
    public Appointment() {}
    
    // Full Parameterized Constructor
    public Appointment(int appointmentId, int patientId, int doctorId, 
                       String appointmentDate, String appointmentTime, 
                       String status, String symptoms, Timestamp createdAt) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.symptoms = symptoms;
        this.createdAt = createdAt;
    }
    
    // Constructor for booking appointment
    public Appointment(int patientId, int doctorId, String appointmentDate, 
                       String appointmentTime, String status, String symptoms) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.symptoms = symptoms;
    }
    
    // Simplified constructor for booking appointment (without symptoms)
    public Appointment(int patientId, int doctorId, String appointmentDate, 
                       String appointmentTime, String status) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }
    
    // Constructor with doctor name for display
    public Appointment(int appointmentId, String doctorName, String appointmentDate, 
                       String appointmentTime, String status) {
        this.appointmentId = appointmentId;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }
    
    // Getters and Setters
    public int getAppointmentId() { 
        return appointmentId; 
    }
    
    public void setAppointmentId(int appointmentId) { 
        this.appointmentId = appointmentId; 
    }
    
    public int getPatientId() { 
        return patientId; 
    }
    
    public void setPatientId(int patientId) { 
        this.patientId = patientId; 
    }
    
    public int getDoctorId() { 
        return doctorId; 
    }
    
    public void setDoctorId(int doctorId) { 
        this.doctorId = doctorId; 
    }
    
    public String getPatientName() { 
        return patientName; 
    }
    
    public void setPatientName(String patientName) { 
        this.patientName = patientName; 
    }
    
    public String getDoctorName() { 
        return doctorName; 
    }
    
    public void setDoctorName(String doctorName) { 
        this.doctorName = doctorName; 
    }
    
    public String getDoctorSpecialization() { 
        return doctorSpecialization; 
    }
    
    public void setDoctorSpecialization(String doctorSpecialization) { 
        this.doctorSpecialization = doctorSpecialization; 
    }
    
    public String getAppointmentDate() { 
        return appointmentDate; 
    }
    
    public void setAppointmentDate(String appointmentDate) { 
        this.appointmentDate = appointmentDate; 
    }
    
    // Alias method for JSP compatibility (maps to appointmentDate)
    public String getDate() { 
        return appointmentDate; 
    }
    
    // Alias method for JSP compatibility
    public void setDate(String date) { 
        this.appointmentDate = date; 
    }
    
    public String getAppointmentTime() { 
        return appointmentTime; 
    }
    
    public void setAppointmentTime(String appointmentTime) { 
        this.appointmentTime = appointmentTime; 
    }
    
    // Alias method for JSP compatibility (maps to appointmentTime)
    public String getTime() { 
        return appointmentTime; 
    }
    
    // Alias method for JSP compatibility
    public void setTime(String time) { 
        this.appointmentTime = time; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public String getSymptoms() { 
        return symptoms; 
    }
    
    public void setSymptoms(String symptoms) { 
        this.symptoms = symptoms; 
    }
    
    public Timestamp getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(Timestamp createdAt) { 
        this.createdAt = createdAt; 
    }
    
    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", doctorName='" + doctorName + '\'' +
                ", doctorSpecialization='" + doctorSpecialization + '\'' +
                ", appointmentDate='" + appointmentDate + '\'' +
                ", appointmentTime='" + appointmentTime + '\'' +
                ", status='" + status + '\'' +
                ", symptoms='" + symptoms + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}