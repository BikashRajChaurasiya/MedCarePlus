package com.medicareplus.controller;

import com.medicareplus.model.User;
import com.medicareplus.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class loginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;
    public void init() { userService = new UserService(); }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response); }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email"); String password = request.getParameter("password");
        try {
            User user = userService.loginUser(email, password);
            if (user == null) { request.setAttribute("error", "Invalid email or password"); request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response); return; }
            HttpSession session = request.getSession(); session.setAttribute("user", user); session.setMaxInactiveInterval(30 * 60);
            String role = user.getRole() == null ? "" : user.getRole().toLowerCase();
            if ("admin".equals(role)) response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            else if ("doctor".equals(role)) response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
            else response.sendRedirect(request.getContextPath() + "/patient/dashboard");
        } catch (Exception e) { request.setAttribute("error", "Login error: " + e.getMessage()); request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response); }
    }
}
