package com.manjith.portfolio.exception;

public class BusinessValidationException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessValidationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
