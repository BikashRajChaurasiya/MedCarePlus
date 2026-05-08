package com.medicareplus.controller;

import com.medicareplus.service.EmailService;
import com.medicareplus.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/forgetPassword")
public class ForgotPasswordController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserService userService;
	private EmailService emailService;

	@Override
	public void init() {
		userService = new UserService();
		emailService = new EmailService();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/pages/forgetPassword.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = trim(request.getParameter("email"));

		try {
			String token = userService.createPasswordResetToken(email);
			if (token != null) {
				String resetLink = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
						+ request.getContextPath() + "/resetPassword?token=" + token;
				emailService.sendPasswordReset(email, resetLink);
				request.setAttribute("resetLink", resetLink);
			}
			request.setAttribute("success", "If the email exists, a password reset link has been sent.");
		} catch (Exception e) {
			request.setAttribute("error", "Unable to process password reset request.");
		}

		request.getRequestDispatcher("/WEB-INF/pages/forgetPassword.jsp").forward(request, response);
	}

	private String trim(String value) {
		return value == null ? "" : value.trim();
	}
}
