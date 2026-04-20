package com.medicareplus.controller;

import java.io.IOException;

import com.medicareplus.model.Patient;
import com.medicareplus.model.User;
import com.medicareplus.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Register Controller - Handles user registration
 */
@WebServlet("/register")
public class RegisterController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String ageStr = request.getParameter("age");
        String gender = request.getParameter("gender");
        String contact = request.getParameter("contact");
        String address = request.getParameter("address");
        
        // Validation
        if (name == null || name.trim().isEmpty()) {
            request.setAttribute("error", "Please enter your full name");
            request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
            return;
        }
        
        if (!userService.isValidEmail(email)) {
            request.setAttribute("error", "Please enter a valid email address");
            request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
            return;
        }
        
        if (!userService.isValidPassword(password)) {
            request.setAttribute("error", "Password must be at least 6 characters long");
            request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
            return;
        }
        
        try {
            // Check if email already exists
            if (userService.emailExists(email)) {
                request.setAttribute("error", "Email already registered. Please use a different email or login.");
                request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
                return;
            }
            
            int age = Integer.parseInt(ageStr);
            if (age < 0 || age > 150) {
                request.setAttribute("error", "Please enter a valid age");
                request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
                return;
            }
            
            // Create user and patient
            User user = new User(name, email, password, "Patient");
            Patient patient = new Patient(0, age, gender, contact, address);
            
            boolean registered = userService.registerPatient(user, patient);
            
            if (registered) {
                request.setAttribute("success", "Registration successful! Please login.");
                request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Please enter a valid age");
            request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred during registration: " + e.getMessage());
            request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
        }
    }
}