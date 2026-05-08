package com.medicareplus.DAO;

import com.medicareplus.config.DBConnection;
import com.medicareplus.model.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

	public boolean add(Doctor doctor) throws SQLException, ClassNotFoundException {
		String sql = "INSERT INTO doctors (user_id, specialization, qualification, experience_years, consultation_fee, availability_status, contact_phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			fill(ps, doctor);
			int rows = ps.executeUpdate();
			if (rows > 0) {
				try (ResultSet keys = ps.getGeneratedKeys()) {
					if (keys.next())
						doctor.setDoctorId(keys.getInt(1));
				}
			}
			return rows > 0;
		}
	}

	public boolean update(Doctor doctor) throws SQLException, ClassNotFoundException {
		String sql = "UPDATE doctors SET specialization=?, qualification=?, experience_years=?, consultation_fee=?, availability_status=?, contact_phone=? WHERE doctor_id=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, doctor.getSpecialization());
			ps.setString(2, doctor.getQualification());
			ps.setInt(3, doctor.getExperienceYears());
			ps.setDouble(4, doctor.getConsultationFee());
			ps.setString(5, doctor.getAvailabilityStatus());
			ps.setString(6, doctor.getContactPhone());
			ps.setInt(7, doctor.getDoctorId());
			return ps.executeUpdate() > 0;
		}
	}

	public boolean delete(int doctorId) throws SQLException, ClassNotFoundException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement("DELETE FROM doctors WHERE doctor_id=?")) {
			ps.setInt(1, doctorId);
			return ps.executeUpdate() > 0;
		}
	}

	public Doctor findById(int doctorId) throws SQLException, ClassNotFoundException {
		String sql = baseSelect() + " WHERE d.doctor_id=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, doctorId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? map(rs) : null;
			}
		}
	}

	public Doctor findByUserId(int userId) throws SQLException, ClassNotFoundException {
		String sql = baseSelect() + " WHERE d.user_id=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? map(rs) : null;
			}
		}
	}

	public List<Doctor> findAll() throws SQLException, ClassNotFoundException {
		return query(baseSelect() + " ORDER BY u.full_name", null);
	}

	public List<Doctor> findAvailable() throws SQLException, ClassNotFoundException {
		return query(baseSelect() + " WHERE d.availability_status='available' ORDER BY u.full_name", null);
	}

	public List<Doctor> searchBySpecialization(String keyword) throws SQLException, ClassNotFoundException {
		List<Doctor> doctors = new ArrayList<>();
		String sql = baseSelect()
				+ " WHERE d.specialization LIKE ? AND d.availability_status='available' ORDER BY u.full_name";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, "%" + keyword + "%");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					doctors.add(map(rs));
			}
		}
		return doctors;
	}

	public List<Doctor> search(String keyword) throws SQLException, ClassNotFoundException {
		if (keyword == null || keyword.trim().isEmpty()) {
			return findAll();
		}
		String like = "%" + keyword.trim() + "%";
		return query(baseSelect()
				+ " WHERE u.full_name LIKE ? OR d.specialization LIKE ? OR d.qualification LIKE ? OR d.contact_phone LIKE ? ORDER BY u.full_name",
				ps -> {
					ps.setString(1, like);
					ps.setString(2, like);
					ps.setString(3, like);
					ps.setString(4, like);
				});
	}

	public int count() throws SQLException, ClassNotFoundException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM doctors");
				ResultSet rs = ps.executeQuery()) {
			return rs.next() ? rs.getInt(1) : 0;
		}
	}

	private void fill(PreparedStatement ps, Doctor d) throws SQLException {
		ps.setInt(1, d.getUserId());
		ps.setString(2, d.getSpecialization());
		ps.setString(3, d.getQualification());
		ps.setInt(4, d.getExperienceYears());
		ps.setDouble(5, d.getConsultationFee());
		ps.setString(6, d.getAvailabilityStatus());
		ps.setString(7, d.getContactPhone());
	}

	private List<Doctor> query(String sql, SqlBinder binder) throws SQLException, ClassNotFoundException {
		List<Doctor> doctors = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			if (binder != null)
				binder.bind(ps);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					doctors.add(map(rs));
			}
		}
		return doctors;
	}

	private String baseSelect() {
		return "SELECT d.*, u.full_name FROM doctors d JOIN users u ON d.user_id=u.user_id";
	}

	private Doctor map(ResultSet rs) throws SQLException {
		Doctor d = new Doctor();
		d.setDoctorId(rs.getInt("doctor_id"));
		d.setUserId(rs.getInt("user_id"));
		d.setName(rs.getString("full_name"));
		d.setSpecialization(rs.getString("specialization"));
		d.setQualification(rs.getString("qualification"));
		d.setExperienceYears(rs.getInt("experience_years"));
		d.setConsultationFee(rs.getDouble("consultation_fee"));
		d.setAvailabilityStatus(rs.getString("availability_status"));
		d.setContactPhone(rs.getString("contact_phone"));
		return d;
	}

	private interface SqlBinder {
		void bind(PreparedStatement ps) throws SQLException;
	}
}
