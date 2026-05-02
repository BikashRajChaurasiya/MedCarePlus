package com.medicareplus.controller;

import com.medicareplus.model.Doctor;
import com.medicareplus.model.User;
import com.medicareplus.service.DoctorService;
import com.medicareplus.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/login")
public class loginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;
    private DoctorService doctorService;

    public void init() {
        userService = new UserService();
        doctorService = new DoctorService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        loadDoctors(request);
        request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = trim(request.getParameter("email"));
        String password = request.getParameter("password");
        String selectedRole = trim(request.getParameter("role"));

        if (email == null || email.isEmpty() || password == null || password.isEmpty() || selectedRole == null || selectedRole.isEmpty()) {
            request.setAttribute("error", "Email, password, and role are required.");
            loadDoctors(request);
            request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
            return;
        }

        try {
            User user = userService.loginUser(email, password);
            if (user == null) {
                request.setAttribute("error", "Invalid email or password");
                loadDoctors(request);
                request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
                return;
            }

            String role = user.getRole() == null ? "" : user.getRole().toLowerCase();
            if (!role.equals(selectedRole.toLowerCase())) {
                request.setAttribute("error", "Selected role does not match this account.");
                loadDoctors(request);
                request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
                return;
            }

            updateRememberMeCookie(request, response, email);

            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60);

            if ("admin".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else if ("doctor".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/patient/dashboard");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Login error: " + e.getMessage());
            loadDoctors(request);
            request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
        }
    }

    private void loadDoctors(HttpServletRequest request) {
        try {
            List<Doctor> doctors = doctorService.getAvailableDoctors();
            request.setAttribute("doctors", doctors);
        } catch (Exception e) {
            request.setAttribute("doctors", List.of());
        }
    }

    private void updateRememberMeCookie(HttpServletRequest request, HttpServletResponse response, String email) {
        boolean rememberMe = "true".equalsIgnoreCase(request.getParameter("rememberMe"));
        Cookie cookie = new Cookie("rememberedEmail", rememberMe ? email : "");
        cookie.setHttpOnly(true);
        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        cookie.setMaxAge(rememberMe ? 7 * 24 * 60 * 60 : 0);
        response.addCookie(cookie);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
