package com.medicareplus.controller;

import com.medicareplus.model.Patient;
import com.medicareplus.model.User;
import com.medicareplus.service.PatientService;
import com.medicareplus.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class registerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;
    private PatientService patientService;
    public void init() { userService = new UserService(); patientService = new PatientService(); }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response); }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name"); String email = request.getParameter("email"); String password = request.getParameter("password"); String confirm = request.getParameter("confirmPassword");
        String role = request.getParameter("role"); if (role == null || role.trim().isEmpty()) role = "patient";
        try {
            if (password == null || !password.equals(confirm)) { request.setAttribute("error", "Password and confirm password do not match"); request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response); return; }
            User user = new User(name, email, password, role);
            boolean created = userService.registerUser(user);
            if (!created) { request.setAttribute("error", "Registration failed. Email may already exist."); request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response); return; }
            if ("patient".equalsIgnoreCase(role)) {
                Patient patient = new Patient(); patient.setUserId(user.getUserId()); patient.setGender(request.getParameter("gender")); patient.setContactPhone(request.getParameter("contact")); patient.setAddress(request.getParameter("address")); patientService.addPatient(patient);
            }
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) { request.setAttribute("error", "Registration error: " + e.getMessage()); request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response); }
    }
}
