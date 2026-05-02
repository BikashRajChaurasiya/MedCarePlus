<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.User, com.medicareplus.model.Patient, com.medicareplus.model.Appointment, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"patient".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare+ - Patient Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <div class="nav-logo">
                <h2>🏥 MediCare+ Patient</h2>
            </div>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/patient/dashboard" class="active">Dashboard</a>
                <a href="${pageContext.request.contextPath}/patient/bookAppointment">Book Appointment</a>
                <a href="${pageContext.request.contextPath}/patient/myAppointments">My Appointments</a>
                <a href="${pageContext.request.contextPath}/patient/medicalRecords">Medical Records</a>
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
            <h1>Patient Dashboard</h1>
            <p>Manage your appointments and health records</p>
        </div>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success"><%= request.getAttribute("success") %></div>
        <% } %>
        
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">📅</div>
                <div class="stat-number"><%= request.getAttribute("totalAppointments") != null ? request.getAttribute("totalAppointments") : 0 %></div>
                <div class="stat-label">Total Appointments</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">⏳</div>
                <div class="stat-number"><%= request.getAttribute("pendingCount") != null ? request.getAttribute("pendingCount") : 0 %></div>
                <div class="stat-label">Pending</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">✅</div>
                <div class="stat-number"><%= request.getAttribute("approvedCount") != null ? request.getAttribute("approvedCount") : 0 %></div>
                <div class="stat-label">Approved</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">📋</div>
                <div class="stat-number"><%= request.getAttribute("completedCount") != null ? request.getAttribute("completedCount") : 0 %></div>
                <div class="stat-label">Completed</div>
            </div>
        </div>
        
        <div class="patient-info">
            <h3>Your Profile</h3>
            <% Patient patient = (Patient) request.getAttribute("patient"); %>
            <% if (patient != null) { %>
                <p><strong>Age:</strong> <%= patient.getAge() %> | 
                   <strong>Gender:</strong> <%= patient.getGender() %> | 
                   <strong>Blood Group:</strong> <%= patient.getBloodGroup() != null ? patient.getBloodGroup() : "N/A" %></p>
                <p><strong>Contact:</strong> <%= patient.getContact() %> | 
                   <strong>Emergency:</strong> <%= patient.getEmergencyContact() != null ? patient.getEmergencyContact() : "N/A" %></p>
                <p><strong>Address:</strong> <%= patient.getAddress() != null ? patient.getAddress() : "N/A" %></p>
            <% } else { %>
                <p>Profile information not available. Please contact administrator.</p>
            <% } %>
        </div>
        
        <div class="recent-section">
            <h3>Recent Appointments</h3>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Doctor</th>
                        <th>Specialization</th>
                        <th>Date</th>
                        <th>Time</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<Appointment> appointments = (List<Appointment>) request.getAttribute("recentAppointments");
                        if (appointments != null && !appointments.isEmpty()) {
                            for (Appointment apt : appointments) {
                                String status = apt.getStatus() != null ? apt.getStatus() : "pending";
                                String statusClass = "status-" + status.toLowerCase();
                    %>
                    <tr>
                        <td><%= apt.getAppointmentId() %></td>
                        <td><%= apt.getDoctorName() != null ? apt.getDoctorName() : "N/A" %></td>
                        <td><%= apt.getDoctorSpecialization() != null ? apt.getDoctorSpecialization() : "N/A" %></td>
                        <td><%= apt.getAppointmentDate() != null ? apt.getAppointmentDate() : "N/A" %></td>
                        <td><%= apt.getAppointmentTime() != null ? apt.getAppointmentTime() : "N/A" %></td>
                        <td><span class="<%= statusClass %>"><%= status %></span></td>
                    </tr>
                    <%      }
                        } else { %>
                    <tr>
                        <td colspan="6" style="text-align:center">No appointments found</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <div class="view-all">
                <a href="${pageContext.request.contextPath}/patient/myAppointments">View All Appointments →</a>
            </div>
        </div>
    </div>
</body>
</html>