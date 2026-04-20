package com.medicareplus.controller;

import java.io.IOException;
import java.util.List;

import com.medicareplus.model.Appointment;
import com.medicareplus.model.Doctor;
import com.medicareplus.model.User;
import com.medicareplus.service.AppointmentService;
import com.medicareplus.service.DoctorService;
import com.medicareplus.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Admin Controller - Handles all admin operations
 */
@WebServlet("/admin")
public class AdminController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;
    private DoctorService doctorService;
    private AppointmentService appointmentService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
        doctorService = new DoctorService();
        appointmentService = new AppointmentService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	request.getRequestDispatcher("/WEB-INF/pages/adminDashboard.jsp").forward(request, response);
    	
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        if (!"Admin".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String path = request.getPathInfo();
        
        if (path == null || "/dashboard".equals(path)) {
            showDashboard(request, response);
        } else if ("/doctors".equals(path)) {
            manageDoctors(request, response);
        } else if ("/appointments".equals(path)) {
            viewAppointments(request, response);
        } else if ("/users".equals(path)) {
            manageUsers(request, response);
        } else {
            showDashboard(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	request.getRequestDispatcher("//pages/adminDashboard.jsp").forward(request, response);
        
        String path = request.getPathInfo();
        
        if ("/addDoctor".equals(path)) {
            addDoctor(request, response);
        } else if ("/updateAppointment".equals(path)) {
            updateAppointment(request, response);
        } else if ("/deleteUser".equals(path)) {
            deleteUser(request, response);
        } else if ("/deleteDoctor".equals(path)) {
            deleteDoctor(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        }
    }
    
    private void showDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int totalDoctors = doctorService.getDoctorCount();
            int totalAppointments = appointmentService.getAppointmentCount();
            int pendingAppointments = appointmentService.getPendingAppointmentCount();
            int totalUsers = userService.getAllUsers().size();
            
            List<Appointment> recentAppointments = appointmentService.getAllAppointments();
            if (recentAppointments.size() > 10) {
                recentAppointments = recentAppointments.subList(0, 10);
            }
            
            request.setAttribute("totalDoctors", totalDoctors);
            request.setAttribute("totalAppointments", totalAppointments);
            request.setAttribute("pendingAppointments", pendingAppointments);
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("recentAppointments", recentAppointments);
            
            request.getRequestDispatcher("/pages/adminDashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("/pages/adminDashboard.jsp").forward(request, response);
        }
    }
    
    private void manageDoctors(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            request.setAttribute("doctors", doctors);
            request.getRequestDispatcher("/pages/manageDoctors.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading doctors: " + e.getMessage());
            request.getRequestDispatcher("/pages/manageDoctors.jsp").forward(request, response);
        }
    }
    
    private void addDoctor(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String specialization = request.getParameter("specialization");
        String availability = request.getParameter("availability");
        String contact = request.getParameter("contact");
        
        try {
            // First create user account for doctor
            User user = new User(name, email, password, "Doctor");
            boolean userCreated = userService.registerUser(user);
            
            if (userCreated) {
                Doctor doctor = new Doctor(user.getId(), name, specialization, availability, contact);
                boolean doctorAdded = doctorService.addDoctor(doctor);
                
                if (doctorAdded) {
                    request.setAttribute("success", "Doctor added successfully!");
                } else {
                    request.setAttribute("error", "Failed to add doctor details");
                }
            } else {
                request.setAttribute("error", "Failed to create doctor account");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error adding doctor: " + e.getMessage());
        }
        
        manageDoctors(request, response);
    }
    
    private void viewAppointments(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            request.setAttribute("appointments", appointments);
            request.getRequestDispatcher("/pages/viewAppointment.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading appointments: " + e.getMessage());
            request.getRequestDispatcher("/pages/viewAppointment.jsp").forward(request, response);
        }
    }
    
    private void updateAppointment(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String appointmentIdStr = request.getParameter("appointmentId");
        String status = request.getParameter("status");
        
        try {
            int appointmentId = Integer.parseInt(appointmentIdStr);
            boolean updated = appointmentService.updateAppointmentStatus(appointmentId, status);
            
            if (updated) {
                request.setAttribute("success", "Appointment status updated successfully!");
            } else {
                request.setAttribute("error", "Failed to update appointment status");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid appointment ID");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error updating appointment: " + e.getMessage());
        }
        
        viewAppointments(request, response);
    }
    
    private void manageUsers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<User> users = userService.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/pages/manageUsers.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading users: " + e.getMessage());
            request.getRequestDispatcher("/pages/manageUsers.jsp").forward(request, response);
        }
    }
    
    private void deleteUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");
        
        try {
            int userId = Integer.parseInt(userIdStr);
            boolean deleted = userService.deleteUser(userId);
            
            if (deleted) {
                request.setAttribute("success", "User deleted successfully!");
            } else {
                request.setAttribute("error", "Failed to delete user");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid user ID");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error deleting user: " + e.getMessage());
        }
        
        manageUsers(request, response);
    }
    
    private void deleteDoctor(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String doctorIdStr = request.getParameter("doctorId");
        
        try {
            int doctorId = Integer.parseInt(doctorIdStr);
            boolean deleted = doctorService.deleteDoctor(doctorId);
            
            if (deleted) {
                request.setAttribute("success", "Doctor deleted successfully!");
            } else {
                request.setAttribute("error", "Failed to delete doctor");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid doctor ID");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error deleting doctor: " + e.getMessage());
        }
        
        manageDoctors(request, response);
    }
}