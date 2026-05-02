package com.medicareplus.controller;

import com.medicareplus.model.Appointment;
import com.medicareplus.model.Patient;
import com.medicareplus.model.User;
import com.medicareplus.service.AppointmentService;
import com.medicareplus.service.DoctorService;
import com.medicareplus.service.PatientService;
import com.medicareplus.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/patient/*")
public class PatientController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PatientService patientService;
    private DoctorService doctorService;
    private AppointmentService appointmentService;

    public void init() {
        patientService = new PatientService();
        doctorService = new DoctorService();
        appointmentService = new AppointmentService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = requireRole(request, response, "patient");
        if (user == null) return;
        String path = request.getPathInfo();
        if (path == null || "/dashboard".equals(path) || "/".equals(path))
            showDashboard(request, response, user);
        else if ("/bookAppointment".equals(path))
            showBookAppointment(request, response);
        else if ("/myAppointments".equals(path))
            showAppointments(request, response, user);
        else if ("/medicalRecords".equals(path))
            showMedicalRecords(request, response, user);
        else if ("/searchDoctors".equals(path))
            searchDoctors(request, response);
        else
            response.sendRedirect(request.getContextPath() + "/patient/dashboard");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = requireRole(request, response, "patient");
        if (user == null) return;
        String path = request.getPathInfo();
        if ("/bookAppointment".equals(path))
            bookAppointment(request, response, user);
        else if ("/cancelAppointment".equals(path)) {
            updateStatus(request, "cancelled");
            showAppointments(request, response, user);
        } else if ("/searchDoctors".equals(path))
            searchDoctors(request, response);
        else
            response.sendRedirect(request.getContextPath() + "/patient/dashboard");
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
        try {
            Patient p = patientService.getPatientByUserId(user.getUserId());
            List<Appointment> a = p == null ? List.of() : appointmentService.getAppointmentsByPatientId(p.getPatientId());
            request.setAttribute("patient", p);
            request.setAttribute("totalAppointments", a.size());
            request.setAttribute("pendingCount", a.stream().filter(x -> "pending".equalsIgnoreCase(x.getStatus())).count());
            request.setAttribute("approvedCount", a.stream().filter(x -> "approved".equalsIgnoreCase(x.getStatus())).count());
            request.setAttribute("completedCount", a.stream().filter(x -> "completed".equalsIgnoreCase(x.getStatus())).count());
            request.setAttribute("recentAppointments", a.size() > 5 ? a.subList(0, 5) : a);
            request.getRequestDispatcher("/WEB-INF/pages/patientDashboard.jsp").forward(request, response);
        } catch (Exception e) {
            forwardError(request, response, "/WEB-INF/pages/patientDashboard.jsp", e);
        }
    }

    private void showBookAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("doctors", doctorService.getAvailableDoctors());
            request.getRequestDispatcher("/WEB-INF/pages/bookAppointment.jsp").forward(request, response);
        } catch (Exception e) {
            forwardError(request, response, "/WEB-INF/pages/bookAppointment.jsp", e);
        }
    }

    private void showAppointments(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
        try {
            Patient p = patientService.getPatientByUserId(user.getUserId());
            request.setAttribute("appointments", p == null ? List.of() : appointmentService.getAppointmentsByPatientId(p.getPatientId()));
            request.getRequestDispatcher("/WEB-INF/pages/viewAppointment.jsp").forward(request, response);
        } catch (Exception e) {
            forwardError(request, response, "/WEB-INF/pages/viewAppointment.jsp", e);
        }
    }

    private void showMedicalRecords(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
        try {
            Patient p = patientService.getPatientByUserId(user.getUserId());
            request.setAttribute("records", p == null ? List.of() : appointmentService.getMedicalRecordsByPatientId(p.getPatientId()));
            request.getRequestDispatcher("/WEB-INF/pages/medicalRecords.jsp").forward(request, response);
        } catch (Exception e) {
            forwardError(request, response, "/WEB-INF/pages/medicalRecords.jsp", e);
        }
    }

    private void searchDoctors(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String keyword = request.getParameter("keyword");
            request.setAttribute("searchKeyword", keyword);
            request.setAttribute("doctors", ValidationUtil.hasText(keyword) ? doctorService.getDoctorsBySpecialization(keyword) : doctorService.getAvailableDoctors());
            request.getRequestDispatcher("/WEB-INF/pages/bookAppointment.jsp").forward(request, response);
        } catch (Exception e) {
            forwardError(request, response, "/WEB-INF/pages/bookAppointment.jsp", e);
        }
    }

    private void bookAppointment(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
        try {
            Patient p = patientService.getPatientByUserId(user.getUserId());
            String date = request.getParameter("date");
            String time = request.getParameter("time");
            if (p == null || !ValidationUtil.isTodayOrFuture(date) || !ValidationUtil.hasText(time)) {
                request.setAttribute("error", "Please select a valid date and time");
                showBookAppointment(request, response);
                return;
            }
            Appointment a = new Appointment(p.getPatientId(), Integer.parseInt(request.getParameter("doctorId")), date, time, "pending", request.getParameter("symptoms"));
            if (appointmentService.bookAppointment(a))
                request.setAttribute("success", "Appointment booked successfully!");
            else
                request.setAttribute("error", "This time slot is already booked.");
            showBookAppointment(request, response);
        } catch (Exception e) {
            forwardError(request, response, "/WEB-INF/pages/bookAppointment.jsp", e);
        }
    }

    private void updateStatus(HttpServletRequest request, String status) {
        try {
            appointmentService.updateAppointmentStatus(Integer.parseInt(request.getParameter("appointmentId")), status);
            request.setAttribute("success", "Appointment updated successfully");
        } catch (Exception e) {
            request.setAttribute("error", "Unable to update appointment: " + e.getMessage());
        }
    }

    private User requireRole(HttpServletRequest request, HttpServletResponse response, String role) throws IOException {
        HttpSession session = request.getSession(false);
        User u = session == null ? null : (User) session.getAttribute("user");
        if (u == null || !role.equalsIgnoreCase(u.getRole())) {
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