package com.medicareplus.controller;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.medicareplus.model.Appointment;
import com.medicareplus.model.Doctor;
import com.medicareplus.model.MedicalRecord;
import com.medicareplus.model.Patient;
import com.medicareplus.model.User;
import com.medicareplus.service.AppointmentService;
import com.medicareplus.service.DoctorService;
import com.medicareplus.service.UserService;
import com.medicareplus.config.DBConnection;

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
        if (!"patient".equalsIgnoreCase(user.getRole())) {
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
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();
            
            Patient patient = null;
            if (rs.next()) {
                patient = new Patient();
                patient.setPatientId(rs.getInt("patient_id"));
                patient.setUserId(rs.getInt("user_id"));
                patient.setAge(calculateAge(rs.getString("date_of_birth")));
                patient.setGender(rs.getString("gender"));
                patient.setContact(rs.getString("contact_phone"));
                patient.setAddress(rs.getString("address"));
                patient.setDateOfBirth(rs.getString("date_of_birth"));
                patient.setBloodGroup(rs.getString("blood_group"));
                patient.setEmergencyContact(rs.getString("emergency_contact"));
            }
            rs.close();
            pstmt.close();
            conn.close();
            
            if (patient == null) {
                request.setAttribute("error", "Patient profile not found");
                request.getRequestDispatcher("/pages/patientDashboard.jsp").forward(request, response);
                return;
            }
            
            // Get appointments with doctor names
            List<Appointment> appointments = getAppointmentsWithDoctorNames(patient.getPatientId());
            int totalAppointments = appointments.size();
            int pendingCount = (int) appointments.stream().filter(a -> "pending".equalsIgnoreCase(a.getStatus())).count();
            int approvedCount = (int) appointments.stream().filter(a -> "approved".equalsIgnoreCase(a.getStatus())).count();
            int completedCount = (int) appointments.stream().filter(a -> "completed".equalsIgnoreCase(a.getStatus())).count();
            
            // Get recent appointments (last 5)
            List<Appointment> recentAppointments = new ArrayList<>();
            if (appointments.size() > 0) {
                int endIndex = Math.min(5, appointments.size());
                recentAppointments = appointments.subList(0, endIndex);
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
    
    private int calculateAge(String dateOfBirth) {
        if (dateOfBirth == null) return 0;
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date birthDate = sdf.parse(dateOfBirth);
            java.util.Date currentDate = new java.util.Date();
            long ageInMillis = currentDate.getTime() - birthDate.getTime();
            return (int) (ageInMillis / (1000L * 60 * 60 * 24 * 365));
        } catch (Exception e) {
            return 0;
        }
    }
    
    private List<Appointment> getAppointmentsWithDoctorNames(int patientId) throws Exception {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name as doctor_name, d.specialization " +
                     "FROM appointments a " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "WHERE a.patient_id = ? " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        
        Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, patientId);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Appointment apt = new Appointment();
            apt.setAppointmentId(rs.getInt("appointment_id"));
            apt.setPatientId(rs.getInt("patient_id"));
            apt.setDoctorId(rs.getInt("doctor_id"));
            apt.setAppointmentDate(rs.getString("appointment_date"));
            apt.setAppointmentTime(rs.getString("appointment_time"));
            apt.setStatus(rs.getString("status"));
            apt.setSymptoms(rs.getString("symptoms"));
            apt.setDoctorName(rs.getString("doctor_name"));
            apt.setDoctorSpecialization(rs.getString("specialization"));
            appointments.add(apt);
        }
        rs.close();
        pstmt.close();
        conn.close();
        
        return appointments;
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
        String symptoms = request.getParameter("symptoms");
        
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
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();
            
            int patientId = -1;
            if (rs.next()) {
                patientId = rs.getInt("patient_id");
            }
            rs.close();
            pstmt.close();
            conn.close();
            
            if (patientId == -1) {
                request.setAttribute("error", "Patient profile not found");
                showBookAppointmentForm(request, response);
                return;
            }
            
            // Check if slot is available
            if (!isSlotAvailable(doctorId, date, time)) {
                request.setAttribute("error", "This time slot is already booked. Please select another time.");
                showBookAppointmentForm(request, response);
                return;
            }
            
            // Book appointment
            boolean booked = bookAppointmentInDB(patientId, doctorId, date, time, symptoms);
            
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
    
    private boolean isSlotAvailable(int doctorId, String date, String time) throws Exception {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, doctorId);
        pstmt.setString(2, date);
        pstmt.setString(3, time);
        ResultSet rs = pstmt.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        rs.close();
        pstmt.close();
        conn.close();
        return count == 0;
    }
    
    private boolean bookAppointmentInDB(int patientId, int doctorId, String date, String time, String symptoms) throws Exception {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, status, symptoms, created_at) VALUES (?, ?, ?, ?, 'pending', ?, NOW())";
        Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, patientId);
        pstmt.setInt(2, doctorId);
        pstmt.setString(3, date);
        pstmt.setString(4, time);
        pstmt.setString(5, symptoms);
        int rows = pstmt.executeUpdate();
        pstmt.close();
        conn.close();
        return rows > 0;
    }
    
    private void viewMyAppointments(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        try {
            // Get patient ID
            String sql = "SELECT patient_id FROM patients WHERE user_id = ?";
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();
            
            int patientId = -1;
            if (rs.next()) {
                patientId = rs.getInt("patient_id");
            }
            rs.close();
            pstmt.close();
            conn.close();
            
            List<Appointment> appointments = getAppointmentsWithDoctorNames(patientId);
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
            boolean cancelled = cancelAppointmentInDB(appointmentId);
            
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
    
    private boolean cancelAppointmentInDB(int appointmentId) throws Exception {
        String sql = "UPDATE appointments SET status = 'cancelled' WHERE appointment_id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, appointmentId);
        int rows = pstmt.executeUpdate();
        pstmt.close();
        conn.close();
        return rows > 0;
    }
    
    private void viewMedicalRecords(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        try {
            // Get patient ID
            String sql = "SELECT patient_id FROM patients WHERE user_id = ?";
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();
            
            int patientId = -1;
            if (rs.next()) {
                patientId = rs.getInt("patient_id");
            }
            rs.close();
            pstmt.close();
            
            // Get medical records
            String recordsSql = "SELECT mr.*, u.full_name as doctor_name " +
                               "FROM medical_records mr " +
                               "JOIN appointments a ON mr.appointment_id = a.appointment_id " +
                               "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                               "JOIN users u ON d.user_id = u.user_id " +
                               "WHERE a.patient_id = ? " +
                               "ORDER BY mr.created_at DESC";
            
            PreparedStatement pstmt2 = conn.prepareStatement(recordsSql);
            pstmt2.setInt(1, patientId);
            ResultSet rs2 = pstmt2.executeQuery();
            
            List<MedicalRecord> records = new ArrayList<>();
            while (rs2.next()) {
                MedicalRecord record = new MedicalRecord();
                record.setRecordId(rs2.getInt("record_id"));
                record.setAppointmentId(rs2.getInt("appointment_id"));
                record.setDiagnosis(rs2.getString("diagnosis"));
                record.setPrescription(rs2.getString("prescription"));
                record.setDoctorName(rs2.getString("doctor_name"));
                record.setCreatedAt(rs2.getTimestamp("created_at"));
                records.add(record);
            }
            rs2.close();
            pstmt2.close();
            conn.close();
            
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
                doctors = getDoctorsBySpecialization(keyword);
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
    
    private List<Doctor> getDoctorsBySpecialization(String specialization) throws Exception {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT d.*, u.full_name as name FROM doctors d " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "WHERE d.specialization LIKE ? AND d.availability_status = 'available'";
        
        Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, "%" + specialization + "%");
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Doctor doctor = new Doctor();
            doctor.setDoctorId(rs.getInt("doctor_id"));
            doctor.setUserId(rs.getInt("user_id"));
            doctor.setName(rs.getString("name"));
            doctor.setSpecialization(rs.getString("specialization"));
            doctor.setQualification(rs.getString("qualification"));
            doctor.setExperienceYears(rs.getInt("experience_years"));
            doctor.setConsultationFee(rs.getDouble("consultation_fee"));
            doctor.setAvailability(rs.getString("availability_status"));
            doctor.setContact(rs.getString("contact_phone"));
            doctors.add(doctor);
        }
        rs.close();
        pstmt.close();
        conn.close();
        
        return doctors;
    }
}