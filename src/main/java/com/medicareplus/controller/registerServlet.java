package com.medicareplus.controller;

import java.io.IOException;

import com.medicareplus.DAO.UserDAO;
import com.medicareplus.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class registerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/pages/register.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role"); // Admin/Doctor/Patient

        // Validation
        if (name == null || email == null || password == null || role == null ||
            name.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {

            request.setAttribute("error", "All fields are required!");
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp")
                   .forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();

        User user = new User(name, email, password, role);

        if (dao.register(user)) {
            response.sendRedirect("login");
        } else {
            request.setAttribute("error", "Registration failed (Email may already exist)");
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp")
                   .forward(request, response);
        }
    }
}