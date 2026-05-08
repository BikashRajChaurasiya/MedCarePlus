package com.medicareplus.controller;

import com.medicareplus.model.Doctor;
import com.medicareplus.model.User;
import com.medicareplus.service.PatientService;
import com.medicareplus.service.AppointmentService;
import com.medicareplus.service.DoctorService;
import com.medicareplus.service.UserService;
import com.medicareplus.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private UserService userService;
	private DoctorService doctorService;
	private AppointmentService appointmentService;
	private PatientService patientService;

	@Override
	public void init() {
		userService = new UserService();
		doctorService = new DoctorService();
		appointmentService = new AppointmentService();
		patientService = new PatientService();
	}

	// =========================
	// GET REQUEST
	// =========================
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (requireAdmin(request, response) == null)
			return;

		String path = request.getPathInfo();

		if (path == null || "/".equals(path) || "/dashboard".equals(path)) {
			dashboard(request, response);

		} else if ("/doctors".equals(path)) {
			doctors(request, response);

		} else if ("/appointments".equals(path)) {
			appointments(request, response);

		} else if ("/medicalRecords".equals(path)) {
			medicalRecords(request, response);

		} else if ("/users".equals(path)) {
			users(request, response);

		} else if ("/about".equals(path)) {
			about(request, response);

		} else if ("/contact".equals(path)) {
			contact(request, response);

		} else {
			dashboard(request, response);
		}
	}

	// =========================
	// POST REQUEST
	// =========================
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (requireAdmin(request, response) == null)
			return;

		String path = request.getPathInfo();

		if ("/addDoctor".equals(path)) {
			addDoctor(request, response);

		} else if ("/deleteDoctor".equals(path)) {
			deleteDoctor(request, response);

		} else if ("/updateDoctor".equals(path)) {
			updateDoctor(request, response);

		} else if ("/updateAppointment".equals(path)) {
			updateAppointment(request, response);

		} else if ("/contact".equals(path)) {
			handleContact(request, response);

		} else {
			response.sendRedirect(request.getContextPath() + "/admin/dashboard");
		}
	}

	// =========================
	// DASHBOARD
	// =========================
	private void dashboard(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			request.setAttribute("totalUsers", userService.getUserCount());
			request.setAttribute("totalDoctors", doctorService.getDoctorCount());
			request.setAttribute("totalAppointments", appointmentService.getAppointmentCount());
			request.setAttribute("pendingAppointments", appointmentService.getPendingAppointmentCount());
			request.setAttribute("recentAppointments", appointmentService.getAllAppointments());
			request.setAttribute("patients", patientService.getAllPatients());
			request.setAttribute("medicalRecords", appointmentService.getAllMedicalRecords());

			request.getRequestDispatcher("/WEB-INF/pages/adminDashboard.jsp").forward(request, response);

		} catch (Exception e) {
			forwardError(request, response, "/WEB-INF/pages/adminDashboard.jsp", e);
		}
	}

	// =========================
	// DOCTORS
	// =========================
	private void doctors(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String search = request.getParameter("search");
			request.setAttribute("search", search);
			request.setAttribute("doctors", doctorService.searchDoctors(search));
			request.getRequestDispatcher("/WEB-INF/pages/manageDoctors.jsp").forward(request, response);

		} catch (Exception e) {
			forwardError(request, response, "/WEB-INF/pages/manageDoctors.jsp", e);
		}
	}

	private void deleteDoctor(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(request.getParameter("doctorId"));
			doctorService.deleteDoctor(id);
			request.setAttribute("success", "Doctor deleted successfully");

		} catch (Exception e) {
			request.setAttribute("error", e.getMessage());
		}

		doctors(request, response);
	}

	private void addDoctor(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			User user = new User(request.getParameter("name"), request.getParameter("email"),
					request.getParameter("password"), "doctor");

			if (!userService.registerUser(user)) {
				request.setAttribute("error", "Doctor email already exists");
				doctors(request, response);
				return;
			}

			Doctor doctor = new Doctor();
			doctor.setUserId(user.getUserId());
			fillDoctorFromRequest(doctor, request);

			doctorService.addDoctor(doctor);

			request.setAttribute("success", "Doctor added successfully");

		} catch (Exception e) {
			request.setAttribute("error", e.getMessage());
		}

		doctors(request, response);
	}

	private void updateDoctor(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			Doctor doctor = doctorService.getDoctorById(parseInt(request.getParameter("doctorId")));
			if (doctor == null) {
				request.setAttribute("error", "Doctor not found.");
				doctors(request, response);
				return;
			}

			fillDoctorFromRequest(doctor, request);
			doctorService.updateDoctor(doctor);
			request.setAttribute("success", "Doctor updated successfully.");

		} catch (Exception e) {
			request.setAttribute("error", e.getMessage());
		}

		doctors(request, response);
	}

	// =========================
	// APPOINTMENTS
	// =========================
	private void appointments(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String search = request.getParameter("search");
			request.setAttribute("search", search);
			request.setAttribute("appointments", appointmentService.searchAppointments(search));
			request.getRequestDispatcher("/WEB-INF/pages/viewAppointment.jsp").forward(request, response);

		} catch (Exception e) {
			forwardError(request, response, "/WEB-INF/pages/viewAppointment.jsp", e);
		}
	}

	private void medicalRecords(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String search = request.getParameter("search");
			request.setAttribute("search", search);
			request.setAttribute("records", appointmentService.searchMedicalRecords(search));
			request.getRequestDispatcher("/WEB-INF/pages/medicalRecords.jsp").forward(request, response);

		} catch (Exception e) {
			forwardError(request, response, "/WEB-INF/pages/medicalRecords.jsp", e);
		}
	}

	private void updateAppointment(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(request.getParameter("appointmentId"));
			String status = request.getParameter("status");

			appointmentService.updateAppointmentStatus(id, status);
			request.setAttribute("success", "Appointment updated successfully");

		} catch (Exception e) {
			request.setAttribute("error", e.getMessage());
		}

		appointments(request, response);
	}

	// =========================
	// USERS
	// =========================
	private void users(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			String search = request.getParameter("search");
			request.setAttribute("search", search);
			request.setAttribute("users", userService.getAllUsers());
			request.setAttribute("patients", patientService.searchPatients(search));
			request.getRequestDispatcher("/WEB-INF/pages/manageUsers.jsp").forward(request, response);

		} catch (Exception e) {
			forwardError(request, response, "/WEB-INF/pages/manageUsers.jsp", e);
		}
	}

	// =========================
	// ABOUT & CONTACT
	// =========================
	private void about(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.getRequestDispatcher("/WEB-INF/pages/about.jsp").forward(request, response);
	}

	private void contact(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.getRequestDispatcher("/WEB-INF/pages/contact.jsp").forward(request, response);
	}

	private void handleContact(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			String subject = request.getParameter("subject");
			String message = request.getParameter("message");

			// You can store in DB later
			System.out.println("==== CONTACT MESSAGE ====");
			System.out.println("Name: " + name);
			System.out.println("Email: " + email);
			System.out.println("Subject: " + subject);
			System.out.println("Message: " + message);

			request.setAttribute("success", "Message sent successfully!");

		} catch (Exception e) {
			request.setAttribute("error", "Failed to send message");
		}

		request.getRequestDispatcher("/WEB-INF/pages/contact.jsp").forward(request, response);
	}

	// =========================
	// UTIL METHODS
	// =========================
	private String normalizeAvailability(String value) {
		if (value == null || value.trim().isEmpty())
			return "available";
		String normalized = value.trim().toLowerCase();
		return ("busy".equals(normalized) || "off_duty".equals(normalized)) ? normalized : "available";
	}

	private void fillDoctorFromRequest(Doctor doctor, HttpServletRequest request) {
		String specialization = value(request, "specialization");
		if (!ValidationUtil.hasText(specialization)) {
			throw new IllegalArgumentException("Specialization is required.");
		}
		doctor.setSpecialization(specialization);
		doctor.setQualification(value(request, "qualification"));
		doctor.setExperienceYears(parseInt(value(request, "experienceYears", "experience_years")));
		doctor.setConsultationFee(parseDouble(value(request, "consultationFee", "consultation_fee")));
		doctor.setAvailabilityStatus(normalizeAvailability(value(request, "availability", "availability_status")));
		doctor.setContactPhone(value(request, "contact", "contactPhone", "contact_phone"));
	}

	private String value(HttpServletRequest request, String... names) {
		for (String name : names) {
			String value = request.getParameter(name);
			if (value != null && !value.trim().isEmpty())
				return value.trim();
		}
		return "";
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
		User u = (session == null) ? null : (User) session.getAttribute("user");

		if (u == null || !"admin".equalsIgnoreCase(u.getRole())) {
			response.sendRedirect(request.getContextPath() + "/login");
			return null;
		}

		return u;
	}

	private void forwardError(HttpServletRequest request, HttpServletResponse response, String jsp, Exception e)
			throws ServletException, IOException {

		request.setAttribute("error", e.getMessage());
		request.getRequestDispatcher(jsp).forward(request, response);
	}
}
