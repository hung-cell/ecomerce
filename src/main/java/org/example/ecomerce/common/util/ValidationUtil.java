package org.example.ecomerce.common.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^[+]?[0-9]{10,13}$");
    
    private static final Pattern PASSWORD_PATTERN = 
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    
    private ValidationUtil() {
        throw new IllegalStateException("Utility class");
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    public static boolean isPositive(Number number) {
        return number != null && number.doubleValue() > 0;
    }
    
    public static boolean isNonNegative(Number number) {
        return number != null && number.doubleValue() >= 0;
    }
}

