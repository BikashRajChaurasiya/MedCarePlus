package com.medicareplus.controller;

import java.io.IOException;
import java.util.List;

import com.medicareplus.model.Appointment;
import com.medicareplus.model.Doctor;
import com.medicareplus.model.MedicalRecord;
import com.medicareplus.model.Patient;
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
 * Patient Controller - Handles all patient operations
 */
@WebServlet("/patient/*")
public class PatientController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
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
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        if (!"Patient".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String path = request.getPathInfo();
        
        if (path == null || "/dashboard".equals(path)) {
            showDashboard(request, response, user);
        } else if ("/bookAppointment".equals(path)) {
            showBookAppointmentForm(request, response);
        } else if ("/myAppointments".equals(path)) {
            viewMyAppointments(request, response, user);
        } else if ("/medicalRecords".equals(path)) {
            viewMedicalRecords(request, response, user);
        } else if ("/searchDoctors".equals(path)) {
            searchDoctors(request, response);
        } else {
            showDashboard(request, response, user);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getPathInfo();
        
        if ("/bookAppointment".equals(path)) {
            bookAppointment(request, response);
        } else if ("/cancelAppointment".equals(path)) {
            cancelAppointment(request, response);
        } else if ("/searchDoctors".equals(path)) {
            searchDoctors(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/patient/dashboard");
        }
    }
    
    private void showDashboard(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        try {
            // Get patient details
            String sql = "SELECT * FROM patients WHERE user_id = ?";
            java.sql.Connection conn = com.medicareplus.config.DBConnection.getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user.getId());
            java.sql.ResultSet rs = pstmt.executeQuery();
            
            Patient patient = null;
            if (rs.next()) {
                patient = new Patient();
                patient.setPatientId(rs.getInt("patient_id"));
                patient.setUserId(rs.getInt("user_id"));
                patient.setAge(rs.getInt("age"));
                patient.setGender(rs.getString("gender"));
                patient.setContact(rs.getString("contact"));
                patient.setAddress(rs.getString("address"));
            }
            rs.close();
            pstmt.close();
            
            if (patient == null) {
                request.setAttribute("error", "Patient profile not found");
                request.getRequestDispatcher("/pages/patientDashboard.jsp").forward(request, response);
                return;
            }
            
            // Get appointments
            List<Appointment> appointments = appointmentService.getAppointmentsByPatientId(patient.getPatientId());
            int totalAppointments = appointments.size();
            int pendingCount = (int) appointments.stream().filter(a -> "Pending".equals(a.getStatus())).count();
            int approvedCount = (int) appointments.stream().filter(a -> "Approved".equals(a.getStatus())).count();
            int completedCount = (int) appointments.stream().filter(a -> "Completed".equals(a.getStatus())).count();
            
            // Get recent appointments
            List<Appointment> recentAppointments = appointments;
            if (recentAppointments.size() > 5) {
                recentAppointments = recentAppointments.subList(0, 5);
            }
            
            request.setAttribute("patient", patient);
            request.setAttribute("totalAppointments", totalAppointments);
            request.setAttribute("pendingCount", pendingCount);
            request.setAttribute("approvedCount", approvedCount);
            request.setAttribute("completedCount", completedCount);
            request.setAttribute("recentAppointments", recentAppointments);
            
            request.getRequestDispatcher("/pages/patientDashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("/pages/patientDashboard.jsp").forward(request, response);
        }
    }
    
    private void showBookAppointmentForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            request.setAttribute("doctors", doctors);
            request.getRequestDispatcher("/pages/bookAppointment.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading doctors: " + e.getMessage());
            request.getRequestDispatcher("/pages/bookAppointment.jsp").forward(request, response);
        }
    }
    
    private void bookAppointment(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String doctorIdStr = request.getParameter("doctorId");
        String date = request.getParameter("date");
        String time = request.getParameter("time");
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        try {
            int doctorId = Integer.parseInt(doctorIdStr);
            
            // Validate inputs
            if (date == null || date.trim().isEmpty()) {
                request.setAttribute("error", "Please select a date");
                showBookAppointmentForm(request, response);
                return;
            }
            
            if (time == null || time.trim().isEmpty()) {
                request.setAttribute("error", "Please select a time");
                showBookAppointmentForm(request, response);
                return;
            }
            
            // Get patient ID
            String sql = "SELECT patient_id FROM patients WHERE user_id = ?";
            java.sql.Connection conn = com.medicareplus.config.DBConnection.getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user.getId());
            java.sql.ResultSet rs = pstmt.executeQuery();
            
            int patientId = -1;
            if (rs.next()) {
                patientId = rs.getInt("patient_id");
            }
            rs.close();
            pstmt.close();
            
            // Check if slot is available
            if (!appointmentService.isSlotAvailable(doctorId, date, time)) {
                request.setAttribute("error", "This time slot is already booked. Please select another time.");
                showBookAppointmentForm(request, response);
                return;
            }
            
            Appointment appointment = new Appointment(patientId, doctorId, date, time, "Pending");
            boolean booked = appointmentService.bookAppointment(appointment);
            
            if (booked) {
                request.setAttribute("success", "Appointment booked successfully!");
            } else {
                request.setAttribute("error", "Failed to book appointment. Please try again.");
            }
            
            List<Doctor> doctors = doctorService.getAllDoctors();
            request.setAttribute("doctors", doctors);
            request.getRequestDispatcher("/pages/bookAppointment.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid doctor selection");
            showBookAppointmentForm(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error booking appointment: " + e.getMessage());
            showBookAppointmentForm(request, response);
        }
    }
    
    private void viewMyAppointments(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        try {
            // Get patient ID
            String sql = "SELECT patient_id FROM patients WHERE user_id = ?";
            java.sql.Connection conn = com.medicareplus.config.DBConnection.getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user.getId());
            java.sql.ResultSet rs = pstmt.executeQuery();
            
            int patientId = -1;
            if (rs.next()) {
                patientId = rs.getInt("patient_id");
            }
            rs.close();
            pstmt.close();
            
            List<Appointment> appointments = appointmentService.getAppointmentsByPatientId(patientId);
            request.setAttribute("appointments", appointments);
            request.getRequestDispatcher("/pages/viewAppointment.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading appointments: " + e.getMessage());
            request.getRequestDispatcher("/pages/viewAppointment.jsp").forward(request, response);
        }
    }
    
    private void cancelAppointment(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String appointmentIdStr = request.getParameter("appointmentId");
        
        try {
            int appointmentId = Integer.parseInt(appointmentIdStr);
            boolean cancelled = appointmentService.updateAppointmentStatus(appointmentId, "Rejected");
            
            if (cancelled) {
                request.setAttribute("success", "Appointment cancelled successfully!");
            } else {
                request.setAttribute("error", "Failed to cancel appointment");
            }
            
            viewMyAppointments(request, response, (User) request.getSession().getAttribute("user"));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid appointment ID");
            viewMyAppointments(request, response, (User) request.getSession().getAttribute("user"));
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error cancelling appointment: " + e.getMessage());
            viewMyAppointments(request, response, (User) request.getSession().getAttribute("user"));
        }
    }
    
    private void viewMedicalRecords(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        try {
            // Get patient ID
            String sql = "SELECT patient_id FROM patients WHERE user_id = ?";
            java.sql.Connection conn = com.medicareplus.config.DBConnection.getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user.getId());
            java.sql.ResultSet rs = pstmt.executeQuery();
            
            int patientId = -1;
            if (rs.next()) {
                patientId = rs.getInt("patient_id");
            }
            rs.close();
            pstmt.close();
            
            List<MedicalRecord> records = appointmentService.getMedicalRecordsByPatientId(patientId);
            request.setAttribute("records", records);
            request.getRequestDispatcher("/pages/medicalRecords.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading medical records: " + e.getMessage());
            request.getRequestDispatcher("/pages/medicalRecords.jsp").forward(request, response);
        }
    }
    
    private void searchDoctors(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        
        try {
            List<Doctor> doctors;
            if (keyword != null && !keyword.trim().isEmpty()) {
                doctors = doctorService.getDoctorsBySpecialization(keyword);
            } else {
                doctors = doctorService.getAllDoctors();
            }
            request.setAttribute("doctors", doctors);
            request.setAttribute("searchKeyword", keyword);
            request.getRequestDispatcher("/pages/bookAppointment.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error searching doctors: " + e.getMessage());
            request.getRequestDispatcher("/pages/bookAppointment.jsp").forward(request, response);
        }
    }
}