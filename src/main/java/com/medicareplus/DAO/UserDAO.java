package com.medicareplus.DAO;

import com.medicareplus.config.DBConnection;
import com.medicareplus.model.PasswordResetToken;
import com.medicareplus.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
	public boolean register(User user) throws SQLException, ClassNotFoundException {
		String sql = "INSERT INTO users (full_name, email, password, role, is_active, failed_attempts, created_at) VALUES (?, ?, ?, ?, 1, 0, NOW())";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, user.getFullName());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getPassword());
			ps.setString(4, normalizeRole(user.getRole()));
			int rows = ps.executeUpdate();
			if (rows > 0) {
				try (ResultSet keys = ps.getGeneratedKeys()) {
					if (keys.next())
						user.setUserId(keys.getInt(1));
				}
			}
			return rows > 0;
		}
	}

	public User findById(int userId) throws SQLException, ClassNotFoundException {
		String sql = "SELECT * FROM users WHERE user_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapUser(rs) : null;
			}
		}
	}

	public User findByEmail(String email) throws SQLException, ClassNotFoundException {
		String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, email);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapUser(rs) : null;
			}
		}
	}

	public List<User> findAll() throws SQLException, ClassNotFoundException {
		List<User> users = new ArrayList<>();
		String sql = "SELECT * FROM users ORDER BY user_id";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next())
				users.add(mapUser(rs));
		}
		return users;
	}

	public void resetFailedAttempts(int userId) throws SQLException, ClassNotFoundException {
		String sql = "UPDATE users SET failed_attempts = 0, account_locked_until = NULL WHERE user_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.executeUpdate();
		}
	}

	public void recordLoginAttempt(Integer userId, String email, boolean success, String failureReason,
			String ipAddress) throws SQLException, ClassNotFoundException {
		String sql = "INSERT INTO login_attempts (user_id, email, success, failure_reason, ip_address, attempted_at) VALUES (?, ?, ?, ?, ?, NOW())";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			if (userId == null) {
				ps.setNull(1, Types.INTEGER);
			} else {
				ps.setInt(1, userId);
			}
			ps.setString(2, email);
			ps.setInt(3, success ? 1 : 0);
			ps.setString(4, failureReason);
			ps.setString(5, ipAddress);
			ps.executeUpdate();
		}
	}

	public void recordFailedAttempt(int userId, int maxAttempts, int lockMinutes)
			throws SQLException, ClassNotFoundException {
		String sql = "UPDATE users SET failed_attempts = failed_attempts + 1, "
				+ "account_locked_until = CASE WHEN failed_attempts + 1 >= ? THEN DATE_ADD(NOW(), INTERVAL ? MINUTE) ELSE account_locked_until END "
				+ "WHERE user_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, maxAttempts);
			ps.setInt(2, lockMinutes);
			ps.setInt(3, userId);
			ps.executeUpdate();
		}
	}

	public boolean updatePassword(int userId, String passwordHash) throws SQLException, ClassNotFoundException {
		String sql = "UPDATE users SET password = ?, failed_attempts = 0, account_locked_until = NULL WHERE user_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, passwordHash);
			ps.setInt(2, userId);
			return ps.executeUpdate() > 0;
		}
	}

	public boolean updatePassword(String email, String passwordHash) throws SQLException, ClassNotFoundException {
		User user = findByEmail(email);
		return user != null && updatePassword(user.getUserId(), passwordHash);
	}

	public boolean delete(int userId) throws SQLException, ClassNotFoundException {
		String sql = "DELETE FROM users WHERE user_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			return ps.executeUpdate() > 0;
		}
	}

	public boolean emailExists(String email) throws SQLException, ClassNotFoundException {
		return findByEmail(email) != null;
	}

	public int count() throws SQLException, ClassNotFoundException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users");
				ResultSet rs = ps.executeQuery()) {
			return rs.next() ? rs.getInt(1) : 0;
		}
	}

	public void invalidateResetTokens(int userId) throws SQLException, ClassNotFoundException {
		String sql = "UPDATE password_reset_tokens SET used_at = NOW() WHERE user_id = ? AND used_at IS NULL";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.executeUpdate();
		}
	}

	public void createResetToken(int userId, String tokenHash, int expiryMinutes)
			throws SQLException, ClassNotFoundException {
		String sql = "INSERT INTO password_reset_tokens (user_id, token_hash, expires_at, created_at) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL ? MINUTE), NOW())";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.setString(2, tokenHash);
			ps.setInt(3, expiryMinutes);
			ps.executeUpdate();
		}
	}

	public PasswordResetToken findValidResetToken(String tokenHash) throws SQLException, ClassNotFoundException {
		String sql = "SELECT * FROM password_reset_tokens WHERE token_hash = ? AND used_at IS NULL AND expires_at >= NOW()";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, tokenHash);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapResetToken(rs) : null;
			}
		}
	}

	public void markResetTokenUsed(int tokenId) throws SQLException, ClassNotFoundException {
		String sql = "UPDATE password_reset_tokens SET used_at = NOW() WHERE token_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, tokenId);
			ps.executeUpdate();
		}
	}

	private User mapUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setUserId(rs.getInt("user_id"));
		user.setFullName(rs.getString("full_name"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("password"));
		user.setRole(rs.getString("role"));
		user.setActive(rs.getInt("is_active") == 1);
		user.setFailedAttempts(rs.getInt("failed_attempts"));
		user.setAccountLockedUntil(rs.getTimestamp("account_locked_until"));
		user.setCreatedAt(rs.getTimestamp("created_at"));
		return user;
	}

	private PasswordResetToken mapResetToken(ResultSet rs) throws SQLException {
		PasswordResetToken token = new PasswordResetToken();
		token.setTokenId(rs.getInt("token_id"));
		token.setUserId(rs.getInt("user_id"));
		token.setTokenHash(rs.getString("token_hash"));
		token.setExpiresAt(rs.getTimestamp("expires_at"));
		token.setUsedAt(rs.getTimestamp("used_at"));
		token.setCreatedAt(rs.getTimestamp("created_at"));
		return token;
	}

	private String normalizeRole(String role) {
		return role == null ? "patient" : role.toLowerCase();
	}
}
