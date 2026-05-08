<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.User"%>
<%
User user = (User) session.getAttribute("user");
String role = user == null ? "" : user.getRole().toLowerCase();
String area = "admin".equals(role) ? "admin"
		: ("doctor".equals(role) ? "doctor" : ("patient".equals(role) ? "patient" : ""));
String titleSuffix = area.isEmpty() ? "" : (" " + area.substring(0, 1).toUpperCase() + area.substring(1));
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>MediCare+ - About</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<nav class="navbar">
		<div class="nav-container">
			<div class="nav-logo">
				<h2>
					MediCare+<%=titleSuffix%></h2>
			</div>
			<div class="nav-links">
				<%
				if ("doctor".equals(role)) {
				%>
				<a href="${pageContext.request.contextPath}/doctor/dashboard">Dashboard</a>
				<a href="${pageContext.request.contextPath}/doctor/appointments">Appointments</a>
				<a href="${pageContext.request.contextPath}/doctor/medicalRecords">Medical
					Records</a> <a href="${pageContext.request.contextPath}/doctor/about"
					class="active">About</a> <a
					href="${pageContext.request.contextPath}/doctor/contact">Contact</a>
				<a href="${pageContext.request.contextPath}/logout">Logout</a>
				<%
				} else if ("patient".equals(role)) {
				%>
				<a href="${pageContext.request.contextPath}/patient/dashboard">Dashboard</a>
				<a href="${pageContext.request.contextPath}/patient/bookAppointment">Book
					Appointment</a> <a
					href="${pageContext.request.contextPath}/patient/myAppointments">Appointments</a>
				<a href="${pageContext.request.contextPath}/patient/medicalRecords">Medical
					Records</a> <a href="${pageContext.request.contextPath}/patient/about"
					class="active">About</a> <a
					href="${pageContext.request.contextPath}/patient/contact">Contact</a>
				<a href="${pageContext.request.contextPath}/logout">Logout</a>
				<%
				} else if ("admin".equals(role)) {
				%>
				<a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
				<a href="${pageContext.request.contextPath}/admin/doctors">Doctors</a>
				<a href="${pageContext.request.contextPath}/admin/users">Patients</a>
				<a href="${pageContext.request.contextPath}/admin/appointments">Appointments</a>
				<a href="${pageContext.request.contextPath}/admin/medicalRecords">Medical
					Records</a> <a href="${pageContext.request.contextPath}/admin/about"
					class="active">About</a> <a
					href="${pageContext.request.contextPath}/admin/contact">Contact</a>
				<a href="${pageContext.request.contextPath}/logout">Logout</a>
				<%
				} else {
				%>
				<a href="${pageContext.request.contextPath}/login">Login</a> <a
					href="${pageContext.request.contextPath}/register">Register</a>
				<%
				}
				%>
			</div>
			<%
			if (user != null) {
			%><div class="user-info">
				Welcome,
				<%=user.getName()%></div>
			<%
			}
			%>
		</div>
	</nav>

	<main class="about-container">
		<header class="about-header">
			<h1>About MediCare+</h1>
			<p>A hospital management system for appointments, treatment
				records, doctors, and patients.</p>
		</header>

		<section class="about-section">
			<h2>Mission</h2>
			<p>To provide a secure and efficient platform that helps patients
				book appointments and helps healthcare teams manage care records
				professionally.</p>
		</section>

		<section class="about-section">
			<h2>Hospital Services</h2>
			<ul>
				<li>Emergency and trauma care with rapid response support</li>
				<li>Electronic health records for centralized patient history</li>
				<li>Multi-specialty doctors across key clinical departments</li>
				<li>Patient portal for appointments and medical record access</li>
				<li>Role-based access for secure healthcare workflows</li>
			</ul>
		</section>

		<section class="about-section">
			<h2>Hospital Profile</h2>
			<ul>
				<li>25+ years of trusted healthcare service</li>
				<li>150+ experienced doctors</li>
				<li>300+ patient beds with ICU facilities</li>
				<li>24/7 emergency and ambulance service</li>
				<li>NMC registered hospital</li>
			</ul>
		</section>
	</main>
</body>
</html>
