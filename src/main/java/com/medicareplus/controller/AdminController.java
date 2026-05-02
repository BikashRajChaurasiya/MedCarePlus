package com.medicareplus.controller;

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
import java.io.IOException;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;
    private DoctorService doctorService;
    private AppointmentService appointmentService;

    public void init() {
        userService = new UserService();
        doctorService = new DoctorService();
        appointmentService = new AppointmentService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (requireAdmin(request, response) == null) return;
        String path = request.getPathInfo();
        if (path == null || "/".equals(path) || "/dashboard".equals(path))
            dashboard(request, response);
        else if ("/doctors".equals(path))
            doctors(request, response);
        else if ("/appointments".equals(path))
            appointments(request, response);
        else
            dashboard(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (requireAdmin(request, response) == null) return;
        String path = request.getPathInfo();
        if ("/addDoctor".equals(path))
            addDoctor(request, response);
        else if ("/deleteDoctor".equals(path)) {
            try {
                doctorService.deleteDoctor(Integer.parseInt(request.getParameter("doctorId")));
                request.setAttribute("success", "Doctor deleted successfully");
            } catch (Exception e) {
                request.setAttribute("error", e.getMessage());
            }
            doctors(request, response);
        } else if ("/updateAppointment".equals(path)) {
            try {
                appointmentService.updateAppointmentStatus(Integer.parseInt(request.getParameter("appointmentId")), request.getParameter("status"));
                request.setAttribute("success", "Appointment updated successfully");
            } catch (Exception e) {
                request.setAttribute("error", e.getMessage());
            }
            appointments(request, response);
        } else
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }

    private void dashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("totalUsers", userService.getUserCount());
            request.setAttribute("totalDoctors", doctorService.getDoctorCount());
            request.setAttribute("totalAppointments", appointmentService.getAppointmentCount());
            request.setAttribute("pendingAppointments", appointmentService.getPendingAppointmentCount());
            request.setAttribute("recentAppointments", appointmentService.getAllAppointments());
            request.getRequestDispatcher("/WEB-INF/pages/adminDashboard.jsp").forward(request, response);
        } catch (Exception e) {
            forwardError(request, response, "/WEB-INF/pages/adminDashboard.jsp", e);
        }
    }

    private void doctors(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("doctors", doctorService.getAllDoctors());
            request.getRequestDispatcher("/WEB-INF/pages/manageDoctors.jsp").forward(request, response);
        } catch (Exception e) {
            forwardError(request, response, "/WEB-INF/pages/manageDoctors.jsp", e);
        }
    }

    private void appointments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("appointments", appointmentService.getAllAppointments());
            request.getRequestDispatcher("/WEB-INF/pages/viewAppointment.jsp").forward(request, response);
        } catch (Exception e) {
            forwardError(request, response, "/WEB-INF/pages/viewAppointment.jsp", e);
        }
    }

    private void addDoctor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            User user = new User(request.getParameter("name"), request.getParameter("email"), request.getParameter("password"), "doctor");
            if (!userService.registerUser(user)) {
                request.setAttribute("error", "Doctor email already exists");
                doctors(request, response);
                return;
            }
            Doctor doctor = new Doctor();
            doctor.setUserId(user.getUserId());
            doctor.setSpecialization(request.getParameter("specialization"));
            doctor.setQualification(request.getParameter("qualification"));
            doctor.setExperienceYears(parseInt(request.getParameter("experienceYears")));
            doctor.setConsultationFee(parseDouble(request.getParameter("consultationFee")));
            doctor.setAvailabilityStatus(normalizeAvailability(request.getParameter("availability")));
            doctor.setContactPhone(request.getParameter("contact"));
            doctorService.addDoctor(doctor);
            request.setAttribute("success", "Doctor added successfully");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
        doctors(request, response);
    }

    private String normalizeAvailability(String value) {
        return value == null || value.trim().isEmpty() ? "available" : value.trim();
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private User requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User u = session == null ? null : (User) session.getAttribute("user");
        if (u == null || !"admin".equalsIgnoreCase(u.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        return u;
    }

    private void forwardError(HttpServletRequest request, HttpServletResponse response, String jsp, Exception e) throws ServletException, IOException {
        request.setAttribute("error", e.getMessage());
        request.getRequestDispatcher(jsp).forward(request, response);
    }
}