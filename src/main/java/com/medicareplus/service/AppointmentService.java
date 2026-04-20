package com.medicareplus.service;

import com.medicareplus.config.DBConnection;
import com.medicareplus.model.Appointment;
import com.medicareplus.model.MedicalRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Appointment Service Class - Handles appointment and medical record related business logic
 */
public class AppointmentService {
    
    /**
     * Book a new appointment
     */
    public boolean bookAppointment(Appointment appointment) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, date, time, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDoctorId());
            pstmt.setString(3, appointment.getDate());
            pstmt.setString(4, appointment.getTime());
            pstmt.setString(5, "Pending");
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get all appointments for a patient
     */
    public List<Appointment> getAppointmentsByPatientId(int patientId) throws SQLException, ClassNotFoundException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, d.name as doctor_name FROM appointments a " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "WHERE a.patient_id = ? ORDER BY a.date DESC, a.time DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(rs.getInt("appointment_id"));
                appointment.setPatientId(rs.getInt("patient_id"));
                appointment.setDoctorId(rs.getInt("doctor_id"));
                appointment.setDoctorName(rs.getString("doctor_name"));
                appointment.setDate(rs.getString("date"));
                appointment.setTime(rs.getString("time"));
                appointment.setStatus(rs.getString("status"));
                appointment.setCreatedAt(rs.getTimestamp("created_at"));
                appointments.add(appointment);
            }
        }
        return appointments;
    }
    
    /**
     * Get all appointments for a doctor
     */
    public List<Appointment> getAppointmentsByDoctorId(int doctorId) throws SQLException, ClassNotFoundException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.name as patient_name FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "WHERE a.doctor_id = ? ORDER BY a.date ASC, a.time ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(rs.getInt("appointment_id"));
                appointment.setPatientId(rs.getInt("patient_id"));
                appointment.setPatientName(rs.getString("patient_name"));
                appointment.setDoctorId(rs.getInt("doctor_id"));
                appointment.setDate(rs.getString("date"));
                appointment.setTime(rs.getString("time"));
                appointment.setStatus(rs.getString("status"));
                appointment.setCreatedAt(rs.getTimestamp("created_at"));
                appointments.add(appointment);
            }
        }
        return appointments;
    }
    
    /**
     * Get all appointments (for admin)
     */
    public List<Appointment> getAllAppointments() throws SQLException, ClassNotFoundException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, d.name as doctor_name, p.name as patient_name FROM appointments a " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "ORDER BY a.date DESC, a.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(rs.getInt("appointment_id"));
                appointment.setPatientId(rs.getInt("patient_id"));
                appointment.setPatientName(rs.getString("patient_name"));
                appointment.setDoctorId(rs.getInt("doctor_id"));
                appointment.setDoctorName(rs.getString("doctor_name"));
                appointment.setDate(rs.getString("date"));
                appointment.setTime(rs.getString("time"));
                appointment.setStatus(rs.getString("status"));
                appointment.setCreatedAt(rs.getTimestamp("created_at"));
                appointments.add(appointment);
            }
        }
        return appointments;
    }
    
    /**
     * Update appointment status
     */
    public boolean updateAppointmentStatus(int appointmentId, String status) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, appointmentId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get appointment by ID
     */
    public Appointment getAppointmentById(int appointmentId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT a.*, d.name as doctor_name, p.name as patient_name FROM appointments a " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "WHERE a.appointment_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(rs.getInt("appointment_id"));
                appointment.setPatientId(rs.getInt("patient_id"));
                appointment.setPatientName(rs.getString("patient_name"));
                appointment.setDoctorId(rs.getInt("doctor_id"));
                appointment.setDoctorName(rs.getString("doctor_name"));
                appointment.setDate(rs.getString("date"));
                appointment.setTime(rs.getString("time"));
                appointment.setStatus(rs.getString("status"));
                appointment.setCreatedAt(rs.getTimestamp("created_at"));
                return appointment;
            }
            return null;
        }
    }
    
    /**
     * Add medical record
     */
    public boolean addMedicalRecord(MedicalRecord record) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO medical_records (appointment_id, diagnosis, prescription) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, record.getAppointmentId());
            pstmt.setString(2, record.getDiagnosis());
            pstmt.setString(3, record.getPrescription());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get medical records by patient ID
     */
    public List<MedicalRecord> getMedicalRecordsByPatientId(int patientId) throws SQLException, ClassNotFoundException {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT mr.*, d.name as doctor_name, p.name as patient_name, a.date as appointment_date " +
                     "FROM medical_records mr " +
                     "JOIN appointments a ON mr.appointment_id = a.appointment_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "WHERE a.patient_id = ? ORDER BY mr.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                MedicalRecord record = new MedicalRecord();
                record.setRecordId(rs.getInt("record_id"));
                record.setAppointmentId(rs.getInt("appointment_id"));
                record.setDoctorName(rs.getString("doctor_name"));
                record.setPatientName(rs.getString("patient_name"));
                record.setDiagnosis(rs.getString("diagnosis"));
                record.setPrescription(rs.getString("prescription"));
                record.setCreatedAt(rs.getTimestamp("created_at"));
                records.add(record);
            }
        }
        return records;
    }
    
    /**
     * Get total appointment count
     */
    public int getAppointmentCount() throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM appointments";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    /**
     * Get pending appointment count
     */
    public int getPendingAppointmentCount() throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE status = 'Pending'";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    /**
     * Check if appointment slot is available
     */
    public boolean isSlotAvailable(int doctorId, String date, String time) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND date = ? AND time = ? AND status != 'Rejected'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return true;
        }
    }
}