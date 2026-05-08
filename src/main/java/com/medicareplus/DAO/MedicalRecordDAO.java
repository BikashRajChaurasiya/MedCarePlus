package com.medicareplus.DAO;

import com.medicareplus.config.DBConnection;
import com.medicareplus.model.MedicalRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAO {
	public boolean save(MedicalRecord r) throws SQLException, ClassNotFoundException {
		MedicalRecord existing = findByAppointmentId(r.getAppointmentId());
		return existing == null ? add(r) : updateByAppointment(r);
	}

	public boolean add(MedicalRecord r) throws SQLException, ClassNotFoundException {
		String sql = "INSERT INTO medical_records "
				+ "(appointment_id, diagnosis, prescription, suggestions, treatment_history, notes, created_at, updated_at) "
				+ "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			fillRecord(ps, r);
			return ps.executeUpdate() > 0;
		}
	}

	public boolean updateByAppointment(MedicalRecord r) throws SQLException, ClassNotFoundException {
		String sql = "UPDATE medical_records SET diagnosis=?, prescription=?, suggestions=?, treatment_history=?, notes=?, updated_at=NOW() "
				+ "WHERE appointment_id=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, r.getDiagnosis());
			ps.setString(2, r.getPrescription());
			ps.setString(3, r.getSuggestions());
			ps.setString(4, r.getTreatmentHistory());
			ps.setString(5, r.getNotes());
			ps.setInt(6, r.getAppointmentId());
			return ps.executeUpdate() > 0;
		}
	}

	public MedicalRecord findByAppointmentId(int appointmentId) throws SQLException, ClassNotFoundException {
		String sql = baseSelect() + " WHERE mr.appointment_id = ?";
		List<MedicalRecord> records = query(sql, ps -> ps.setInt(1, appointmentId));
		return records.isEmpty() ? null : records.get(0);
	}

	public List<MedicalRecord> findByPatientId(int patientId) throws SQLException, ClassNotFoundException {
		return query(baseSelect() + " WHERE a.patient_id = ? ORDER BY COALESCE(mr.updated_at, mr.created_at) DESC",
				ps -> ps.setInt(1, patientId));
	}

	public List<MedicalRecord> findByDoctorId(int doctorId) throws SQLException, ClassNotFoundException {
		return query(baseSelect() + " WHERE a.doctor_id = ? ORDER BY COALESCE(mr.updated_at, mr.created_at) DESC",
				ps -> ps.setInt(1, doctorId));
	}

	public List<MedicalRecord> findAll() throws SQLException, ClassNotFoundException {
		return query(baseSelect() + " ORDER BY COALESCE(mr.updated_at, mr.created_at) DESC", null);
	}

	public List<MedicalRecord> search(String keyword) throws SQLException, ClassNotFoundException {
		if (keyword == null || keyword.trim().isEmpty()) {
			return findAll();
		}
		String like = "%" + keyword.trim() + "%";
		String sql = baseSelect()
				+ " WHERE pu.full_name LIKE ? OR du.full_name LIKE ? OR CAST(mr.appointment_id AS CHAR) LIKE ? "
				+ "OR mr.diagnosis LIKE ? OR mr.prescription LIKE ? OR mr.suggestions LIKE ? OR mr.treatment_history LIKE ? "
				+ "ORDER BY COALESCE(mr.updated_at, mr.created_at) DESC";
		return query(sql, ps -> {
			for (int i = 1; i <= 7; i++)
				ps.setString(i, like);
		});
	}

	private void fillRecord(PreparedStatement ps, MedicalRecord r) throws SQLException {
		ps.setInt(1, r.getAppointmentId());
		ps.setString(2, r.getDiagnosis());
		ps.setString(3, r.getPrescription());
		ps.setString(4, r.getSuggestions());
		ps.setString(5, r.getTreatmentHistory());
		ps.setString(6, r.getNotes());
	}

	private List<MedicalRecord> query(String sql, SqlBinder binder) throws SQLException, ClassNotFoundException {
		List<MedicalRecord> list = new ArrayList<>();
		try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
			if (binder != null)
				binder.bind(ps);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(map(rs));
			}
		}
		return list;
	}

	private String baseSelect() {
		return "SELECT mr.*, a.patient_id, a.doctor_id, a.appointment_date, a.appointment_time, "
				+ "pu.full_name patient_name, du.full_name doctor_name " + "FROM medical_records mr "
				+ "JOIN appointments a ON mr.appointment_id = a.appointment_id "
				+ "JOIN patients p ON a.patient_id = p.patient_id " + "JOIN users pu ON p.user_id = pu.user_id "
				+ "JOIN doctors d ON a.doctor_id = d.doctor_id " + "JOIN users du ON d.user_id = du.user_id";
	}

	private MedicalRecord map(ResultSet rs) throws SQLException {
		MedicalRecord r = new MedicalRecord();
		r.setRecordId(rs.getInt("record_id"));
		r.setAppointmentId(rs.getInt("appointment_id"));
		r.setPatientId(rs.getInt("patient_id"));
		r.setDoctorId(rs.getInt("doctor_id"));
		r.setAppointmentDate(rs.getString("appointment_date"));
		r.setAppointmentTime(rs.getString("appointment_time"));
		r.setDiagnosis(rs.getString("diagnosis"));
		r.setPrescription(rs.getString("prescription"));
		r.setSuggestions(readOptional(rs, "suggestions"));
		r.setTreatmentHistory(readOptional(rs, "treatment_history"));
		r.setNotes(rs.getString("notes"));
		r.setCreatedAt(rs.getTimestamp("created_at"));
		r.setUpdatedAt(readTimestampOptional(rs, "updated_at"));
		r.setPatientName(rs.getString("patient_name"));
		r.setDoctorName(rs.getString("doctor_name"));
		return r;
	}

	private String readOptional(ResultSet rs, String column) {
		try {
			return rs.getString(column);
		} catch (SQLException ignored) {
			return "";
		}
	}

	private Timestamp readTimestampOptional(ResultSet rs, String column) {
		try {
			return rs.getTimestamp(column);
		} catch (SQLException ignored) {
			return null;
		}
	}

	private interface SqlBinder {
		void bind(PreparedStatement ps) throws SQLException;
	}
}
