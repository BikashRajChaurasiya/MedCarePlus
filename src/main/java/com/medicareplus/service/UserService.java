package com.medicareplus.service;

import com.medicareplus.DAO.PatientDAO;
import com.medicareplus.DAO.UserDAO;
import com.medicareplus.model.Patient;
import com.medicareplus.model.User;
import com.medicareplus.util.ValidationUtil;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDAO userDAO = new UserDAO();
    private final PatientDAO patientDAO = new PatientDAO();

    public boolean registerUser(User user) throws SQLException, ClassNotFoundException {
        validateUser(user);
        if (userDAO.emailExists(user.getEmail()))
            return false;
        return userDAO.register(user);
    }

    public boolean registerPatient(User user, Patient patient) throws SQLException, ClassNotFoundException {
        user.setRole("patient");
        boolean created = registerUser(user);
        if (!created)
            return false;
        patient.setUserId(user.getUserId());
        return patientDAO.add(patient);
    }

    public User loginUser(String email, String password) throws SQLException, ClassNotFoundException {
        return userDAO.login(email, password);
    }

    public User getUserByEmail(String email) throws SQLException, ClassNotFoundException {
        return userDAO.findByEmail(email);
    }

    public User getUserById(int userId) throws SQLException, ClassNotFoundException {
        return userDAO.findById(userId);
    }

    public boolean updatePassword(String email, String newPassword) throws SQLException, ClassNotFoundException {
        return userDAO.updatePassword(email, newPassword);
    }

    public List<User> getAllUsers() throws SQLException, ClassNotFoundException {
        return userDAO.findAll();
    }

    public boolean deleteUser(int userId) throws SQLException, ClassNotFoundException {
        return userDAO.delete(userId);
    }

    public boolean emailExists(String email) throws SQLException, ClassNotFoundException {
        return userDAO.emailExists(email);
    }

    public int getUserCount() throws SQLException, ClassNotFoundException {
        return userDAO.count();
    }

    public boolean isValidEmail(String email) {
        return ValidationUtil.isValidEmail(email);
    }

    public boolean isValidPassword(String password) {
        return ValidationUtil.isValidPassword(password);
    }

    private void validateUser(User user) {
        if (!ValidationUtil.hasText(user.getFullName()))
            throw new IllegalArgumentException("Full name is required");
        if (!ValidationUtil.isValidEmail(user.getEmail()))
            throw new IllegalArgumentException("Valid email is required");
        if (!ValidationUtil.isValidPassword(user.getPassword()))
            throw new IllegalArgumentException("Password must be at least 6 characters");
    }
}