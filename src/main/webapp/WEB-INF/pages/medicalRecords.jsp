<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.User, com.medicareplus.model.Appointment, com.medicareplus.model.MedicalRecord, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    String role = user.getRole() == null ? "" : user.getRole().toLowerCase();
    List<MedicalRecord> records = (List<MedicalRecord>) request.getAttribute("records");
    List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
    String base = role.equals("admin") ? "admin" : role.equals("doctor") ? "doctor" : "patient";
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
        <div class="nav-logo"><h2>MediCare+ <%= role.substring(0, 1).toUpperCase() + role.substring(1) %></h2></div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/<%= base %>/dashboard">Dashboard</a>
            <% if ("doctor".equals(role)) { %>
                <a href="${pageContext.request.contextPath}/doctor/appointments">Appointments</a>
                <a href="${pageContext.request.contextPath}/doctor/medicalRecords" class="active">Medical Records</a>
                <a href="${pageContext.request.contextPath}/doctor/about">About</a>
                <a href="${pageContext.request.contextPath}/doctor/contact">Contact</a>
            <% } else if ("admin".equals(role)) { %>
                <a href="${pageContext.request.contextPath}/admin/doctors">Doctors</a>
                <a href="${pageContext.request.contextPath}/admin/users">Patients</a>
                <a href="${pageContext.request.contextPath}/admin/appointments">Appointments</a>
                <a href="${pageContext.request.contextPath}/admin/medicalRecords" class="active">Medical Records</a>
            <% } else { %>
                <a href="${pageContext.request.contextPath}/patient/bookAppointment">Book Appointment</a>
                <a href="${pageContext.request.contextPath}/patient/myAppointments">Appointments</a>
                <a href="${pageContext.request.contextPath}/patient/medicalRecords" class="active">Medical Records</a>
                <a href="${pageContext.request.contextPath}/patient/about">About</a>
                <a href="${pageContext.request.contextPath}/patient/contact">Contact</a>
            <% } %>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
        <div class="user-info"><%= user.getName() %></div>
    </div>
</nav>

<main class="dashboard-container">
    <header class="dashboard-header">
        <h1>Medical Records</h1>
        <p><%= "doctor".equals(role) ? "Add diagnosis, medicines, suggestions, and treatment history for your assigned patients." : "Review diagnosis, prescribed medicines, suggestions, and treatment history." %></p>
    </header>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>
    <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success"><%= request.getAttribute("success") %></div>
    <% } %>

    <% if ("admin".equals(role)) { %>
        <section class="panel-section">
            <form class="search-form" method="get" action="${pageContext.request.contextPath}/admin/medicalRecords">
                <input type="text" name="search" placeholder="Search patient, doctor, appointment ID, diagnosis, medicines" value="<%= request.getAttribute("search") == null ? "" : request.getAttribute("search") %>">
                <button class="btn btn-primary" type="submit">Search</button>
            </form>
        </section>
    <% } %>

    <% if ("doctor".equals(role)) { %>
        <section class="panel-section">
            <h2>Add or Update Treatment Record</h2>
            <form action="${pageContext.request.contextPath}/doctor/saveMedicalRecord" method="post" class="record-form">
                <div class="form-grid">
                    <div class="form-group full-span">
                        <label for="appointmentId">Assigned Appointment</label>
                        <select id="appointmentId" name="appointmentId" required>
                            <option value="">Select appointment</option>
                            <% if (appointments != null) {
                                for (Appointment apt : appointments) {
                                    String status = apt.getStatus() == null ? "" : apt.getStatus().toLowerCase();
                                    if (!"cancelled".equals(status)) { %>
                                <option value="<%= apt.getAppointmentId() %>">#<%= apt.getAppointmentId() %> - <%= apt.getPatientName() %> - <%= apt.getAppointmentDate() %> <%= apt.getAppointmentTime() %></option>
                            <%      }
                                }
                            } %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="diagnosis">Diagnosis</label>
                        <textarea id="diagnosis" name="diagnosis" required placeholder="Enter diagnosis details"></textarea>
                    </div>
                    <div class="form-group">
                        <label for="prescription">Prescribed Medicines</label>
                        <textarea id="prescription" name="prescription" required placeholder="Medicine name, dose, frequency"></textarea>
                    </div>
                    <div class="form-group">
                        <label for="suggestions">Suggestions/Recommendations</label>
                        <textarea id="suggestions" name="suggestions" placeholder="Diet, lifestyle, follow-up advice"></textarea>
                    </div>
                    <div class="form-group">
                        <label for="treatmentHistory">Treatment History</label>
                        <textarea id="treatmentHistory" name="treatmentHistory" placeholder="Progress notes and treatment updates"></textarea>
                    </div>
                    <div class="form-group full-span">
                        <label for="notes">Additional Notes</label>
                        <textarea id="notes" name="notes" placeholder="Optional internal notes"></textarea>
                    </div>
                </div>
                <button class="btn btn-primary" type="submit">Save Medical Record</button>
            </form>
        </section>
    <% } %>

    <section class="medical-records">
        <% if (records != null && !records.isEmpty()) {
            for (MedicalRecord record : records) { %>
            <article class="record-card">
                <div class="record-header">
                    <div>
                        <h2><%= record.getPatientName() %></h2>
                        <p>Doctor: <%= record.getDoctorName() %> | Appointment #<%= record.getAppointmentId() %> | <%= record.getAppointmentDate() %> <%= record.getAppointmentTime() %></p>
                    </div>
                    <span class="record-date"><%= record.getUpdatedAt() != null ? record.getUpdatedAt() : record.getCreatedAt() %></span>
                </div>
                <div class="record-sections">
                    <section><h3>Patient Information</h3><p><%= record.getPatientName() %></p></section>
                    <section><h3>Diagnosis</h3><p><%= record.getDiagnosis() == null ? "No diagnosis recorded." : record.getDiagnosis() %></p></section>
                    <section><h3>Prescribed Medicines</h3><p><%= record.getPrescription() == null ? "No medicines recorded." : record.getPrescription() %></p></section>
                    <section><h3>Suggestions/Recommendations</h3><p><%= record.getSuggestions() == null || record.getSuggestions().isEmpty() ? "No suggestions recorded." : record.getSuggestions() %></p></section>
                    <section><h3>Treatment History</h3><p><%= record.getTreatmentHistory() == null || record.getTreatmentHistory().isEmpty() ? "No treatment updates recorded." : record.getTreatmentHistory() %></p></section>
                </div>
            </article>
        <%  }
        } else { %>
            <div class="empty-state">No medical records found.</div>
        <% } %>
    </section>
</main>
</body>
</html>
