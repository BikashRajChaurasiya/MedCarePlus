<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.User, com.medicareplus.model.MedicalRecord, java.util.List" %>
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
    <title>MediCare+ - Medical Records</title>
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
                <a href="${pageContext.request.contextPath}/patient/myAppointments">My Appointments</a>
                <a href="${pageContext.request.contextPath}/patient/medicalRecords" class="active">Medical Records</a>
                <a href="${pageContext.request.contextPath}/logout">Logout</a>
            </div>
            <div class="user-info">
                Welcome, <%= user.getName() %>
            </div>
        </div>
    </nav>
    
    <div class="dashboard-container">
        <div class="dashboard-header">
            <h1>Your Medical Records</h1>
            <p>View your diagnosis and prescription history</p>
        </div>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>
        
        <div class="medical-records">
            <%
                List<MedicalRecord> records = (List<MedicalRecord>) request.getAttribute("records");
                if (records != null && !records.isEmpty()) {
                    for (MedicalRecord record : records) {
            %>
            <div class="record-card">
                <div class="record-header">
                    <div class="record-doctor">👨‍⚕️ Dr. <%= record.getDoctorName() != null ? record.getDoctorName() : "Unknown" %></div>
                    <div class="record-date">📅 <%= record.getCreatedAt() != null ? record.getCreatedAt() : "N/A" %></div>
                </div>
                <div class="record-diagnosis">
                    <strong>Diagnosis:</strong>
                    <p><%= record.getDiagnosis() != null ? record.getDiagnosis() : "No diagnosis recorded" %></p>
                </div>
                <div class="record-prescription">
                    <strong>Prescription:</strong>
                    <p><%= record.getPrescription() != null && !record.getPrescription().isEmpty() ? record.getPrescription() : "No prescription provided" %></p>
                </div>
            </div>
            <%      }
                } else { %>
            <div class="no-records">
                <p>No medical records found.</p>
                <p>Your medical records will appear here after your consultations.</p>
            </div>
            <% } %>
        </div>
    </div>
</body>
</html>