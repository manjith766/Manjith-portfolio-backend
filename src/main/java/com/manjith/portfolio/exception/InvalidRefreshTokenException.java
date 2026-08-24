package com.manjith.portfolio.exception;

public class InvalidRefreshTokenException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidRefreshTokenException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
