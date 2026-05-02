<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.User, com.medicareplus.model.Doctor, java.util.List" %>
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
    <title>MediCare+ - Book Appointment</title>
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
                <a href="${pageContext.request.contextPath}/patient/bookAppointment" class="active">Book Appointment</a>
                <a href="${pageContext.request.contextPath}/patient/myAppointments">My Appointments</a>
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
            <h1>Book an Appointment</h1>
            <p>Select a doctor and schedule your consultation</p>
        </div>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success"><%= request.getAttribute("success") %></div>
        <% } %>
        
        <!-- Search Section -->
        <div class="search-section">
            <h3>Search Doctors by Specialization</h3>
            <form action="${pageContext.request.contextPath}/patient/searchDoctors" method="post" class="search-form">
                <input type="text" name="keyword" placeholder="Enter specialization (e.g., Cardiologist, Neurologist)" value="<%= request.getAttribute("searchKeyword") != null ? request.getAttribute("searchKeyword") : "" %>">
                <button type="submit" class="btn btn-primary">Search</button>
                <a href="${pageContext.request.contextPath}/patient/bookAppointment" class="btn btn-secondary">Reset</a>
            </form>
        </div>
        
        <!-- Doctors List -->
        <div class="doctors-section">
            <h3>Available Doctors</h3>
            <div class="doctors-grid">
                <%
                    List<Doctor> doctors = (List<Doctor>) request.getAttribute("doctors");
                    if (doctors != null && !doctors.isEmpty()) {
                        for (Doctor doctor : doctors) {
                %>
                <div class="doctor-card">
                    <div class="doctor-icon">👨‍⚕️</div>
                    <div class="doctor-name"><%= doctor.getName() %></div>
                    <div class="doctor-specialization"><%= doctor.getSpecialization() %></div>
                    <div class="doctor-qualification"><%= doctor.getQualification() %></div>
                    <div class="doctor-contact">📞 <%= doctor.getContact() %></div>
                    <div class="doctor-fee">💰 Fee: $<%= doctor.getConsultationFee() %></div>
                    
                    <form action="${pageContext.request.contextPath}/patient/bookAppointment" method="post">
                        <input type="hidden" name="doctorId" value="<%= doctor.getDoctorId() %>">
                        <div class="form-group">
                            <label>Date:</label>
                            <input type="date" name="date" required min="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>">
                        </div>
                        <div class="form-group">
                            <label>Time:</label>
                            <input type="time" name="time" required>
                        </div>
                        <div class="form-group">
                            <label>Symptoms:</label>
                            <textarea name="symptoms" rows="2" placeholder="Describe your symptoms..."></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary btn-block">Book Appointment</button>
                    </form>
                </div>
                <%      }
                    } else { %>
                <div class="no-doctors">No doctors found. Please try a different specialization.</div>
                <% } %>
            </div>
        </div>
    </div>
</body>
</html>