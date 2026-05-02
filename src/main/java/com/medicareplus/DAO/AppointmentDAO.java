package com.medicareplus.DAO;

import com.medicareplus.config.DBConnection;
import com.medicareplus.model.Appointment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    public boolean add(Appointment a) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, status, symptoms, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setString(3, a.getAppointmentDate());
            ps.setString(4, a.getAppointmentTime());
            ps.setString(5, a.getStatus() == null ? "pending" : a.getStatus().toLowerCase());
            ps.setString(6, a.getSymptoms());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean isSlotAvailable(int doctorId, String date, String time) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND appointment_date=? AND appointment_time=? AND status NOT IN ('rejected','cancelled')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setString(2, date);
            ps.setString(3, time);
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next() || rs.getInt(1) == 0;
            }
        }
    }

    public boolean updateStatus(int appointmentId, String status) throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE appointments SET status=? WHERE appointment_id=?")) {
            ps.setString(1, status == null ? "pending" : status.toLowerCase());
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        }
    }

    public Appointment findById(int appointmentId) throws SQLException, ClassNotFoundException {
        String sql = baseSelect() + " WHERE a.appointment_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Appointment> findByPatientId(int patientId) throws SQLException, ClassNotFoundException {
        return query(baseSelect() + " WHERE a.patient_id=" + patientId + " ORDER BY a.appointment_date DESC, a.appointment_time DESC");
    }

    public List<Appointment> findByDoctorId(int doctorId) throws SQLException, ClassNotFoundException {
        return query(baseSelect() + " WHERE a.doctor_id=" + doctorId + " ORDER BY a.appointment_date ASC, a.appointment_time ASC");
    }

    public List<Appointment> findAll() throws SQLException, ClassNotFoundException {
        return query(baseSelect() + " ORDER BY a.appointment_date DESC, a.created_at DESC");
    }

    public int count() throws SQLException, ClassNotFoundException {
        return countWhere("");
    }

    public int countPending() throws SQLException, ClassNotFoundException {
        return countWhere(" WHERE status='pending'");
    }

    private int countWhere(String where) throws SQLException, ClassNotFoundException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM appointments" + where);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private List<Appointment> query(String sql) throws SQLException, ClassNotFoundException {
        List<Appointment> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    private String baseSelect() {
        return "SELECT a.*, pu.full_name patient_name, du.full_name doctor_name, d.specialization doctor_specialization FROM appointments a JOIN patients p ON a.patient_id=p.patient_id JOIN users pu ON p.user_id=pu.user_id JOIN doctors d ON a.doctor_id=d.doctor_id JOIN users du ON d.user_id=du.user_id";
    }

    private Appointment map(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doctor_id"));
        a.setAppointmentDate(rs.getString("appointment_date"));
        a.setAppointmentTime(rs.getString("appointment_time"));
        a.setStatus(rs.getString("status"));
        a.setSymptoms(rs.getString("symptoms"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setPatientName(rs.getString("patient_name"));
        a.setDoctorName(rs.getString("doctor_name"));
        a.setDoctorSpecialization(rs.getString("doctor_specialization"));
        return a;
    }
}