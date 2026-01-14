package org.example.ecomerce.common.exception;

import org.example.ecomerce.common.constant.ErrorCode;

public class BadRequestException extends AppException {
    
    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
    
    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

