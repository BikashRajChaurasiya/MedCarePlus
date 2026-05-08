<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !("admin".equalsIgnoreCase(user.getRole()) || "doctor".equalsIgnoreCase(user.getRole()) || "patient".equalsIgnoreCase(user.getRole()))) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    String role = user.getRole().toLowerCase();
    String titleSuffix = role.substring(0, 1).toUpperCase() + role.substring(1);
    String contactAction = request.getContextPath() + "/" + role + "/contact";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare+ - Contact</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar">
    <div class="nav-container">
        <div class="nav-logo"><h2>MediCare+ <%= titleSuffix %></h2></div>
        <div class="nav-links">
            <% if ("doctor".equals(role)) { %>
                <a href="${pageContext.request.contextPath}/doctor/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/doctor/appointments">Appointments</a>
                <a href="${pageContext.request.contextPath}/doctor/medicalRecords">Medical Records</a>
                <a href="${pageContext.request.contextPath}/doctor/about">About</a>
                <a href="${pageContext.request.contextPath}/doctor/contact" class="active">Contact</a>
            <% } else if ("patient".equals(role)) { %>
                <a href="${pageContext.request.contextPath}/patient/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/patient/bookAppointment">Book Appointment</a>
                <a href="${pageContext.request.contextPath}/patient/myAppointments">Appointments</a>
                <a href="${pageContext.request.contextPath}/patient/medicalRecords">Medical Records</a>
                <a href="${pageContext.request.contextPath}/patient/about">About</a>
                <a href="${pageContext.request.contextPath}/patient/contact" class="active">Contact</a>
            <% } else { %>
                <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/doctors">Doctors</a>
                <a href="${pageContext.request.contextPath}/admin/users">Patients</a>
                <a href="${pageContext.request.contextPath}/admin/appointments">Appointments</a>
                <a href="${pageContext.request.contextPath}/admin/medicalRecords">Medical Records</a>
                <a href="${pageContext.request.contextPath}/admin/about">About</a>
                <a href="${pageContext.request.contextPath}/admin/contact" class="active">Contact</a>
            <% } %>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
        <div class="user-info">Welcome, <%= user.getName() %></div>
    </div>
</nav>

<main class="contact-container">
    <header class="contact-header">
        <h1>Contact Support</h1>
        <p>Hospital support and communication details.</p>
    </header>

    <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success"><%= request.getAttribute("success") %></div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <section class="contact-grid">
        <article class="contact-info">
            <h2>Support Information</h2>
            <div class="info-item"><div></div><div><h3>Address</h3><p>MediCare+ Hospital, Kamalpokhari, Kathmandu, Nepal</p></div></div>
            <div class="info-item"><div></div><div><h3>Phone</h3><p>+977-9804297438</p></div></div>
            <div class="info-item"><div></div><div><h3>Email</h3><p>dr.bikashraj@medicareplus.com</p></div></div>
            <div class="info-item"><div></div><div><h3>Support Hours</h3><p>Monday - Friday: 9:00 AM - 6:00 PM<br>Saturday: 10:00 AM - 4:00 PM</p></div></div>
        </article>

        <article class="contact-form">
            <h2>Send Message</h2>
            <form action="<%= contactAction %>" method="post">
                <div class="form-group"><label>Name</label><input type="text" name="name" value="<%= user.getName() %>" required></div>
                <div class="form-group"><label>Email</label><input type="email" name="email" value="<%= user.getEmail() %>" required></div>
                <div class="form-group"><label>Subject</label><input type="text" name="subject" required></div>
                <div class="form-group"><label>Message</label><textarea name="message" rows="5" required></textarea></div>
                <button type="submit" class="btn btn-primary">Send Message</button>
            </form>
        </article>
    </section>
</main>
</body>
</html>
