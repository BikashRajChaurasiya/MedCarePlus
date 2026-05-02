package com.medicareplus.DAO;

import com.medicareplus.config.DBConnection;
import com.medicareplus.model.MedicalRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAO {
    public boolean add(MedicalRecord r) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO medical_records (appointment_id, diagnosis, prescription, notes, created_at) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getAppointmentId());
            ps.setString(2, r.getDiagnosis());
            ps.setString(3, r.getPrescription());
            ps.setString(4, r.getNotes());
            return ps.executeUpdate() > 0;
        }
    }

    public List<MedicalRecord> findByPatientId(int patientId) throws SQLException, ClassNotFoundException {
        return query(baseSelect() + " WHERE a.patient_id=" + patientId + " ORDER BY mr.created_at DESC");
    }

    public List<MedicalRecord> findByDoctorId(int doctorId) throws SQLException, ClassNotFoundException {
        return query(baseSelect() + " WHERE a.doctor_id=" + doctorId + " ORDER BY mr.created_at DESC");
    }

    private List<MedicalRecord> query(String sql) throws SQLException, ClassNotFoundException {
        List<MedicalRecord> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    private String baseSelect() {
        return "SELECT mr.*, pu.full_name patient_name, du.full_name doctor_name FROM medical_records mr JOIN appointments a ON mr.appointment_id=a.appointment_id JOIN patients p ON a.patient_id=p.patient_id JOIN users pu ON p.user_id=pu.user_id JOIN doctors d ON a.doctor_id=d.doctor_id JOIN users du ON d.user_id=du.user_id";
    }

    private MedicalRecord map(ResultSet rs) throws SQLException {
        MedicalRecord r = new MedicalRecord();
        r.setRecordId(rs.getInt("record_id"));
        r.setAppointmentId(rs.getInt("appointment_id"));
        r.setDiagnosis(rs.getString("diagnosis"));
        r.setPrescription(rs.getString("prescription"));
        r.setNotes(rs.getString("notes"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setPatientName(rs.getString("patient_name"));
        r.setDoctorName(rs.getString("doctor_name"));
        return r;
    }
}