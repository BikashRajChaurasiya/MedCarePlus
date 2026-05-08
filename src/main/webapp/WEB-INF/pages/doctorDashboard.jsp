<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.User, com.medicareplus.model.Doctor, com.medicareplus.model.Appointment, com.medicareplus.model.MedicalRecord, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"doctor".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    Doctor doctor = (Doctor) request.getAttribute("doctor");
    List<Appointment> appointments = (List<Appointment>) request.getAttribute("recentAppointments");
    List<MedicalRecord> records = (List<MedicalRecord>) request.getAttribute("recentRecords");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare+ - Doctor Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <!-- Added CSS only for profile formatting -->
    <style>
        .profile-detail-list {
            display: grid;
            grid-template-columns: 180px 1fr;
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
        <div class="nav-logo"><h2>MediCare+ Doctor</h2></div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/doctor/dashboard" class="active">Dashboard</a>
            <a href="${pageContext.request.contextPath}/doctor/appointments">Appointments</a>
            <a href="${pageContext.request.contextPath}/doctor/medicalRecords">Medical Records</a>
            <a href="${pageContext.request.contextPath}/doctor/about">About</a>
            <a href="${pageContext.request.contextPath}/doctor/contact">Contact</a>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
        <div class="user-info">Dr. <%= user.getName() %></div>
    </div>
</nav>

<main class="dashboard-container">
    <header class="dashboard-header">
        <h1>Doctor Dashboard</h1>
        <p>Review appointments, complete consultations, and update patient treatment records.</p>
    </header>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>
    <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success"><%= request.getAttribute("success") %></div>
    <% } %>

    <section class="stats-grid">
        <article class="stat-card"><div class="stat-number"><%= request.getAttribute("totalAppointments") %></div><div class="stat-label">Total Appointments</div></article>
        <article class="stat-card"><div class="stat-number"><%= request.getAttribute("pendingCount") %></div><div class="stat-label">Pending</div></article>
        <article class="stat-card"><div class="stat-number"><%= request.getAttribute("completedCount") %></div><div class="stat-label">Completed</div></article>
    </section>

    <section class="panel-grid two-columns">
        <article class="panel-section profile-panel">
            <h2>Doctor Profile</h2>
            <% if (doctor != null) { %>

                <!-- UPDATED PROFILE FORMAT -->
                <dl class="profile-detail-list">
                    <dt>Doctor Name:</dt>
                    <dd>Dr. <%= user.getName() %></dd>

                    <dt>Specialization:</dt>
                    <dd><%= doctor.getSpecialization() == null || doctor.getSpecialization().isEmpty() ? "N/A" : doctor.getSpecialization() %></dd>

                    <dt>Experience:</dt>
                    <dd><%= doctor.getExperienceYears() %> years</dd>

                    <dt>Qualification:</dt>
                    <dd><%= doctor.getQualification() == null || doctor.getQualification().isEmpty() ? "N/A" : doctor.getQualification() %></dd>

                    <dt>Contact:</dt>
                    <dd><%= doctor.getContact() == null || doctor.getContact().isEmpty() ? "N/A" : doctor.getContact() %></dd>

                    <dt>Consultation Fee:</dt>
                    <dd>Rs. <%= doctor.getConsultationFee() %></dd>

                    <dt>Availability:</dt>
                    <dd><%= doctor.getAvailability() == null || doctor.getAvailability().isEmpty() ? "N/A" : doctor.getAvailability() %></dd>
                </dl>

            <% } else { %>
                <p class="empty-state">Doctor profile is not linked to this account.</p>
            <% } %>
        </article>

        <article class="panel-section">
            <div class="section-title-row">
                <div><h2>Recent Records</h2><p>Latest treatment notes saved by you.</p></div>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/doctor/medicalRecords">Manage</a>
            </div>
            <div class="record-list">
                <% if (records != null && !records.isEmpty()) {
                    int limit = Math.min(records.size(), 3);
                    for (int i = 0; i < limit; i++) {
                        MedicalRecord record = records.get(i); %>
                    <div class="mini-record">
                        <strong><%= record.getPatientName() %></strong>
                        <span><%= record.getDiagnosis() %></span>
                    </div>
                <%  }
                } else { %>
                    <p class="empty-state">No medical records added yet.</p>
                <% } %>
            </div>
        </article>
    </section>

    <section class="panel-section">
        <div class="section-title-row">
            <div><h2>Upcoming Appointments</h2><p>Approve, complete, or cancel assigned appointments.</p></div>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/doctor/appointments">View All</a>
        </div>
        <table class="data-table">
            <thead><tr><th>ID</th><th>Patient</th><th>Date</th><th>Time</th><th>Status</th><th>Action</th></tr></thead>
            <tbody>
            <% if (appointments != null && !appointments.isEmpty()) {
                for (Appointment apt : appointments) {
                    String status = apt.getStatus() == null ? "pending" : apt.getStatus().toLowerCase();
            %>
                <tr>
                    <td><%= apt.getAppointmentId() %></td>
                    <td><%= apt.getPatientName() %></td>
                    <td><%= apt.getDate() %></td>
                    <td><%= apt.getTime() %></td>
                    <td><span class="status-<%= status %>"><%= status %></span></td>
                    <td>
                        <form class="inline-form" action="${pageContext.request.contextPath}/doctor/updateAppointment" method="post">
                            <input type="hidden" name="appointmentId" value="<%= apt.getAppointmentId() %>">
                            <select name="status">
                                <option value="pending" <%= "pending".equals(status) ? "selected" : "" %>>Pending</option>
                                <option value="approved" <%= "approved".equals(status) ? "selected" : "" %>>Approved</option>
                                <option value="completed" <%= "completed".equals(status) ? "selected" : "" %>>Completed</option>
                                <option value="cancelled" <%= "cancelled".equals(status) ? "selected" : "" %>>Cancelled</option>
                            </select>
                            <button class="btn btn-small btn-primary" type="submit">Save</button>
                        </form>
                    </td>
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