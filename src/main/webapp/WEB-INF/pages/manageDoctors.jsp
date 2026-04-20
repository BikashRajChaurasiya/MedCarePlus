<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.User, com.medicareplus.model.Doctor, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"Admin".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare+ - Manage Doctors</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <div class="nav-logo">
                <h2>🏥 MediCare+ Admin</h2>
            </div>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/doctors" class="active">Manage Doctors</a>
                <a href="${pageContext.request.contextPath}/admin/appointments">Appointments</a>
                <a href="${pageContext.request.contextPath}/admin/users">Manage Users</a>
                <a href="${pageContext.request.contextPath}/logout">Logout</a>
            </div>
            <div class="user-info">
                Welcome, <%= user.getName() %>
            </div>
        </div>
    </nav>
    
    <div class="dashboard-container">
        <div class="dashboard-header">
            <h1>Manage Doctors</h1>
            <p>Add, update, or remove doctors from the system</p>
        </div>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success"><%= request.getAttribute("success") %></div>
        <% } %>
        
        <!-- Add Doctor Form -->
        <div class="add-form">
            <h3>Add New Doctor</h3>
            <form action="${pageContext.request.contextPath}/admin/addDoctor" method="post">
                <div class="form-row">
                    <div class="form-group">
                        <input type="text" name="name" placeholder="Full Name" required>
                    </div>
                    <div class="form-group">
                        <input type="email" name="email" placeholder="Email Address" required>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <input type="password" name="password" placeholder="Password" required>
                    </div>
                    <div class="form-group">
                        <input type="text" name="specialization" placeholder="Specialization" required>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <input type="text" name="availability" placeholder="Availability (e.g., Mon-Fri 9AM-5PM)" required>
                    </div>
                    <div class="form-group">
                        <input type="text" name="contact" placeholder="Contact Number" required>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">Add Doctor</button>
            </form>
        </div>
        
        <!-- Doctors List -->
        <div class="doctors-list">
            <h3>Existing Doctors</h3>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Specialization</th>
                        <th>Availability</th>
                        <th>Contact</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<Doctor> doctors = (List<Doctor>) request.getAttribute("doctors");
                        if (doctors != null && !doctors.isEmpty()) {
                            for (Doctor doctor : doctors) {
                    %>
                    <tr>
                        <td><%= doctor.getDoctorId() %></td>
                        <td><%= doctor.getName() %></td>
                        <td><%= doctor.getSpecialization() %></td>
                        <td><%= doctor.getAvailability() %></td>
                        <td><%= doctor.getContact() %></td>
                        <td>
                            <form action="${pageContext.request.contextPath}/admin/deleteDoctor" method="post" onsubmit="return confirm('Are you sure you want to delete this doctor?')">
                                <input type="hidden" name="doctorId" value="<%= doctor.getDoctorId() %>">
                                <button type="submit" class="btn btn-danger btn-small">Delete</button>
                            </form>
                        </td>
                    </tr>
                    <%      }
                        } else { %>
                    <tr><td colspan="6" style="text-align:center">No doctors found</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>