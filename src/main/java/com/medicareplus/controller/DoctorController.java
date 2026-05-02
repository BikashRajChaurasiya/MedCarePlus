package com.medicareplus.controller;

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
import java.io.IOException;
import java.util.List;

@WebServlet("/doctor/*")
public class DoctorController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DoctorService doctorService;
    private AppointmentService appointmentService;

    public void init() {
        doctorService = new DoctorService();
        appointmentService = new AppointmentService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = requireDoctor(request, response);
        if (user == null) return;
        String path = request.getPathInfo();
        if (path == null || "/".equals(path) || "/dashboard".equals(path))
            dashboard(request, response, user);
        else if ("/appointments".equals(path))
            appointments(request, response, user);
        else
            dashboard(request, response, user);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = requireDoctor(request, response);
        if (user == null) return;
        String path = request.getPathInfo();
        if ("/updateAppointment".equals(path)) {
            updateAppointment(request);
            appointments(request, response, user);
        } else if ("/addMedicalRecord".equals(path)) {
            addMedicalRecord(request);
            appointments(request, response, user);
        } else
            response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
    }

    private void dashboard(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
        try {
            Doctor doctor = doctorService.getDoctorByUserId(user.getUserId());
            java.util.List<com.medicareplus.model.Appointment> appointments = doctor == null ? java.util.Collections.emptyList() : appointmentService.getAppointmentsByDoctorId(doctor.getDoctorId());
            request.setAttribute("doctor", doctor);
            request.setAttribute("totalAppointments", appointments.size());
            request.setAttribute("pendingCount", appointments.stream().filter(a -> "pending".equalsIgnoreCase(a.getStatus())).count());
            request.setAttribute("completedCount", appointments.stream().filter(a -> "completed".equalsIgnoreCase(a.getStatus())).count());
            request.setAttribute("recentAppointments", appointments.size() > 5 ? appointments.subList(0, 5) : appointments);
            request.getRequestDispatcher("/WEB-INF/pages/doctorDashboard.jsp").forward(request, response);
        } catch (Exception e) {
            forwardError(request, response, "/WEB-INF/pages/doctorDashboard.jsp", e);
        }
    }

    private void appointments(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
        try {
            Doctor doctor = doctorService.getDoctorByUserId(user.getUserId());
            request.setAttribute("appointments", doctor == null ? List.of() : appointmentService.getAppointmentsByDoctorId(doctor.getDoctorId()));
            request.getRequestDispatcher("/WEB-INF/pages/viewAppointment.jsp").forward(request, response);
        } catch (Exception e) {
            forwardError(request, response, "/WEB-INF/pages/viewAppointment.jsp", e);
        }
    }

    private void updateAppointment(HttpServletRequest request) {
        try {
            appointmentService.updateAppointmentStatus(Integer.parseInt(request.getParameter("appointmentId")), request.getParameter("status"));
            request.setAttribute("success", "Appointment status updated successfully");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
    }

    private void addMedicalRecord(HttpServletRequest request) {
        try {
            MedicalRecord record = new MedicalRecord();
            record.setAppointmentId(Integer.parseInt(request.getParameter("appointmentId")));
            record.setDiagnosis(request.getParameter("diagnosis"));
            record.setPrescription(request.getParameter("prescription"));
            record.setNotes(request.getParameter("notes"));
            appointmentService.addMedicalRecord(record);
            appointmentService.updateAppointmentStatus(record.getAppointmentId(), "completed");
            request.setAttribute("success", "Medical record added successfully");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
    }

    private User requireDoctor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User u = session == null ? null : (User) session.getAttribute("user");
        if (u == null || !"doctor".equalsIgnoreCase(u.getRole())) {
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