<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="com.medicareplus.model.User, com.medicareplus.model.Appointment, com.medicareplus.model.Patient, com.medicareplus.model.MedicalRecord, java.util.List"%>
<%
User user = (User) session.getAttribute("user");
if (user == null || !"admin".equalsIgnoreCase(user.getRole())) {
	response.sendRedirect(request.getContextPath() + "/login");
	return;
}
List<Appointment> appointments = (List<Appointment>) request.getAttribute("recentAppointments");
List<Patient> patients = (List<Patient>) request.getAttribute("patients");
List<MedicalRecord> records = (List<MedicalRecord>) request.getAttribute("medicalRecords");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>MediCare+ - Admin Dashboard</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<nav class="navbar">
		<div class="nav-container">
			<div class="nav-logo">
				<h2>MediCare+ Admin</h2>
			</div>
			<div class="nav-links">
				<a href="${pageContext.request.contextPath}/admin/dashboard"
					class="active">Dashboard</a> <a
					href="${pageContext.request.contextPath}/admin/doctors">Doctors</a>
				<a href="${pageContext.request.contextPath}/admin/users">Patients</a>
				<a href="${pageContext.request.contextPath}/admin/appointments">Appointments</a>
				<a href="${pageContext.request.contextPath}/admin/medicalRecords">Medical
					Records</a> <a href="${pageContext.request.contextPath}/logout">Logout</a>
			</div>
			<div class="user-info">
				Welcome,
				<%=user.getName()%></div>
		</div>
	</nav>

	<main class="dashboard-container">
		<header class="dashboard-header">
			<h1>Admin Dashboard</h1>
			<p>Monitor doctors, patients, appointments, and treatment records
				from one organized workspace.</p>
		</header>

		<%
		if (request.getAttribute("error") != null) {
		%>
		<div class="alert alert-error"><%=request.getAttribute("error")%></div>
		<%
		}
		%>
		<%
		if (request.getAttribute("success") != null) {
		%>
		<div class="alert alert-success"><%=request.getAttribute("success")%></div>
		<%
		}
		%>

		<section class="stats-grid">
			<article class="stat-card">
				<div class="stat-number"><%=request.getAttribute("totalDoctors")%></div>
				<div class="stat-label">Doctors</div>
			</article>
			<article class="stat-card">
				<div class="stat-number"><%=patients == null ? 0 : patients.size()%></div>
				<div class="stat-label">Patients</div>
			</article>
			<article class="stat-card">
				<div class="stat-number"><%=request.getAttribute("totalAppointments")%></div>
				<div class="stat-label">Appointments</div>
			</article>
			<article class="stat-card">
				<div class="stat-number"><%=request.getAttribute("pendingAppointments")%></div>
				<div class="stat-label">Pending Appointments</div>
			</article>
		</section>

		<section class="panel-section">
			<div class="section-title-row">
				<div>
					<h2>Recent Appointments</h2>
					<p>Update appointment status and monitor current activity.</p>
				</div>
				<a class="btn btn-secondary"
					href="${pageContext.request.contextPath}/admin/appointments">View
					All</a>
			</div>
			<table class="data-table">
				<thead>
					<tr>
						<th>ID</th>
						<th>Patient</th>
						<th>Doctor</th>
						<th>Date</th>
						<th>Time</th>
						<th>Status</th>
						<th>Update</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (appointments != null && !appointments.isEmpty()) {
						for (Appointment apt : appointments) {
							String status = apt.getStatus() == null ? "pending" : apt.getStatus().toLowerCase();
					%>
					<tr>
						<td><%=apt.getAppointmentId()%></td>
						<td><%=apt.getPatientName()%></td>
						<td><%=apt.getDoctorName()%></td>
						<td><%=apt.getDate()%></td>
						<td><%=apt.getTime()%></td>
						<td><span class="status-<%=status%>"><%=status%></span></td>
						<td>
							<form class="inline-form"
								action="${pageContext.request.contextPath}/admin/updateAppointment"
								method="post">
								<input type="hidden" name="appointmentId"
									value="<%=apt.getAppointmentId()%>"> <select
									name="status">
									<option value="pending"
										<%="pending".equals(status) ? "selected" : ""%>>Pending</option>
									<option value="approved"
										<%="approved".equals(status) ? "selected" : ""%>>Approved</option>
									<option value="completed"
										<%="completed".equals(status) ? "selected" : ""%>>Completed</option>
									<option value="cancelled"
										<%="cancelled".equals(status) ? "selected" : ""%>>Cancelled</option>
								</select>
								<button class="btn btn-small btn-primary" type="submit">Save</button>
							</form>
						</td>
					</tr>
					<%
					}
					} else {
					%>
					<tr>
						<td colspan="7" class="empty-cell">No appointments found.</td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
		</section>

		<section class="panel-grid two-columns">
			<article class="panel-section">
				<div class="section-title-row">
					<div>
						<h2>Patients</h2>
						<p>Registered patient profiles.</p>
					</div>
					<a class="btn btn-secondary"
						href="${pageContext.request.contextPath}/admin/users">Manage</a>
				</div>
				<table class="data-table compact-table">
					<thead>
						<tr>
							<th>Name</th>
							<th>Phone</th>
							<th>Blood Group</th>
						</tr>
					</thead>
					<tbody>
						<%
						if (patients != null && !patients.isEmpty()) {
							for (Patient patient : patients) {
						%>
						<tr>
							<td><%=patient.getName()%></td>
							<td><%=patient.getContact()%></td>
							<td><%=patient.getBloodGroup() == null ? "N/A" : patient.getBloodGroup()%></td>
						</tr>
						<%
						}
						} else {
						%>
						<tr>
							<td colspan="3" class="empty-cell">No patients found.</td>
						</tr>
						<%
						}
						%>
					</tbody>
				</table>
			</article>

			<article class="panel-section">
				<div class="section-title-row">
					<div>
						<h2>Medical Records</h2>
						<p>Latest treatment updates.</p>
					</div>
					<a class="btn btn-secondary"
						href="${pageContext.request.contextPath}/admin/medicalRecords">Open</a>
				</div>
				<table class="data-table compact-table">
					<thead>
						<tr>
							<th>Patient</th>
							<th>Doctor</th>
							<th>Diagnosis</th>
						</tr>
					</thead>
					<tbody>
						<%
						if (records != null && !records.isEmpty()) {
							int limit = Math.min(records.size(), 5);
							for (int i = 0; i < limit; i++) {
								MedicalRecord record = records.get(i);
						%>
						<tr>
							<td><%=record.getPatientName()%></td>
							<td><%=record.getDoctorName()%></td>
							<td><%=record.getDiagnosis()%></td>
						</tr>
						<%
						}
						} else {
						%>
						<tr>
							<td colspan="3" class="empty-cell">No medical records found.</td>
						</tr>
						<%
						}
						%>
					</tbody>
				</table>
			</article>
		</section>
	</main>
</body>
</html>
