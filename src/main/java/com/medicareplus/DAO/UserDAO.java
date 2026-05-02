package com.medicareplus.DAO;


import java.sql.*;
import com.medicareplus.model.*;
import com.medicareplus.config.*;

public class UserDAO {

    // ✅ REGISTER USER
    public boolean register(User user) {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO users(name, email, password, role) VALUES(?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());

            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Email already exists!");
        } catch (Exception e) {
            e.printStackTrace();
        }
      
        return false;
    }

    // ✅ LOGIN USER
    public User login(String email, String password) {
        try {
            Connection conn = DBConnection.getConnection();
            System.out.println("Login Called!");
            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at")
                );
            }

        } catch (Exception e) {
        	System.out.println("Login Error!");
            e.printStackTrace();
        }
        return null;
    }
}