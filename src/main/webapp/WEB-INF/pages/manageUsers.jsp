<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.medicareplus.model.User, com.medicareplus.model.Patient" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"admin".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    List<User> users = (List<User>) request.getAttribute("users");
    List<Patient> patients = (List<Patient>) request.getAttribute("patients");
    String search = request.getAttribute("search") == null ? "" : request.getAttribute("search").toString();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare+ - Patients and Users</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar">
    <div class="nav-container">
        <div class="nav-logo"><h2>MediCare+ Admin</h2></div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/doctors">Doctors</a>
            <a href="${pageContext.request.contextPath}/admin/users" class="active">Patients</a>
            <a href="${pageContext.request.contextPath}/admin/appointments">Appointments</a>
            <a href="${pageContext.request.contextPath}/admin/medicalRecords">Medical Records</a>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
        <div class="user-info">Welcome, <%= user.getName() %></div>
    </div>
</nav>

<main class="dashboard-container">
    <header class="dashboard-header">
        <h1>Patients and Users</h1>
        <p>View patient details and account roles in a clean administrative view.</p>
    </header>

    <section class="panel-section">
        <form class="search-form" method="get" action="${pageContext.request.contextPath}/admin/users">
            <input type="text" name="search" placeholder="Search patient name, email, or phone" value="<%= search %>">
            <button class="btn btn-primary" type="submit">Search</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/users">Reset</a>
        </form>
    </section>

    <section class="panel-section">
        <h2>Patients</h2>
        <table class="data-table">
            <thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Gender</th><th>Blood Group</th><th>Phone</th><th>Emergency</th><th>Address</th></tr></thead>
            <tbody>
            <% if (patients != null && !patients.isEmpty()) {
                for (Patient p : patients) { %>
                <tr>
                    <td><%= p.getPatientId() %></td>
                    <td><%= p.getName() %></td>
                    <td><%= p.getEmail() == null ? "N/A" : p.getEmail() %></td>
                    <td><%= p.getGender() == null ? "N/A" : p.getGender() %></td>
                    <td><%= p.getBloodGroup() == null ? "N/A" : p.getBloodGroup() %></td>
                    <td><%= p.getContact() == null ? "N/A" : p.getContact() %></td>
                    <td><%= p.getEmergencyContact() == null ? "N/A" : p.getEmergencyContact() %></td>
                    <td><%= p.getAddress() == null ? "N/A" : p.getAddress() %></td>
                </tr>
            <%  }
            } else { %>
                <tr><td colspan="8" class="empty-cell">No patients found.</td></tr>
            <% } %>
            </tbody>
        </table>
    </section>

    <section class="panel-section">
        <h2>User Accounts</h2>
        <table class="data-table">
            <thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Role</th><th>Status</th></tr></thead>
            <tbody>
            <% if (users != null && !users.isEmpty()) {
                for (User u : users) { %>
                <tr>
                    <td><%= u.getUserId() %></td>
                    <td><%= u.getName() %></td>
                    <td><%= u.getEmail() %></td>
                    <td><span class="role-pill"><%= u.getRole() %></span></td>
                    <td><%= u.isActive() ? "Active" : "Inactive" %></td>
                </tr>
            <%  }
            } else { %>
                <tr><td colspan="5" class="empty-cell">No users found.</td></tr>
            <% } %>
            </tbody>
        </table>
    </section>
</main>
</body>
</html>
