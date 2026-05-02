<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.Doctor, jakarta.servlet.http.Cookie, java.util.List" %>
<%
    String rememberedEmail = "";
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("rememberedEmail".equals(cookie.getName())) {
                rememberedEmail = cookie.getValue();
                break;
            }
        }
    }
    List<Doctor> doctors = (List<Doctor>) request.getAttribute("doctors");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare+ - Secure Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20260503-login-fix">
</head>
<body class="login-page">
    <main class="login-shell">
        <section class="login-brand-panel">
            <div class="brand-mark">+</div>
            <p class="eyebrow">Smart Hospital Portal</p>
            <h1>HealthCare System</h1>
            <p class="brand-copy">Secure access for admins, doctors, and patients to manage appointments, records, and care workflows.</p>

            <div class="brand-stats">
                <div>
                    <span>24/7</span>
                    <small>Access</small>
                </div>
                <div>
                    <span>3</span>
                    <small>User Roles</small>
                </div>
                <div>
                    <span>JDBC</span>
                    <small>MySQL</small>
                </div>
            </div>
        </section>

        <section class="login-card" aria-labelledby="loginTitle">
            <div class="login-card-header">
                <div>
                    <p class="eyebrow">Welcome Back</p>
                    <h2 id="loginTitle">Login to MediCare+</h2>
                </div>
                <span class="secure-badge">Secure Session</span>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("error") %></div>
            <% } %>

            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success"><%= request.getAttribute("success") %></div>
            <% } %>

            <form id="loginForm" action="${pageContext.request.contextPath}/login" method="post" novalidate>
                <div class="role-tabs" role="tablist" aria-label="Select login role">
                    <button type="button" class="role-tab active" data-role="admin">Admin</button>
                    <button type="button" class="role-tab" data-role="doctor">Doctor</button>
                    <button type="button" class="role-tab" data-role="patient">Patient</button>
                </div>

                <div class="form-group">
                    <label for="role">Login Role</label>
                    <select id="role" name="role" required>
                        <option value="admin">Admin</option>
                        <option value="doctor">Doctor</option>
                        <option value="patient">Patient</option>
                    </select>
                </div>

                <div id="doctorPanel" class="doctor-login-panel" hidden>
                    <div>
                        <strong>Doctor Login Hint</strong>
                        <span>Use your registered doctor email, for example doctor@hospital.com</span>
                    </div>
                    <div class="doctor-meta">
                        <span id="doctorNamePreview">Doctor Name: Auto-detected after login</span>
                        <span>Specialization: Linked with doctor profile</span>
                    </div>
                    <% if (doctors != null && !doctors.isEmpty()) { %>
                        <select id="doctorQuickPick" aria-label="Doctor email hint">
                            <option value="">Select a sample doctor</option>
                            <% for (Doctor doctor : doctors) { %>
                                <option value="<%= doctor.getName() %>"><%= doctor.getName() %> - <%= doctor.getSpecialization() %></option>
                            <% } %>
                        </select>
                    <% } %>
                </div>

                <div class="form-group">
                    <label for="email">Email Address</label>
                    <div class="input-with-icon">
                        <span aria-hidden="true">✉</span>
                        <input type="email" id="email" name="email" value="<%= rememberedEmail %>" required placeholder="name@example.com" autocomplete="username">
                    </div>
                    <small class="field-error" id="emailError"></small>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <div class="input-with-icon">
                        <span aria-hidden="true">●</span>
                        <input type="password" id="password" name="password" required minlength="6" placeholder="Enter your password" autocomplete="current-password">
                        <button type="button" class="password-toggle" id="togglePassword" aria-label="Show password">Show</button>
                    </div>
                    <small class="field-error" id="passwordError"></small>
                </div>

                <div class="login-options">
                    <label class="checkbox-line">
                        <input type="checkbox" name="rememberMe" value="true" <%= rememberedEmail.isEmpty() ? "" : "checked" %>>
                        <span>Remember Me</span>
                    </label>
                    <a href="${pageContext.request.contextPath}/pages/forgetPassword.jsp">Forgot Password?</a>
                </div>

                <button type="submit" class="btn btn-primary btn-block login-submit">
                    <span class="btn-text">Login Securely</span>
                    <span class="spinner" aria-hidden="true"></span>
                </button>

                <div class="form-footer">
                    New patient? <a href="${pageContext.request.contextPath}/patient-register">Create New Account</a>
                </div>
            </form>
        </section>
    </main>

    <footer class="login-footer">
        © 2026 HealthCare System. All rights reserved.
    </footer>

    <script>
        const roleSelect = document.getElementById('role');
        const tabs = document.querySelectorAll('.role-tab');
        const doctorPanel = document.getElementById('doctorPanel');
        const form = document.getElementById('loginForm');
        const email = document.getElementById('email');
        const password = document.getElementById('password');
        const emailError = document.getElementById('emailError');
        const passwordError = document.getElementById('passwordError');
        const togglePassword = document.getElementById('togglePassword');

        function setRole(role) {
            roleSelect.value = role;
            tabs.forEach(tab => tab.classList.toggle('active', tab.dataset.role === role));
            doctorPanel.hidden = role !== 'doctor';
        }

        tabs.forEach(tab => tab.addEventListener('click', () => setRole(tab.dataset.role)));
        roleSelect.addEventListener('change', () => setRole(roleSelect.value));

        togglePassword.addEventListener('click', () => {
            const isPassword = password.type === 'password';
            password.type = isPassword ? 'text' : 'password';
            togglePassword.textContent = isPassword ? 'Hide' : 'Show';
            togglePassword.setAttribute('aria-label', isPassword ? 'Hide password' : 'Show password');
        });

        form.addEventListener('submit', (event) => {
            let valid = true;
            emailError.textContent = '';
            passwordError.textContent = '';

            if (!email.value.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value.trim())) {
                emailError.textContent = 'Enter a valid email address.';
                valid = false;
            }

            if (!password.value || password.value.length < 6) {
                passwordError.textContent = 'Password must be at least 6 characters.';
                valid = false;
            }

            if (!valid) {
                event.preventDefault();
                return;
            }

            form.classList.add('is-loading');
        });

        setRole(roleSelect.value);
    </script>
</body>
</html>
