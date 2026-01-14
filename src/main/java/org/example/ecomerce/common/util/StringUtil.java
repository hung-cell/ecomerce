package org.example.ecomerce.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class StringUtil {
    
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    
    private StringUtil() {
        throw new IllegalStateException("Utility class");
    }
    
    public static boolean isNullOrEmpty(String str) {
        return StringUtils.isBlank(str);
    }
    
    public static String toSlug(String input) {
        if (isNullOrEmpty(input)) {
            return "";
        }
        
        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
    
    public static String capitalize(String str) {
        return StringUtils.capitalize(str);
    }
    
    public static String truncate(String str, int maxLength) {
        return StringUtils.abbreviate(str, maxLength);
    }
    
    public static String maskEmail(String email) {
        if (isNullOrEmpty(email) || !email.contains("@")) {
            return email;
        }
        
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];
        
        if (localPart.length() <= 2) {
            return "*".repeat(localPart.length()) + "@" + domain;
        }
        
        return localPart.charAt(0) + "*".repeat(localPart.length() - 2) + 
               localPart.charAt(localPart.length() - 1) + "@" + domain;
    }
}

