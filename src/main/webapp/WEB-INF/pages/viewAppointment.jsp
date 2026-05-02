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
    <title>MediCare+ - My Appointments</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <div class="nav-logo">
                <h2>🏥 MediCare+ Patient</h2>
            </div>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/patient/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/patient/bookAppointment">Book Appointment</a>
                <a href="${pageContext.request.contextPath}/patient/myAppointments" class="active">My Appointments</a>
                <a href="${pageContext.request.contextPath}/patient/medicalRecords">Medical Records</a>
                <a href="${pageContext.request.contextPath}/logout">Logout</a>
            </div>
            <div class="user-info">
                Welcome, <%= user.getName() %>
            </div>
        </div>
    </nav>
    
    <div class="dashboard-container">
        <div class="dashboard-header">
            <h1>My Appointments</h1>
            <p>View and manage your appointments</p>
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
                    <th>Doctor Name</th>
                    <th>Specialization</th>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Symptoms</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <%
                    List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
                    if (appointments != null && !appointments.isEmpty()) {
                        for (Appointment apt : appointments) {
                            String statusClass = "status-" + (apt.getStatus() != null ? apt.getStatus().toLowerCase() : "pending");
                %>
                <td>
                    <td><%= apt.getAppointmentId() %></td>
                    <td><%= apt.getDoctorName() != null ? apt.getDoctorName() : "N/A" %></td>
                    <td><%= apt.getDoctorSpecialization() != null ? apt.getDoctorSpecialization() : "N/A" %></td>
                    <td><%= apt.getAppointmentDate() != null ? apt.getAppointmentDate() : "N/A" %></td>
                    <td><%= apt.getAppointmentTime() != null ? apt.getAppointmentTime() : "N/A" %></td>
                    <td><%= apt.getSymptoms() != null ? apt.getSymptoms() : "N/A" %></td>
                    <td><span class="<%= statusClass %>"><%= apt.getStatus() != null ? apt.getStatus() : "pending" %></span></td>
                    <td>
                        <% if ("pending".equalsIgnoreCase(apt.getStatus())) { %>
                            <form action="${pageContext.request.contextPath}/patient/cancelAppointment" method="post">
                                <input type="hidden" name="appointmentId" value="<%= apt.getAppointmentId() %>">
                                <button type="submit" class="btn btn-danger btn-small" onclick="return confirm('Are you sure you want to cancel this appointment?')">Cancel</button>
                            </form>
                        <% } else { %>
                            <span class="no-action">—</span>
                        <% } %>
                    </td>
                </tr>
                <%      }
                    } else { %>
                <tr><td colspan="8" style="text-align:center">No appointments found</td></tr>
                <% } %>
            </tbody>
        </table>
    </div>
</body>
</html>