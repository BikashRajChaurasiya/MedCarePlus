package com.medicareplus.DAO;

import com.medicareplus.config.DBConnection;
import com.medicareplus.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public boolean register(User user) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO users (full_name, email, password, role, is_active, failed_attempts, created_at) VALUES (?, ?, ?, ?, 1, 0, NOW())";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, normalizeRole(user.getRole()));
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) user.setUserId(keys.getInt(1)); }
            }
            return rows > 0;
        }
    }

    public User login(String email, String password) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND is_active = 1 AND (account_locked_until IS NULL OR account_locked_until <= NOW())";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? mapUser(rs) : null; }
        }
    }

    public User findById(int userId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? mapUser(rs) : null; }
        }
    }

    public User findByEmail(String email) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? mapUser(rs) : null; }
        }
    }

    public List<User> findAll() throws SQLException, ClassNotFoundException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(mapUser(rs));
        }
        return users;
    }

    public boolean updatePassword(String email, String password) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE users SET password = ?, failed_attempts = 0, account_locked_until = NULL WHERE email = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, password);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        }
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
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users"); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
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

    private String normalizeRole(String role) { return role == null ? "patient" : role.toLowerCase(); }
}
