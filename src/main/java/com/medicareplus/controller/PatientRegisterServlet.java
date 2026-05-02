package com.medicareplus.controller;

import com.medicareplus.DAO.PatientDAO;
import com.medicareplus.DAO.UserDAO;
import com.medicareplus.model.Patient;
import com.medicareplus.util.EncryptionUtil;
import com.medicareplus.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;

@WebServlet("/patient-register")
public class PatientRegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PatientDAO patientDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        patientDAO = new PatientDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/patient_register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Patient patient = buildPatient(request);
        request.setAttribute("patient", patient);

        String error = validate(patient);
        if (error != null) {
            forwardWithError(request, response, error);
            return;
        }

        try {
            if (userDAO.emailExists(patient.getEmail())) {
                forwardWithError(request, response, "Email address already exists. Please use another email.");
                return;
            }

            patient.setPassword(EncryptionUtil.sha256(patient.getPassword()));
            patientDAO.registerPatient(patient);

            request.getSession().setAttribute("success", "Patient registered successfully. Please log in.");
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (SQLIntegrityConstraintViolationException e) {
            forwardWithError(request, response, "Email address already exists. Please use another email.");
        } catch (Exception e) {
            forwardWithError(request, response, "Registration failed: " + e.getMessage());
        }
    }

    private Patient buildPatient(HttpServletRequest request) {
        Patient patient = new Patient();
        patient.setFullName(trim(request.getParameter("fullName")));
        patient.setEmail(trim(request.getParameter("email")));
        patient.setPassword(request.getParameter("password"));
        patient.setConfirmPassword(request.getParameter("confirmPassword"));
        patient.setContactPhone(trim(request.getParameter("phone")));
        patient.setGender(trim(request.getParameter("gender")));
        patient.setDateOfBirth(trim(request.getParameter("dateOfBirth")));
        patient.setAddress(trim(request.getParameter("address")));
        patient.setBloodGroup(trim(request.getParameter("bloodGroup")));
        patient.setEmergencyContact(trim(request.getParameter("emergencyContact")));
        patient.setMedicalHistory(trim(request.getParameter("medicalHistory")));
        return patient;
    }

    private String validate(Patient patient) {
        if (!ValidationUtil.hasText(patient.getFullName())) return "Full name is required.";
        if (!ValidationUtil.isValidEmail(patient.getEmail())) return "Valid email address is required.";
        if (!ValidationUtil.isValidPassword(patient.getPassword())) return "Password must be at least 6 characters.";
        if (!patient.getPassword().equals(patient.getConfirmPassword())) return "Password and confirm password do not match.";
        if (!isNumeric(patient.getContactPhone())) return "Phone number must contain digits only.";
        if (!isNumeric(patient.getEmergencyContact())) return "Emergency contact number must contain digits only.";
        if (!isValidGender(patient.getGender())) return "Please select a valid gender.";
        if (!isValidDateOfBirth(patient.getDateOfBirth())) return "Please enter a valid date of birth.";
        if (!ValidationUtil.hasText(patient.getAddress())) return "Address is required.";
        if (!ValidationUtil.hasText(patient.getBloodGroup())) return "Blood group is required.";
        return null;
    }

    private boolean isNumeric(String value) {
        return value != null && value.matches("\\d{7,15}");
    }

    private boolean isValidGender(String gender) {
        return "Male".equalsIgnoreCase(gender) || "Female".equalsIgnoreCase(gender) || "Other".equalsIgnoreCase(gender);
    }

    private boolean isValidDateOfBirth(String value) {
        try {
            LocalDate dob = LocalDate.parse(value);
            return dob.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {
        request.setAttribute("error", error);
        request.getRequestDispatcher("/WEB-INF/pages/patient_register.jsp").forward(request, response);
    }
}
