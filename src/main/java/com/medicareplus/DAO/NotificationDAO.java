package com.medicareplus.DAO;

import com.medicareplus.config.DBConnection;
import com.medicareplus.model.Notification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
	public boolean add(int userId, String message) throws SQLException, ClassNotFoundException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"INSERT INTO notifications (user_id, message, is_read, created_at) VALUES (?, ?, 0, NOW())")) {
			ps.setInt(1, userId);
			ps.setString(2, message);
			return ps.executeUpdate() > 0;
		}
	}

	public List<Notification> findUnreadByUserId(int userId) throws SQLException, ClassNotFoundException {
		List<Notification> list = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"SELECT * FROM notifications WHERE user_id=? AND is_read=0 ORDER BY created_at DESC")) {
			ps.setInt(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Notification n = new Notification();
					n.setNotificationId(rs.getInt("notification_id"));
					n.setUserId(rs.getInt("user_id"));
					n.setMessage(rs.getString("message"));
					n.setRead(rs.getInt("is_read") == 1);
					n.setCreatedAt(rs.getTimestamp("created_at"));
					list.add(n);
				}
			}
		}
		return list;
	}

	public boolean markAsRead(int notificationId) throws SQLException, ClassNotFoundException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn
						.prepareStatement("UPDATE notifications SET is_read=1 WHERE notification_id=?")) {
			ps.setInt(1, notificationId);
			return ps.executeUpdate() > 0;
		}
	}
}