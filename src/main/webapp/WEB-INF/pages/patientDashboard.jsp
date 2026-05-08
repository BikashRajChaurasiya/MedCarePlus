<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.User, com.medicareplus.model.Patient, com.medicareplus.model.Appointment, com.medicareplus.model.MedicalRecord, java.util.List, java.time.LocalDate, java.time.Period" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"patient".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    Patient patient = (Patient) request.getAttribute("patient");
    List<Appointment> appointments = (List<Appointment>) request.getAttribute("recentAppointments");
    List<MedicalRecord> records = (List<MedicalRecord>) request.getAttribute("recentRecords");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare+ - Patient Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <!-- Added CSS only for profile formatting -->
    <style>
        .profile-detail-list {
            display: grid;
            grid-template-columns: 200px 1fr;
            row-gap: 10px;
        }
        .profile-detail-list dt {
            font-weight: 600;
        }
        .profile-detail-list dd {
            margin: 0;
        }
    </style>
</head>
<body>
<nav class="navbar">
    <div class="nav-container">
        <div class="nav-logo"><h2>MediCare+ Patient</h2></div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/patient/dashboard" class="active">Dashboard</a>
            <a href="${pageContext.request.contextPath}/patient/bookAppointment">Book Appointment</a>
            <a href="${pageContext.request.contextPath}/patient/myAppointments">Appointments</a>
            <a href="${pageContext.request.contextPath}/patient/medicalRecords">Medical Records</a>
            <a href="${pageContext.request.contextPath}/patient/about">About</a>
            <a href="${pageContext.request.contextPath}/patient/contact">Contact</a>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
        <div class="user-info">Welcome, <%= user.getName() %></div>
    </div>
</nav>

<main class="dashboard-container">
    <header class="dashboard-header">
        <h1>Patient Dashboard</h1>
        <p>Book appointments and review your diagnosis, medicines, suggestions, and treatment updates.</p>
    </header>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>
    <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success"><%= request.getAttribute("success") %></div>
    <% } %>

    <section class="stats-grid">
        <article class="stat-card"><div class="stat-number"><%= request.getAttribute("totalAppointments") %></div><div class="stat-label">Appointments</div></article>
        <article class="stat-card"><div class="stat-number"><%= request.getAttribute("pendingCount") %></div><div class="stat-label">Pending</div></article>
        <article class="stat-card"><div class="stat-number"><%= request.getAttribute("approvedCount") %></div><div class="stat-label">Approved</div></article>
        <article class="stat-card"><div class="stat-number"><%= request.getAttribute("completedCount") %></div><div class="stat-label">Completed</div></article>
    </section>

    <section class="panel-grid two-columns">
        <article class="panel-section profile-panel">
            <h2>Patient Profile</h2>
            <% if (patient != null) {
                String ageText = "N/A";
                try {
                    if (patient.getDateOfBirth() != null && !patient.getDateOfBirth().isEmpty()) {
                        ageText = Period.between(LocalDate.parse(patient.getDateOfBirth()), LocalDate.now()).getYears() + " years";
                    }
                } catch (Exception ignored) { }
            %>

            <!-- UPDATED PROFILE FORMAT -->
            <dl class="profile-detail-list">
                <dt>Patient Name:</dt>
                <dd><%= patient.getName() == null || patient.getName().isEmpty() ? user.getName() : patient.getName() %></dd>

                <dt>Blood Group:</dt>
                <dd><%= patient.getBloodGroup() == null || patient.getBloodGroup().isEmpty() ? "N/A" : patient.getBloodGroup() %></dd>

                <dt>Age:</dt>
                <dd><%= ageText %></dd>

                <dt>Date of Birth:</dt>
                <dd><%= patient.getDateOfBirth() == null || patient.getDateOfBirth().isEmpty() ? "N/A" : patient.getDateOfBirth() %></dd>

                <dt>Gender:</dt>
                <dd><%= patient.getGender() == null || patient.getGender().isEmpty() ? "N/A" : patient.getGender() %></dd>

                <dt>Contact:</dt>
                <dd><%= patient.getContact() == null || patient.getContact().isEmpty() ? "N/A" : patient.getContact() %></dd>

                <dt>Emergency Contact:</dt>
                <dd><%= patient.getEmergencyContact() == null || patient.getEmergencyContact().isEmpty() ? "N/A" : patient.getEmergencyContact() %></dd>

                <dt>Address:</dt>
                <dd><%= patient.getAddress() == null || patient.getAddress().isEmpty() ? "N/A" : patient.getAddress() %></dd>

                <dt>Medical History:</dt>
                <dd><%= patient.getMedicalHistory() == null || patient.getMedicalHistory().isEmpty() ? "No medical history recorded." : patient.getMedicalHistory() %></dd>
            </dl>

            <% } else { %>
                <p class="empty-state">Profile information is not available.</p>
            <% } %>
        </article>

        <article class="panel-section">
            <div class="section-title-row">
                <div><h2>Latest Treatment Updates</h2><p>Records appear automatically after your doctor saves them.</p></div>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/patient/medicalRecords">View Records</a>
            </div>
            <div class="record-list">
            <% if (records != null && !records.isEmpty()) {
                int limit = Math.min(records.size(), 3);
                for (int i = 0; i < limit; i++) {
                    MedicalRecord record = records.get(i); %>
                <div class="mini-record">
                    <strong><%= record.getDiagnosis() %></strong>
                    <span>Medicines: <%= record.getPrescription() == null ? "N/A" : record.getPrescription() %></span>
                </div>
            <%  }
            } else { %>
                <p class="empty-state">No treatment updates yet.</p>
            <% } %>
            </div>
        </article>
    </section>

    <section class="panel-section">
        <div class="section-title-row">
            <div><h2>Appointment History</h2><p>Your most recent appointments and their current status.</p></div>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/patient/myAppointments">View All</a>
        </div>
        <table class="data-table">
            <thead><tr><th>ID</th><th>Doctor</th><th>Specialization</th><th>Date</th><th>Time</th><th>Status</th></tr></thead>
            <tbody>
            <% if (appointments != null && !appointments.isEmpty()) {
                for (Appointment apt : appointments) {
                    String status = apt.getStatus() == null ? "pending" : apt.getStatus().toLowerCase();
            %>
                <tr>
                    <td><%= apt.getAppointmentId() %></td>
                    <td><%= apt.getDoctorName() %></td>
                    <td><%= apt.getDoctorSpecialization() %></td>
                    <td><%= apt.getAppointmentDate() %></td>
                    <td><%= apt.getAppointmentTime() %></td>
                    <td><span class="status-<%= status %>"><%= status %></span></td>
                </tr>
            <%  }
            } else { %>
                <tr><td colspan="6" class="empty-cell">No appointments found.</td></tr>
            <% } %>
            </tbody>
        </table>
    </section>
</main>
</body>
</html>