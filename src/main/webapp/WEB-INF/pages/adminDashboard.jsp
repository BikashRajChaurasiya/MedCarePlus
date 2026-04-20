<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.User, com.medicareplus.model.Appointment, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"Admin".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare+ - Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <div class="nav-logo">
                <h2>🏥 MediCare+ Admin</h2>
            </div>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="active">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/doctors">Manage Doctors</a>
                <a href="${pageContext.request.contextPath}/admin/appointments">Appointments</a>
                <a href="${pageContext.request.contextPath}/admin/users">Manage Users</a>
                <a href="${pageContext.request.contextPath}/pages/about.jsp">About</a>
                <a href="${pageContext.request.contextPath}/pages/contact.jsp">Contact</a>
                <a href="${pageContext.request.contextPath}/logout">Logout</a>
            </div>
            <div class="user-info">
                Welcome, <%= user.getName() %>
            </div>
        </div>
    </nav>
    
    <div class="dashboard-container">
        <div class="dashboard-header">
            <h1>Admin Dashboard</h1>
            <p>Overview of MediCare+ System</p>
        </div>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success"><%= request.getAttribute("success") %></div>
        <% } %>
        
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">👨‍⚕️</div>
                <div class="stat-number"><%= request.getAttribute("totalDoctors") %></div>
                <div class="stat-label">Total Doctors</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">📅</div>
                <div class="stat-number"><%= request.getAttribute("totalAppointments") %></div>
                <div class="stat-label">Total Appointments</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">⏳</div>
                <div class="stat-number"><%= request.getAttribute("pendingAppointments") %></div>
                <div class="stat-label">Pending Appointments</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">👥</div>
                <div class="stat-number"><%= request.getAttribute("totalUsers") %></div>
                <div class="stat-label">Total Users</div>
            </div>
        </div>
        
        <div class="recent-section">
            <h3>Recent Appointments</h3>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Patient</th>
                        <th>Doctor</th>
                        <th>Date</th>
                        <th>Time</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<Appointment> appointments = (List<Appointment>) request.getAttribute("recentAppointments");
                        if (appointments != null && !appointments.isEmpty()) {
                            for (Appointment apt : appointments) {
                    %>
                    <tr>
                        <td><%= apt.getAppointmentId() %></td>
                        <td><%= apt.getPatientName() != null ? apt.getPatientName() : "N/A" %></td>
                        <td><%= apt.getDoctorName() != null ? apt.getDoctorName() : "N/A" %></td>
                        <td><%= apt.getDate() %></td>
                        <td><%= apt.getTime() %></td>
                        <td><span class="status-<%= apt.getStatus().toLowerCase() %>"><%= apt.getStatus() %></span></td>
                        <td>
                            <form action="${pageContext.request.contextPath}/admin/updateAppointment" method="post" style="display:inline">
                                <input type="hidden" name="appointmentId" value="<%= apt.getAppointmentId() %>">
                                <select name="status" onchange="this.form.submit()">
                                    <option value="Pending" <%= "Pending".equals(apt.getStatus()) ? "selected" : "" %>>Pending</option>
                                    <option value="Approved" <%= "Approved".equals(apt.getStatus()) ? "selected" : "" %>>Approved</option>
                                    <option value="Rejected" <%= "Rejected".equals(apt.getStatus()) ? "selected" : "" %>>Rejected</option>
                                    <option value="Completed" <%= "Completed".equals(apt.getStatus()) ? "selected" : "" %>>Completed</option>
                                </select>
                            </form>
                        </td>
                    </tr>
                    <%      }
                        } else { %>
                    <tr><td colspan="7" style="text-align:center">No appointments found</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>