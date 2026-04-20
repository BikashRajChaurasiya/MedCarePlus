<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare+ - Contact Us</title>
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
                <a href="${pageContext.request.contextPath}/pages/about.jsp">About</a>
                <a href="${pageContext.request.contextPath}/pages/contact.jsp" class="active">Contact</a>
            </div>
        </div>
    </nav>
    
    <div class="contact-container">
        <div class="contact-header">
            <h1>Contact Us</h1>
            <p>We're here to help and answer any questions you might have</p>
        </div>
        
        <div class="contact-grid">
            <div class="contact-info">
                <h2>Get in Touch</h2>
                <div class="info-item">
                    <span class="icon">📍</span>
                    <div>
                        <h3>Address</h3>
                        <p>Islington College, Kamalpokhari, Kathmandu, Nepal</p>
                    </div>
                </div>
                <div class="info-item">
                    <span class="icon">📞</span>
                    <div>
                        <h3>Phone</h3>
                        <p>+977-1-1234567</p>
                    </div>
                </div>
                <div class="info-item">
                    <span class="icon">✉️</span>
                    <div>
                        <h3>Email</h3>
                        <p>support@medicareplus.com</p>
                    </div>
                </div>
                <div class="info-item">
                    <span class="icon">🕐</span>
                    <div>
                        <h3>Support Hours</h3>
                        <p>Monday - Friday: 9:00 AM - 6:00 PM<br>Saturday: 10:00 AM - 4:00 PM</p>
                    </div>
                </div>
            </div>
            
            <div class="contact-form">
                <h2>Send us a Message</h2>
                <form action="#" method="post">
                    <div class="form-group">
                        <input type="text" name="name" placeholder="Your Name" required>
                    </div>
                    <div class="form-group">
                        <input type="email" name="email" placeholder="Your Email" required>
                    </div>
                    <div class="form-group">
                        <input type="text" name="subject" placeholder="Subject" required>
                    </div>
                    <div class="form-group">
                        <textarea name="message" rows="5" placeholder="Your Message" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Send Message</button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>