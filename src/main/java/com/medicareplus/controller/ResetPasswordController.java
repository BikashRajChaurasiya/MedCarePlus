package com.medicareplus.controller;

import com.medicareplus.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/resetPassword")
public class ResetPasswordController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserService userService;

	@Override
	public void init() {
		userService = new UserService();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("token", trim(request.getParameter("token")));
		request.getRequestDispatcher("/WEB-INF/pages/resetPassword.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String token = trim(request.getParameter("token"));
		String newPassword = request.getParameter("newPassword");
		String confirmPassword = request.getParameter("confirmPassword");

		try {
			if (newPassword == null || !newPassword.equals(confirmPassword)) {
				request.setAttribute("error", "Password and confirm password do not match.");
				request.setAttribute("token", token);
				request.getRequestDispatcher("/WEB-INF/pages/resetPassword.jsp").forward(request, response);
				return;
			}

			if (!userService.resetPassword(token, newPassword)) {
				request.setAttribute("error", "Reset link is invalid, expired, or the new password was used before.");
				request.setAttribute("token", token);
				request.getRequestDispatcher("/WEB-INF/pages/resetPassword.jsp").forward(request, response);
				return;
			}

			request.setAttribute("success", "Password reset successful. Please log in with your new password.");
			request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
		} catch (Exception e) {
			request.setAttribute("error", "Unable to reset password.");
			request.setAttribute("token", token);
			request.getRequestDispatcher("/WEB-INF/pages/resetPassword.jsp").forward(request, response);
		}
	}

	private String trim(String value) {
		return value == null ? "" : value.trim();
	}
}
