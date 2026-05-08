<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.User, com.medicareplus.model.Appointment, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    String role = user.getRole() == null ? "" : user.getRole().toLowerCase();
    String base = "admin".equals(role) ? "admin" : "doctor".equals(role) ? "doctor" : "patient";
    List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
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
        <div class="nav-logo"><h2>MediCare+ <%= role.substring(0, 1).toUpperCase() + role.substring(1) %></h2></div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/<%= base %>/dashboard">Dashboard</a>
            <% if ("admin".equals(role)) { %>
                <a href="${pageContext.request.contextPath}/admin/doctors">Doctors</a>
                <a href="${pageContext.request.contextPath}/admin/users">Patients</a>
                <a href="${pageContext.request.contextPath}/admin/appointments" class="active">Appointments</a>
                <a href="${pageContext.request.contextPath}/admin/medicalRecords">Medical Records</a>
            <% } else if ("doctor".equals(role)) { %>
                <a href="${pageContext.request.contextPath}/doctor/appointments" class="active">Appointments</a>
                <a href="${pageContext.request.contextPath}/doctor/medicalRecords">Medical Records</a>
            <% } else { %>
                <a href="${pageContext.request.contextPath}/patient/bookAppointment">Book Appointment</a>
                <a href="${pageContext.request.contextPath}/patient/myAppointments" class="active">Appointments</a>
                <a href="${pageContext.request.contextPath}/patient/medicalRecords">Medical Records</a>
            <% } %>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
        <div class="user-info"><%= user.getName() %></div>
    </div>
</nav>

<main class="dashboard-container">
    <header class="dashboard-header">
        <h1>Appointments</h1>
        <p>Review appointment date, time, doctor, patient, status, and notes.</p>
    </header>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>
    <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success"><%= request.getAttribute("success") %></div>
    <% } %>

    <% if ("admin".equals(role)) { %>
        <section class="panel-section">
            <form class="search-form" method="get" action="${pageContext.request.contextPath}/admin/appointments">
                <input type="text" name="search" placeholder="Search patient, doctor, appointment ID, or notes" value="<%= request.getAttribute("search") == null ? "" : request.getAttribute("search") %>">
                <button class="btn btn-primary" type="submit">Search</button>
            </form>
        </section>
    <% } %>

    <section class="panel-section">
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <% if (!"patient".equals(role)) { %><th>Patient</th><% } %>
                    <% if (!"doctor".equals(role)) { %><th>Doctor</th><% } %>
                    <th>Date</th><th>Time</th><th>Notes</th><th>Status</th><th>Action</th>
                </tr>
            </thead>
            <tbody>
            <% if (appointments != null && !appointments.isEmpty()) {
                for (Appointment apt : appointments) {
                    String status = apt.getStatus() == null ? "pending" : apt.getStatus().toLowerCase();
            %>
                <tr>
                    <td><%= apt.getAppointmentId() %></td>
                    <% if (!"patient".equals(role)) { %><td><%= apt.getPatientName() %></td><% } %>
                    <% if (!"doctor".equals(role)) { %><td><%= apt.getDoctorName() %><br><small><%= apt.getDoctorSpecialization() %></small></td><% } %>
                    <td><%= apt.getAppointmentDate() %></td>
                    <td><%= apt.getAppointmentTime() %></td>
                    <td><%= apt.getNotes() == null || apt.getNotes().isEmpty() ? (apt.getSymptoms() == null ? "N/A" : apt.getSymptoms()) : apt.getNotes() %></td>
                    <td><span class="status-<%= status %>"><%= status %></span></td>
                    <td>
                        <% if ("admin".equals(role) || "doctor".equals(role)) { %>
                            <form class="inline-form" action="${pageContext.request.contextPath}/<%= base %>/updateAppointment" method="post">
                                <input type="hidden" name="appointmentId" value="<%= apt.getAppointmentId() %>">
                                <select name="status">
                                    <option value="pending" <%= "pending".equals(status) ? "selected" : "" %>>Pending</option>
                                    <option value="approved" <%= "approved".equals(status) ? "selected" : "" %>>Approved</option>
                                    <option value="completed" <%= "completed".equals(status) ? "selected" : "" %>>Completed</option>
                                    <option value="cancelled" <%= "cancelled".equals(status) ? "selected" : "" %>>Cancelled</option>
                                </select>
                                <button class="btn btn-small btn-primary" type="submit">Save</button>
                            </form>
                            <% if ("doctor".equals(role) && !"cancelled".equals(status)) { %>
                                <a class="btn btn-small btn-secondary" href="${pageContext.request.contextPath}/doctor/medicalRecords">Record</a>
                            <% } %>
                        <% } else if ("pending".equals(status)) { %>
                            <form action="${pageContext.request.contextPath}/patient/cancelAppointment" method="post">
                                <input type="hidden" name="appointmentId" value="<%= apt.getAppointmentId() %>">
                                <button type="submit" class="btn btn-danger btn-small" onclick="return confirm('Cancel this appointment?')">Cancel</button>
                            </form>
                        <% } else { %>
                            <span class="no-action">No action</span>
                        <% } %>
                    </td>
                </tr>
            <%  }
            } else { %>
                <tr><td colspan="8" class="empty-cell">No appointments found.</td></tr>
            <% } %>
            </tbody>
        </table>
    </section>
</main>
</body>
</html>
