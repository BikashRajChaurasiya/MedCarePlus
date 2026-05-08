<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.User, com.medicareplus.model.Doctor, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"admin".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    List<Doctor> doctors = (List<Doctor>) request.getAttribute("doctors");
    String search = request.getAttribute("search") == null ? "" : request.getAttribute("search").toString();
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
        <div class="nav-logo"><h2>MediCare+ Admin</h2></div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/doctors" class="active">Doctors</a>
            <a href="${pageContext.request.contextPath}/admin/users">Patients</a>
            <a href="${pageContext.request.contextPath}/admin/appointments">Appointments</a>
            <a href="${pageContext.request.contextPath}/admin/medicalRecords">Medical Records</a>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
        <div class="user-info">Welcome, <%= user.getName() %></div>
    </div>
</nav>

<main class="dashboard-container">
    <header class="dashboard-header">
        <h1>Manage Doctors</h1>
        <p>Add doctors, update profile details, and manage availability.</p>
    </header>

    <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success"><%= request.getAttribute("success") %></div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <section class="panel-section">
        <form method="get" action="${pageContext.request.contextPath}/admin/doctors" class="search-form">
            <input type="text" name="search" placeholder="Search by doctor name, specialization, qualification, or phone" value="<%= search %>">
            <button class="btn btn-primary" type="submit">Search</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/doctors">Reset</a>
        </form>
    </section>

    <section class="panel-section">
        <h2>Add New Doctor</h2>
        <form action="${pageContext.request.contextPath}/admin/addDoctor" method="post" class="form-grid">
            <div class="form-group"><label>Full Name</label><input type="text" name="name" required></div>
            <div class="form-group"><label>Email</label><input type="email" name="email" required></div>
            <div class="form-group"><label>Password</label><input type="password" name="password" required minlength="6"></div>
            <div class="form-group"><label>Specialization</label><input type="text" name="specialization" required></div>
            <div class="form-group"><label>Qualification</label><input type="text" name="qualification"></div>
            <div class="form-group"><label>Experience Years</label><input type="number" name="experienceYears" min="0" value="0"></div>
            <div class="form-group"><label>Consultation Fee</label><input type="number" name="consultationFee" min="0" step="0.01" value="0"></div>
            <div class="form-group"><label>Phone</label><input type="text" name="contact" pattern="[0-9]{7,15}" title="Use 7 to 15 digits"></div>
            <div class="form-group"><label>Availability</label>
                <select name="availability">
                    <option value="available">Available</option>
                    <option value="busy">Busy</option>
                    <option value="off_duty">Off Duty</option>
                </select>
            </div>
            <div class="form-actions full-span"><button class="btn btn-primary" type="submit">Add Doctor</button></div>
        </form>
    </section>

    <section class="panel-section">
        <h2>Doctor Directory</h2>
        <table class="data-table">
            <thead><tr><th>ID</th><th>Name</th><th>Specialization</th><th>Qualification</th><th>Experience</th><th>Fee</th><th>Status</th><th>Actions</th></tr></thead>
            <tbody>
            <% if (doctors != null && !doctors.isEmpty()) {
                for (Doctor doctor : doctors) {
                    String modalId = "editDoctor" + doctor.getDoctorId();
            %>
                <tr>
                    <td><%= doctor.getDoctorId() %></td>
                    <td><%= doctor.getName() %></td>
                    <td><%= doctor.getSpecialization() %></td>
                    <td><%= doctor.getQualification() == null ? "N/A" : doctor.getQualification() %></td>
                    <td><%= doctor.getExperienceYears() %> years</td>
                    <td>Rs. <%= doctor.getConsultationFee() %></td>
                    <td><span class="status-approved"><%= doctor.getAvailability() %></span></td>
                    <td>
                        <div class="action-group">
                            <button class="btn btn-small btn-secondary" type="button" onclick="document.getElementById('<%= modalId %>').showModal()">Edit</button>
                            <form action="${pageContext.request.contextPath}/admin/deleteDoctor" method="post" onsubmit="return confirm('Delete this doctor?')">
                                <input type="hidden" name="doctorId" value="<%= doctor.getDoctorId() %>">
                                <button class="btn btn-small btn-danger" type="submit">Delete</button>
                            </form>
                        </div>
                        <dialog class="modal edit-modal" id="<%= modalId %>">
                            <form action="${pageContext.request.contextPath}/admin/updateDoctor" method="post" class="modal-card edit-modal-card">
                                <div class="edit-modal-header modal-drag-handle">
                                    <div>
                                        <h2>Edit Doctor</h2>
                                        <p><%= doctor.getName() %></p>
                                    </div>
                                    <button class="modal-close-button" type="button" aria-label="Close edit form" onclick="this.closest('dialog').close()">x</button>
                                </div>
                                <input type="hidden" name="doctorId" value="<%= doctor.getDoctorId() %>">
                                <div class="edit-modal-body">
                                    <div class="edit-form-sections">
                                    <section class="edit-form-section">
                                        <h3>Professional Details</h3>
                                        <div class="form-grid">
                                            <div class="form-group"><label>Specialization</label><input type="text" name="specialization" value="<%= doctor.getSpecialization() %>" required></div>
                                            <div class="form-group"><label>Qualification</label><input type="text" name="qualification" value="<%= doctor.getQualification() == null ? "" : doctor.getQualification() %>"></div>
                                            <div class="form-group"><label>Experience Years</label><input type="number" name="experienceYears" min="0" value="<%= doctor.getExperienceYears() %>"></div>
                                            <div class="form-group"><label>Consultation Fee</label><input type="number" name="consultationFee" min="0" step="0.01" value="<%= doctor.getConsultationFee() %>"></div>
                                        </div>
                                    </section>
                                    <section class="edit-form-section">
                                        <h3>Contact and Availability</h3>
                                        <div class="form-grid">
                                            <div class="form-group"><label>Phone</label><input type="text" name="contact" pattern="[0-9]{7,15}" value="<%= doctor.getContact() == null ? "" : doctor.getContact() %>"></div>
                                            <div class="form-group"><label>Availability</label>
                                                <select name="availability">
                                                    <option value="available" <%= "available".equalsIgnoreCase(doctor.getAvailability()) ? "selected" : "" %>>Available</option>
                                                    <option value="busy" <%= "busy".equalsIgnoreCase(doctor.getAvailability()) ? "selected" : "" %>>Busy</option>
                                                    <option value="off_duty" <%= "off_duty".equalsIgnoreCase(doctor.getAvailability()) ? "selected" : "" %>>Off Duty</option>
                                                </select>
                                            </div>
                                        </div>
                                    </section>
                                    </div>
                                </div>
                                <div class="form-actions edit-form-actions">
                                    <button class="btn btn-secondary" type="button" onclick="this.closest('dialog').close()">Close</button>
                                    <button class="btn btn-primary" type="submit">Save Changes</button>
                                </div>
                            </form>
                        </dialog>
                    </td>
                </tr>
            <%  }
            } else { %>
                <tr><td colspan="8" class="empty-cell">No doctors found.</td></tr>
            <% } %>
            </tbody>
        </table>
    </section>
