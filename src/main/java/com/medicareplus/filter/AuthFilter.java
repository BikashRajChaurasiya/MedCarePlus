package com.medicareplus.filter;

import com.medicareplus.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = { "/admin/*", "/doctor/*", "/patient/*" })
public class AuthFilter implements Filter {
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpSession session = request.getSession(false);
		User user = session == null ? null : (User) session.getAttribute("user");

		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		String requiredRole = requiredRole(request.getRequestURI(), request.getContextPath());
		if (requiredRole != null && !requiredRole.equalsIgnoreCase(user.getRole())) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to access this page.");
			return;
		}

		chain.doFilter(request, response);
	}

	private String requiredRole(String uri, String contextPath) {
		String path = uri.substring(contextPath.length());
		if (path.startsWith("/admin/"))
			return "admin";
		if (path.startsWith("/doctor/"))
			return "doctor";
		if (path.startsWith("/patient/"))
			return "patient";
		return null;
	}
}
