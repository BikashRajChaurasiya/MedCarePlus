<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare+ - Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="error-container">
            <div class="error-icon">⚠️</div>
            <h1>Something Went Wrong</h1>
            <p>We encountered an error while processing your request.</p>
            
            <% if (request.getAttribute("error") != null) { %>
                <div class="error-details"><%= request.getAttribute("error") %></div>
            <% } else if (exception != null) { %>
                <div class="error-details"><%= exception.getMessage() %></div>
            <% } %>
            
            <div class="error-actions">
                <a href="javascript:history.back()" class="btn btn-primary">Go Back</a>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">Go to Login</a>
            </div>
        </div>
    </div>
</body>
</html>