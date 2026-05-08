package com.medicareplus.service;

public class EmailService {
    public void sendPasswordReset(String email, String resetLink) {
        System.out.println("Password reset email for " + email + ": " + resetLink);
    }
}
