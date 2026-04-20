package com.medicareplus.controller;

import java.io.IOException;
import java.util.List;

import com.medicareplus.model.Appointment;
import com.medicareplus.model.Doctor;
import com.medicareplus.model.MedicalRecord;
import com.medicareplus.model.User;
import com.medicareplus.service.AppointmentService;
import com.medicareplus.service.DoctorService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Doctor Controller - Handles all doctor operations
 */
@WebServlet("/doctor/*")
public class DoctorController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DoctorService doctorService;
    private AppointmentService appointmentService;
    
    @Override
    public void init() throws ServletException {
        doctorService = new DoctorService();
        appointmentService = new AppointmentService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        if (!"Doctor".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String path = request.getPathInfo();
        
        if (path == null || "/dashboard".equals(path)) {
            showDashboard(request, response, user);
        } else if ("/appointments".equals(path)) {
            viewAppointments(request, response, user);
        } else if ("/addMedicalRecord".equals(path)) {
            showAddMedicalRecordForm(request, response);
        } else {
            showDashboard(request, response, user);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getPathInfo();
        
        if ("/updateAppointment".equals(path)) {
            updateAppointmentStatus(request, response);
        } else if ("/addMedicalRecord".equals(path)) {
            addMedicalRecord(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
        }
    }
    
    private void showDashboard(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        try {
            Doctor doctor = doctorService.getDoctorByUserId(user.getId());
            if (doctor == null) {
                request.setAttribute("error", "Doctor profile not found");
                request.getRequestDispatcher("/pages/doctorDashboard.jsp").forward(request, response);
                return;
            }
            
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctor.getDoctorId());
            int totalAppointments = appointments.size();
            int pendingCount = (int) appointments.stream().filter(a -> "Pending".equals(a.getStatus())).count();
            int completedCount = (int) appointments.stream().filter(a -> "Completed".equals(a.getStatus())).count();
            
            // Get recent appointments (last 5)
            List<Appointment> recentAppointments = appointments;
            if (recentAppointments.size() > 5) {
                recentAppointments = recentAppointments.subList(0, 5);
            }
            
            request.setAttribute("doctor", doctor);
            request.setAttribute("totalAppointments", totalAppointments);
            request.setAttribute("pendingCount", pendingCount);
            request.setAttribute("completedCount", completedCount);
            request.setAttribute("recentAppointments", recentAppointments);
            
            request.getRequestDispatcher("/pages/doctorDashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("/pages/doctorDashboard.jsp").forward(request, response);
        }
    }
    
    private void viewAppointments(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        try {
            Doctor doctor = doctorService.getDoctorByUserId(user.getId());
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctor.getDoctorId());
            request.setAttribute("appointments", appointments);
            request.getRequestDispatcher("/pages/viewAppointment.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading appointments: " + e.getMessage());
            request.getRequestDispatcher("/pages/viewAppointment.jsp").forward(request, response);
        }
    }
    
    private void updateAppointmentStatus(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String appointmentIdStr = request.getParameter("appointmentId");
        String status = request.getParameter("status");
        
        try {
            int appointmentId = Integer.parseInt(appointmentIdStr);
            boolean updated = appointmentService.updateAppointmentStatus(appointmentId, status);
            
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (updated) {
                request.setAttribute("success", "Appointment status updated successfully!");
            } else {
                request.setAttribute("error", "Failed to update appointment status");
            }
            
            viewAppointments(request, response, user);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid appointment ID");
            viewAppointments(request, response, (User) request.getSession().getAttribute("user"));
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error updating appointment: " + e.getMessage());
            viewAppointments(request, response, (User) request.getSession().getAttribute("user"));
        }
    }
    
    private void showAddMedicalRecordForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String appointmentIdStr = request.getParameter("appointmentId");
        
        try {
            int appointmentId = Integer.parseInt(appointmentIdStr);
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            request.setAttribute("appointment", appointment);
            request.getRequestDispatcher("/pages/addMedicalRecord.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading appointment details");
            response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
        }
    }
    
    private void addMedicalRecord(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String appointmentIdStr = request.getParameter("appointmentId");
        String diagnosis = request.getParameter("diagnosis");
        String prescription = request.getParameter("prescription");
        
        try {
            int appointmentId = Integer.parseInt(appointmentIdStr);
            
            // Validation
            if (diagnosis == null || diagnosis.trim().isEmpty()) {
                request.setAttribute("error", "Please enter diagnosis");
                showAddMedicalRecordForm(request, response);
                return;
            }
            
            MedicalRecord record = new MedicalRecord(appointmentId, diagnosis, prescription);
            boolean added = appointmentService.addMedicalRecord(record);
            
            if (added) {
                // Update appointment status to Completed
                appointmentService.updateAppointmentStatus(appointmentId, "Completed");
                request.setAttribute("success", "Medical record added successfully!");
            } else {
                request.setAttribute("error", "Failed to add medical record");
            }
            
            response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid appointment ID");
            response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error adding medical record: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
        }
    }
}