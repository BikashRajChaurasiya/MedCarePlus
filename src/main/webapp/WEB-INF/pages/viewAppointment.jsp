<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.User, com.medicareplus.model.Appointment, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare+ - Appointments</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <div class="nav-logo">
                <h2>🏥 MediCare+ <%= user.getRole() %></h2>
            </div>
            <div class="nav-links">
                <% if ("Patient".equalsIgnoreCase(user.getRole())) { %>
                    <a href="${pageContext.request.contextPath}/patient/dashboard">Dashboard</a>
                    <a href="${pageContext.request.contextPath}/patient/bookAppointment">Book Appointment</a>
                    <a href="${pageContext.request.contextPath}/patient/myAppointments" class="active">My Appointments</a>
                    <a href="${pageContext.request.contextPath}/patient/medicalRecords">Medical Records</a>
                <% } else if ("Doctor".equalsIgnoreCase(user.getRole())) { %>
                    <a href="${pageContext.request.contextPath}/doctor/dashboard">Dashboard</a>
                    <a href="${pageContext.request.contextPath}/doctor/appointments" class="active">My Appointments</a>
                <% } else if ("Admin".equalsIgnoreCase(user.getRole())) { %>
                    <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
                    <a href="${pageContext.request.contextPath}/admin/doctors">Manage Doctors</a>
                    <a href="${pageContext.request.contextPath}/admin/appointments" class="active">Appointments</a>
                    <a href="${pageContext.request.contextPath}/admin/users">Manage Users</a>
                <% } %>
                <a href="${pageContext.request.contextPath}/logout">Logout</a>
            </div>
            <div class="user-info">
                Welcome, <%= user.getName() %>
            </div>
        </div>
    </nav>
    
    <div class="dashboard-container">
        <div class="dashboard-header">
            <h1>Appointments</h1>
            <p>View and manage all appointments</p>
        </div>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success"><%= request.getAttribute("success") %></div>
        <% } %>
        
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <% if ("Patient".equalsIgnoreCase(user.getRole())) { %>
                        <th>Doctor Name</th>
                    <% } else { %>
                        <th>Patient Name</th>
                    <% } %>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Status</th>
                    <% if ("Patient".equalsIgnoreCase(user.getRole())) { %>
                        <th>Action</th>
                    <% } %>
                </tr>
            </thead>
            <tbody>
                <%
                    List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
                    if (appointments != null && !appointments.isEmpty()) {
                        for (Appointment apt : appointments) {
                %>
                <tr>
                    <td><%= apt.getAppointmentId() %></td>
                    <% if ("Patient".equalsIgnoreCase(user.getRole())) { %>
                        <td><%= apt.getDoctorName() != null ? apt.getDoctorName() : "N/A" %></td>
                    <% } else { %>
                        <td><%= apt.getPatientName() != null ? apt.getPatientName() : "N/A" %></td>
                    <% } %>
                    <td><%= apt.getDate() %></td>
                    <td><%= apt.getTime() %></td>
                    <td><span class="status-<%= apt.getStatus().toLowerCase() %>"><%= apt.getStatus() %></span></td>
                    <% if ("Patient".equalsIgnoreCase(user.getRole()) && "Pending".equals(apt.getStatus())) { %>
                        <td>
                            <form action="${pageContext.request.contextPath}/patient/cancelAppointment" method="post">
                                <input type="hidden" name="appointmentId" value="<%= apt.getAppointmentId() %>">
                                <button type="submit" class="btn btn-danger btn-small" onclick="return confirm('Are you sure you want to cancel this appointment?')">Cancel</button>
                            </form>
                        </td>
                    <% } %>
                </tr>
                <%      }
                    } else { %>
                <tr><td colspan="6" style="text-align:center">No appointments found</td></tr>
                <% } %>
            </tbody>
        </table>
    </div>
</body>
</html>