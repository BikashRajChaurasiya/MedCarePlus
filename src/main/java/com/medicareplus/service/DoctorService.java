package com.medicareplus.service;

import com.medicareplus.config.DBConnection;
import com.medicareplus.model.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Doctor Service Class - Handles doctor related business logic
 */
public class DoctorService {
    
    /**
     * Add a new doctor
     */
    public boolean addDoctor(Doctor doctor) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO doctors (user_id, name, specialization, availability, contact) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctor.getUserId());
            pstmt.setString(2, doctor.getName());
            pstmt.setString(3, doctor.getSpecialization());
            pstmt.setString(4, doctor.getAvailability());
            pstmt.setString(5, doctor.getContact());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get all doctors
     */
    public List<Doctor> getAllDoctors() throws SQLException, ClassNotFoundException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setDoctorId(rs.getInt("doctor_id"));
                doctor.setUserId(rs.getInt("user_id"));
                doctor.setName(rs.getString("name"));
                doctor.setSpecialization(rs.getString("specialization"));
                doctor.setAvailability(rs.getString("availability"));
                doctor.setContact(rs.getString("contact"));
                doctors.add(doctor);
            }
        }
        return doctors;
    }
    
    /**
     * Get doctor by ID
     */
    public Doctor getDoctorById(int doctorId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM doctors WHERE doctor_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setDoctorId(rs.getInt("doctor_id"));
                doctor.setUserId(rs.getInt("user_id"));
                doctor.setName(rs.getString("name"));
                doctor.setSpecialization(rs.getString("specialization"));
                doctor.setAvailability(rs.getString("availability"));
                doctor.setContact(rs.getString("contact"));
                return doctor;
            }
            return null;
        }
    }
    
    /**
     * Get doctor by User ID
     */
    public Doctor getDoctorByUserId(int userId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM doctors WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setDoctorId(rs.getInt("doctor_id"));
                doctor.setUserId(rs.getInt("user_id"));
                doctor.setName(rs.getString("name"));
                doctor.setSpecialization(rs.getString("specialization"));
                doctor.setAvailability(rs.getString("availability"));
                doctor.setContact(rs.getString("contact"));
                return doctor;
            }
            return null;
        }
    }
    
    /**
     * Get doctors by specialization
     */
    public List<Doctor> getDoctorsBySpecialization(String specialization) throws SQLException, ClassNotFoundException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE specialization LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + specialization + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setDoctorId(rs.getInt("doctor_id"));
                doctor.setUserId(rs.getInt("user_id"));
                doctor.setName(rs.getString("name"));
                doctor.setSpecialization(rs.getString("specialization"));
                doctor.setAvailability(rs.getString("availability"));
                doctor.setContact(rs.getString("contact"));
                doctors.add(doctor);
            }
        }
        return doctors;
    }
    
    /**
     * Update doctor details
     */
    public boolean updateDoctor(Doctor doctor) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE doctors SET name = ?, specialization = ?, availability = ?, contact = ? WHERE doctor_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getSpecialization());
            pstmt.setString(3, doctor.getAvailability());
            pstmt.setString(4, doctor.getContact());
            pstmt.setInt(5, doctor.getDoctorId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete doctor
     */
    public boolean deleteDoctor(int doctorId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM doctors WHERE doctor_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get total doctor count
     */
    public int getDoctorCount() throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM doctors";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}