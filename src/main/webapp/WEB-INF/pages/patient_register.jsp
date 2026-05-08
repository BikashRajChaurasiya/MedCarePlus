<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.medicareplus.model.Patient" %>
<%
    Patient patient = (Patient) request.getAttribute("patient");
    if (patient == null) {
        patient = new Patient();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare+ :Patient Registration</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=patient-register-20260503">
</head>
<body class="patient-register-page">
    <main class="patient-register-shell">
        <section class="patient-register-card">
            <div class="register-header">
                <div class="register-mark">+</div>
                <div>
                    <p class="eyebrow">Patient Portal</p>
                    <h1>Create Patient Account</h1>
                    <p>Fill all required details to register and book appointments online.</p>
                </div>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("error") %></div>
            <% } %>

            <form id="patientRegisterForm" action="${pageContext.request.contextPath}/patient-register" method="post" novalidate>
                <div class="form-grid">
                    <div class="form-group">
                        <label for="fullName">Full Name *</label>
                        <div class="input-with-icon">
                            <span aria-hidden="true"></span>
                            <input type="text" id="fullName" name="fullName" required value="<%= patient.getFullName() != null ? patient.getFullName() : "" %>" placeholder="Enter full name">
                        </div>
                        <small class="field-error"></small>
                    </div>

                    <div class="form-group">
                        <label for="email">Email Address *</label>
                        <div class="input-with-icon">
                            <span>@</span>
                            <input type="email" id="email" name="email" required value="<%= patient.getEmail() != null ? patient.getEmail() : "" %>" placeholder="name@example.com">
                        </div>
                        <small class="field-error"></small>
                    </div>

                    <div class="form-group">
                        <label for="password">Password *</label>
                        <div class="input-with-icon">
                            <span aria-hidden="true"></span>
                            <input type="password" id="password" name="password" required minlength="6" placeholder="Minimum 6 characters">
                            <button type="button" class="password-toggle" data-target="password">Show</button>
                        </div>
                        <small class="field-error"></small>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password *</label>
                        <div class="input-with-icon">
                            <span aria-hidden="true"></span>
                            <input type="password" id="confirmPassword" name="confirmPassword" required minlength="6" placeholder="Re-enter password">
                            <button type="button" class="password-toggle" data-target="confirmPassword">Show</button>
                        </div>
                        <small class="field-error"></small>
                    </div>

                    <div class="form-group">
                        <label for="phone">Phone Number *</label>
                        <div class="input-with-icon">
                            <span>☎</span>
                            <input type="tel" id="phone" name="phone" required value="<%= patient.getContactPhone() != null ? patient.getContactPhone() : "" %>" placeholder="Digits only">
                        </div>
                        <small class="field-error"></small>
                    </div>

                    <div class="form-group">
                        <label for="gender">Gender *</label>
                        <select id="gender" name="gender" required>
                            <option value="">Select Gender</option>
                            <option value="Male" <%= "Male".equals(patient.getGender()) ? "selected" : "" %>>Male</option>
                            <option value="Female" <%= "Female".equals(patient.getGender()) ? "selected" : "" %>>Female</option>
                            <option value="Other" <%= "Other".equals(patient.getGender()) ? "selected" : "" %>>Other</option>
                        </select>
                        <small class="field-error"></small>
                    </div>

                    <div class="form-group">
                        <label for="dateOfBirth">Date of Birth *</label>
                        <input type="date" id="dateOfBirth" name="dateOfBirth" required value="<%= patient.getDateOfBirth() != null ? patient.getDateOfBirth() : "" %>">
                        <small class="field-error"></small>
                    </div>

                    <div class="form-group">
                        <label for="bloodGroup">Blood Group *</label>
                        <select id="bloodGroup" name="bloodGroup" required>
                            <option value="">Select Blood Group</option>
                            <option value="A+" <%= "A+".equals(patient.getBloodGroup()) ? "selected" : "" %>>A+</option>
                            <option value="A-" <%= "A-".equals(patient.getBloodGroup()) ? "selected" : "" %>>A-</option>
                            <option value="B+" <%= "B+".equals(patient.getBloodGroup()) ? "selected" : "" %>>B+</option>
                            <option value="B-" <%= "B-".equals(patient.getBloodGroup()) ? "selected" : "" %>>B-</option>
                            <option value="AB+" <%= "AB+".equals(patient.getBloodGroup()) ? "selected" : "" %>>AB+</option>
                            <option value="AB-" <%= "AB-".equals(patient.getBloodGroup()) ? "selected" : "" %>>AB-</option>
                            <option value="O+" <%= "O+".equals(patient.getBloodGroup()) ? "selected" : "" %>>O+</option>
                            <option value="O-" <%= "O-".equals(patient.getBloodGroup()) ? "selected" : "" %>>O-</option>
                        </select>
                        <small class="field-error"></small>
                    </div>

                    <div class="form-group">
                        <label for="emergencyContact">Emergency Contact Number *</label>
                        <div class="input-with-icon">
                            <span>☎</span>
                            <input type="tel" id="emergencyContact" name="emergencyContact" required value="<%= patient.getEmergencyContact() != null ? patient.getEmergencyContact() : "" %>" placeholder="Digits only">
                        </div>
                        <small class="field-error"></small>
                    </div>

                    <div class="form-group full-span">
                        <label for="address">Address *</label>
                        <textarea id="address" name="address" required placeholder="Enter full address"><%= patient.getAddress() != null ? patient.getAddress() : "" %></textarea>
                        <small class="field-error"></small>
                    </div>

                    <div class="form-group full-span">
                        <label for="medicalHistory">Medical History <span class="optional-text">(optional but recommended)</span></label>
                        <textarea id="medicalHistory" name="medicalHistory" placeholder="Allergies, long-term illness, current medication, past surgery, etc."><%= patient.getMedicalHistory() != null ? patient.getMedicalHistory() : "" %></textarea>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary btn-block register-submit">
                    <span class="btn-text">Register Patient</span>
                    <span class="spinner" aria-hidden="true"></span>
                </button>

                <div class="form-footer">
                    Already have an account? <a href="${pageContext.request.contextPath}/login">Login</a>
                </div>
            </form>
        </section>
    </main>

    <script>
        const form = document.getElementById('patientRegisterForm');
        const fields = {
            fullName: document.getElementById('fullName'),
            email: document.getElementById('email'),
            password: document.getElementById('password'),
            confirmPassword: document.getElementById('confirmPassword'),
            phone: document.getElementById('phone'),
            gender: document.getElementById('gender'),
            dateOfBirth: document.getElementById('dateOfBirth'),
            address: document.getElementById('address'),
            bloodGroup: document.getElementById('bloodGroup'),
            emergencyContact: document.getElementById('emergencyContact')
        };

        document.querySelectorAll('.password-toggle').forEach(button => {
            button.addEventListener('click', () => {
                const input = document.getElementById(button.dataset.target);
                const show = input.type === 'password';
                input.type = show ? 'text' : 'password';
                button.textContent = show ? 'Hide' : 'Show';
            });
        });

        form.addEventListener('submit', event => {
            let valid = true;
            document.querySelectorAll('.field-error').forEach(error => error.textContent = '');

            Object.values(fields).forEach(input => {
                if (!input.value.trim()) {
                    setError(input, 'This field is required.');
                    valid = false;
                }
            });

            if (fields.email.value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(fields.email.value.trim())) {
                setError(fields.email, 'Enter a valid email address.');
                valid = false;
            }

            if (fields.password.value.length < 6) {
                setError(fields.password, 'Password must be at least 6 characters.');
                valid = false;
            }

            if (fields.password.value !== fields.confirmPassword.value) {
                setError(fields.confirmPassword, 'Passwords do not match.');
                valid = false;
            }

            if (fields.phone.value && !/^\d{7,15}$/.test(fields.phone.value.trim())) {
                setError(fields.phone, 'Phone number must be numeric.');
                valid = false;
            }

            if (fields.emergencyContact.value && !/^\d{7,15}$/.test(fields.emergencyContact.value.trim())) {
                setError(fields.emergencyContact, 'Emergency contact must be numeric.');
                valid = false;
            }

            if (fields.dateOfBirth.value) {
                const dob = new Date(fields.dateOfBirth.value);
                const today = new Date();
                if (Number.isNaN(dob.getTime()) || dob >= today) {
                    setError(fields.dateOfBirth, 'Enter a valid past date.');
                    valid = false;
                }
            }

            if (!valid) {
                event.preventDefault();
                return;
            }

            form.classList.add('is-loading');
        });

        function setError(input, message) {
            const group = input.closest('.form-group');
            const error = group ? group.querySelector('.field-error') : null;
            if (error) error.textContent = message;
        }
    </script>
</body>
</html>
