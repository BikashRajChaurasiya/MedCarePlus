package com.medicareplus.controller;



import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet implementation class loginServlet
 */
import com.medicareplus.DAO.UserDAO;
import com.medicareplus.model.User;

@WebServlet("/login")
public class loginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/pages/login.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Email and Password are required!");
            request.getRequestDispatcher("/WEB-INF/pages/login.jsp")
                   .forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.login(email, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            // Role-based redirect
            if (user.getRole().equalsIgnoreCase("Admin")) {
                response.sendRedirect("admin-dashboard");
            } else if (user.getRole().equalsIgnoreCase("Doctor")) {
                response.sendRedirect("doctor-dashboard");
            } else {
                response.sendRedirect("patient-dashboard");
            }

        } else {
            request.setAttribute("error", "Invalid email or password");
            request.getRequestDispatcher("/WEB-INF/pages/login.jsp")
                   .forward(request, response);
        }
    }
}