package com.manjith.portfolio.exception;

public class FileStorageException extends RuntimeException {

    private final ErrorCode errorCode;

    public FileStorageException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
