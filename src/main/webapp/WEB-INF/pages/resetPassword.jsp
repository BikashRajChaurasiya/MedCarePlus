<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare+ - Reset Password</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="form-container">
            <div class="logo">
                <h1>MediCare+</h1>
                <p>Create New Password</p>
            </div>

            <h2>Reset Password</h2>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("error") %></div>
            <% } %>

            <form action="${pageContext.request.contextPath}/resetPassword" method="post">
                <input type="hidden" name="token" value="<%= request.getAttribute("token") == null ? "" : request.getAttribute("token") %>">
                <div class="form-group">
                    <label for="newPassword">New Password</label>
                    <input type="password" id="newPassword" name="newPassword" required minlength="6" placeholder="Min 6 characters" autocomplete="new-password">
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Confirm New Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required minlength="6" placeholder="Re-enter new password" autocomplete="new-password">
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-primary btn-block">Reset Password</button>
                </div>
                <div class="form-footer">
                    <a href="${pageContext.request.contextPath}/login">Back to Login</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
