package com.medicareplus.DAO;

import com.medicareplus.config.DBConnection;
import com.medicareplus.model.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
	public boolean registerPatient(Patient p) throws SQLException, ClassNotFoundException {
		String userSql = "INSERT INTO users (full_name, email, password, role, is_active, failed_attempts, created_at) VALUES (?, ?, ?, 'patient', 1, 0, NOW())";
		String patientSql = "INSERT INTO patients (user_id, date_of_birth, gender, blood_group, contact_phone, address, emergency_contact, medical_history) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);

			int userId;
			try (PreparedStatement userPs = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
				userPs.setString(1, p.getFullName());
				userPs.setString(2, p.getEmail());
				userPs.setString(3, p.getPassword());
				userPs.executeUpdate();

				try (ResultSet keys = userPs.getGeneratedKeys()) {
					if (!keys.next()) {
						throw new SQLException("Unable to create user account.");
					}
					userId = keys.getInt(1);
				}
			}

			try (PreparedStatement patientPs = conn.prepareStatement(patientSql, Statement.RETURN_GENERATED_KEYS)) {
				patientPs.setInt(1, userId);
				patientPs.setString(2, p.getDateOfBirth());
				patientPs.setString(3, p.getGender());
				patientPs.setString(4, p.getBloodGroup());
				patientPs.setString(5, p.getContactPhone());
				patientPs.setString(6, p.getAddress());
				patientPs.setString(7, p.getEmergencyContact());
				patientPs.setString(8, p.getMedicalHistory());

				int rows = patientPs.executeUpdate();
				if (rows > 0) {
					try (ResultSet keys = patientPs.getGeneratedKeys()) {
						if (keys.next()) {
							p.setPatientId(keys.getInt(1));
						}
					}
					p.setUserId(userId);
				}
			}

			conn.commit();
			return true;
		} catch (SQLException e) {
			if (conn != null) {
				conn.rollback();
			}
			throw e;
		} finally {
			if (conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		}
	}

	public boolean add(Patient p) throws SQLException, ClassNotFoundException {
		String sql = "INSERT INTO patients (user_id, date_of_birth, gender, blood_group, contact_phone, address, emergency_contact, medical_history) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, p.getUserId());
			ps.setString(2, p.getDateOfBirth());
			ps.setString(3, p.getGender());
			ps.setString(4, p.getBloodGroup());
			ps.setString(5, p.getContactPhone());
			ps.setString(6, p.getAddress());
			ps.setString(7, p.getEmergencyContact());
			ps.setString(8, p.getMedicalHistory());
			int rows = ps.executeUpdate();
			if (rows > 0) {
				try (ResultSet keys = ps.getGeneratedKeys()) {
					if (keys.next())
						p.setPatientId(keys.getInt(1));
				}
			}
			return rows > 0;
		}
	}

	public Patient findByUserId(int userId) throws SQLException, ClassNotFoundException {
		String sql = "SELECT p.*, u.full_name FROM patients p JOIN users u ON p.user_id=u.user_id WHERE p.user_id=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? map(rs) : null;
			}
		}
	}

	public Patient findById(int patientId) throws SQLException, ClassNotFoundException {
		String sql = "SELECT p.*, u.full_name FROM patients p JOIN users u ON p.user_id=u.user_id WHERE p.patient_id=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, patientId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? map(rs) : null;
			}
		}
	}

	public List<Patient> findAll() throws SQLException, ClassNotFoundException {
		return query(
				"SELECT p.*, u.full_name, u.email FROM patients p JOIN users u ON p.user_id=u.user_id ORDER BY u.full_name",
				null);
	}

	public List<Patient> search(String keyword) throws SQLException, ClassNotFoundException {
		if (keyword == null || keyword.trim().isEmpty()) {
			return findAll();
		}
		String like = "%" + keyword.trim() + "%";
		return query(
				"SELECT p.*, u.full_name, u.email FROM patients p JOIN users u ON p.user_id=u.user_id "
						+ "WHERE u.full_name LIKE ? OR u.email LIKE ? OR p.contact_phone LIKE ? ORDER BY u.full_name",
				ps -> {
					ps.setString(1, like);
					ps.setString(2, like);
					ps.setString(3, like);
				});
	}

	private List<Patient> query(String sql, SqlBinder binder) throws SQLException, ClassNotFoundException {
		List<Patient> patients = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			if (binder != null)
				binder.bind(ps);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					patients.add(map(rs));
			}
		}
		return patients;
	}

	private Patient map(ResultSet rs) throws SQLException {
		Patient p = new Patient();
		p.setPatientId(rs.getInt("patient_id"));
		p.setUserId(rs.getInt("user_id"));
		p.setName(rs.getString("full_name"));
		try {
			p.setEmail(rs.getString("email"));
		} catch (SQLException ignored) {
			p.setEmail("");
		}
		p.setDateOfBirth(rs.getString("date_of_birth"));
		p.setGender(rs.getString("gender"));
		p.setBloodGroup(rs.getString("blood_group"));
		p.setContactPhone(rs.getString("contact_phone"));
		p.setAddress(rs.getString("address"));
		p.setEmergencyContact(rs.getString("emergency_contact"));
		try {
			p.setMedicalHistory(rs.getString("medical_history"));
		} catch (SQLException ignored) {
			p.setMedicalHistory("");
		}
		return p;
	}

	private interface SqlBinder {
		void bind(PreparedStatement ps) throws SQLException;
	}
}
