package com.pn.career.utils;

import java.util.regex.Pattern;

public class ValidationUtils {
    private static final String PHONE_REGEX = "^[0-9]{10,11}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    public static boolean isValidEmail(String email) {
        // Regular expression pattern for validating email addresses
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        // Create a Pattern object
        Pattern pattern = Pattern.compile(emailRegex);
        // Match the input email with the pattern
        return email != null && pattern.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phoneNumber).matches();
    }
    public static boolean isValidPassword(String password) {
        // Password validation: At least 3 characters
        return password != null && password.length() >= 3;
    }
}
