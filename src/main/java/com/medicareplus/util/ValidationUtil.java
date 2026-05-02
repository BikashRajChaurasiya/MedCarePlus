package com.medicareplus.util;

import java.time.LocalDate;

public final class ValidationUtil {
    private ValidationUtil() {}
    public static boolean hasText(String value) { return value != null && !value.trim().isEmpty(); }
    public static boolean isValidEmail(String email) { return hasText(email) && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"); }
    public static boolean isValidPassword(String password) { return password != null && password.length() >= 6; }
    public static boolean isTodayOrFuture(String isoDate) {
        try { return !LocalDate.parse(isoDate).isBefore(LocalDate.now()); } catch (Exception e) { return false; }
    }
}
