package com.medicareplus.controller;

import com.medicareplus.model.Doctor;
import com.medicareplus.model.AuthResult;
import com.medicareplus.model.User;
import com.medicareplus.service.DoctorService;
import com.medicareplus.service.UserService;
import com.medicareplus.util.SecurityConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/login")
public class loginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserService userService;
	private DoctorService doctorService;

	public void init() {
		userService = new UserService();
		doctorService = new DoctorService();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		loadDoctors(request);
		request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = trim(request.getParameter("email"));
		String password = request.getParameter("password");
		String selectedRole = trim(request.getParameter("role"));

		if (email == null || email.isEmpty() || password == null || password.isEmpty() || selectedRole == null
				|| selectedRole.isEmpty()) {
			request.setAttribute("error", "Email, password, and role are required.");
			loadDoctors(request);
			request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
			return;
		}

		try {
			AuthResult authResult = userService.authenticate(email, password, selectedRole, request.getRemoteAddr());
			if (!authResult.isSuccess()) {
				request.setAttribute("error", authResult.getMessage());
				loadDoctors(request);
				request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
				return;
			}

			User user = authResult.getUser();
			String role = user.getRole() == null ? "" : user.getRole().toLowerCase();
			updateRememberMeCookie(request, response, email);

			HttpSession oldSession = request.getSession(false);
			if (oldSession != null)
				oldSession.invalidate();

			HttpSession session = request.getSession(true);
			session.setAttribute("user", user);
			session.setMaxInactiveInterval(SecurityConfig.SESSION_TIMEOUT_SECONDS);

			if ("admin".equals(role)) {
				response.sendRedirect(request.getContextPath() + "/admin/dashboard");
			} else if ("doctor".equals(role)) {
				response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
			} else {
				response.sendRedirect(request.getContextPath() + "/patient/dashboard");
			}
		} catch (Exception e) {
			request.setAttribute("error", "Login error: " + e.getMessage());
			loadDoctors(request);
			request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
		}
	}

	private void loadDoctors(HttpServletRequest request) {
		try {
			List<Doctor> doctors = doctorService.getAvailableDoctors();
			request.setAttribute("doctors", doctors);
		} catch (Exception e) {
			request.setAttribute("doctors", List.of());
		}
	}

	private void updateRememberMeCookie(HttpServletRequest request, HttpServletResponse response, String email) {
		boolean rememberMe = "true".equalsIgnoreCase(request.getParameter("rememberMe"));
		Cookie cookie = new Cookie("rememberedEmail", rememberMe ? email : "");
		cookie.setHttpOnly(true);
		cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
		cookie.setMaxAge(rememberMe ? 7 * 24 * 60 * 60 : 0);
		response.addCookie(cookie);
	}

	private String trim(String value) {
		return value == null ? null : value.trim();
	}
}
