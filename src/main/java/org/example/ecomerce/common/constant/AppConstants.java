package org.example.ecomerce.common.constant;

public class AppConstants {
    
    // Pagination
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";
    
    // API Response Messages
    public static final String SUCCESS = "Success";
    public static final String CREATED = "Created successfully";
    public static final String UPDATED = "Updated successfully";
    public static final String DELETED = "Deleted successfully";
    public static final String NOT_FOUND = "Resource not found";
    public static final String BAD_REQUEST = "Bad request";
    public static final String UNAUTHORIZED = "Unauthorized access";
    public static final String FORBIDDEN = "Access forbidden";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    
    // User Roles
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_VENDOR = "ROLE_VENDOR";
    
    // File Upload
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/jpg", "image/png", "image/gif"};
    
    // Date Format
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }
}

