<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare+ - About Us</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <div class="nav-logo">
                <h2>🏥 MediCare+</h2>
            </div>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/login">Login</a>
                <a href="${pageContext.request.contextPath}/register">Register</a>
                <a href="${pageContext.request.contextPath}/pages/about.jsp" class="active">About</a>
                <a href="${pageContext.request.contextPath}/pages/contact.jsp">Contact</a>
            </div>
        </div>
    </nav>
    
    <div class="about-container">
        <div class="about-header">
            <h1>About MediCare+</h1>
            <p>Smart Hospital Appointment & Health Record System</p>
        </div>
        
        <div class="about-content">
            <div class="about-section">
                <h2>Our Mission</h2>
                <p>To provide a seamless, efficient, and secure digital platform that connects patients with healthcare providers, simplifying appointment booking and medical record management.</p>
            </div>
            
            <div class="about-section">
                <h2>What We Offer</h2>
                <ul>
                    <li>🏥 Online Appointment Booking - Book appointments with doctors anytime, anywhere</li>
                    <li>📋 Digital Medical Records - Secure storage and easy access to your health history</li>
                    <li>👨‍⚕️ Doctor Management - Comprehensive doctor profiles and scheduling</li>
                    <li>📊 Admin Dashboard - Complete system oversight and management</li>
                    <li>🔒 Secure Authentication - Role-based access control for data security</li>
                </ul>
            </div>
            
            <div class="about-section">
                <h2>Technology Stack</h2>
                <ul>
                    <li>Java J2EE - Backend development</li>
                    <li>JSP & Servlets - Web application framework</li>
                    <li>MySQL - Database management</li>
                    <li>HTML5, CSS3 - Frontend design</li>
                    <li>MVC Architecture - Clean code organization</li>
                </ul>
            </div>
        </div>
    </div>
</body>
</html>