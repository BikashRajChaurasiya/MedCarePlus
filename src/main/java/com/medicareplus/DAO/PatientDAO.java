package com.medicareplus.DAO;

import com.medicareplus.config.DBConnection;
import com.medicareplus.model.Patient;
import java.sql.*;

public class PatientDAO {
    public boolean add(Patient p) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO patients (user_id, date_of_birth, gender, blood_group, contact_phone, address, emergency_contact) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getUserId()); ps.setString(2, p.getDateOfBirth()); ps.setString(3, p.getGender()); ps.setString(4, p.getBloodGroup());
            ps.setString(5, p.getContactPhone()); ps.setString(6, p.getAddress()); ps.setString(7, p.getEmergencyContact());
            int rows = ps.executeUpdate();
            if (rows > 0) try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) p.setPatientId(keys.getInt(1)); }
            return rows > 0;
        }
    }
    public Patient findByUserId(int userId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT p.*, u.full_name FROM patients p JOIN users u ON p.user_id=u.user_id WHERE p.user_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
    public Patient findById(int patientId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT p.*, u.full_name FROM patients p JOIN users u ON p.user_id=u.user_id WHERE p.patient_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
    private Patient map(ResultSet rs) throws SQLException {
        Patient p = new Patient(); p.setPatientId(rs.getInt("patient_id")); p.setUserId(rs.getInt("user_id")); p.setName(rs.getString("full_name"));
        p.setDateOfBirth(rs.getString("date_of_birth")); p.setGender(rs.getString("gender")); p.setBloodGroup(rs.getString("blood_group"));
        p.setContactPhone(rs.getString("contact_phone")); p.setAddress(rs.getString("address")); p.setEmergencyContact(rs.getString("emergency_contact")); return p;
    }
}
