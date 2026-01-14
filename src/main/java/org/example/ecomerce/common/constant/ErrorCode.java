package org.example.ecomerce.common.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    
    // General Errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_001", "Internal server error"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "ERR_002", "Bad request"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "ERR_003", "Validation error"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR_004", "Resource not found"),
    
    // Authentication & Authorization
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_001", "Unauthorized access"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_002", "Invalid credentials"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_003", "Token has expired"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_004", "Invalid token"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_005", "Access forbidden"),
    
    // User Errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "User not found"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_002", "User already exists"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_003", "Email already exists"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER_004", "Invalid password"),
    
    // Product Errors
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROD_001", "Product not found"),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "PROD_002", "Product out of stock"),
    INVALID_PRODUCT_DATA(HttpStatus.BAD_REQUEST, "PROD_003", "Invalid product data"),
    
    // Order Errors
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_001", "Order not found"),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "ORDER_002", "Invalid order status"),
    CANNOT_CANCEL_ORDER(HttpStatus.BAD_REQUEST, "ORDER_003", "Cannot cancel order"),
    
    // Payment Errors
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PAY_001", "Payment failed"),
    INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "PAY_002", "Invalid payment method"),
    
    // File Upload Errors
    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "FILE_001", "File upload failed"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "FILE_002", "Invalid file type"),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "FILE_003", "File too large");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
    
    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