</main>
<script>
    document.querySelectorAll('.edit-modal').forEach(function (modal) {
        var handle = modal.querySelector('.modal-drag-handle');
        var startX = 0;
        var startY = 0;
        var startLeft = 0;
        var startTop = 0;

        if (!handle) return;

        modal.addEventListener('close', function () {
            modal.style.left = '';
            modal.style.top = '';
            modal.style.transform = '';
        });

        handle.addEventListener('mousedown', function (event) {
            if (event.target.closest('button, input, select, textarea, a')) return;

            var rect = modal.getBoundingClientRect();
            startX = event.clientX;
            startY = event.clientY;
            startLeft = rect.left;
            startTop = rect.top;

            modal.style.left = startLeft + 'px';
            modal.style.top = startTop + 'px';
            modal.style.transform = 'none';
            modal.classList.add('is-dragging');
            event.preventDefault();

            function moveModal(moveEvent) {
                var nextLeft = startLeft + moveEvent.clientX - startX;
                var nextTop = startTop + moveEvent.clientY - startY;
                var maxLeft = window.innerWidth - modal.offsetWidth - 12;
                var maxTop = window.innerHeight - modal.offsetHeight - 12;

                modal.style.left = Math.max(12, Math.min(nextLeft, maxLeft)) + 'px';
                modal.style.top = Math.max(12, Math.min(nextTop, maxTop)) + 'px';
            }

            function stopDrag() {
                modal.classList.remove('is-dragging');
                document.removeEventListener('mousemove', moveModal);
                document.removeEventListener('mouseup', stopDrag);
            }

            document.addEventListener('mousemove', moveModal);
            document.addEventListener('mouseup', stopDrag);
        });
    });
</script></body>
</html>


