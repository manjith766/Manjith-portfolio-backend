package com.manjith.portfolio.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(ex.getErrorCode(), ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateResource(
            DuplicateResourceException ex, HttpServletRequest request) {
        log.warn("Duplicate resource at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(ex.getErrorCode(), ex.getMessage(), request);
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessValidation(
            BusinessValidationException ex, HttpServletRequest request) {
        log.warn("Business validation failed at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(ex.getErrorCode(), ex.getMessage(), request);
    }

    @ExceptionHandler(com.manjith.portfolio.exception.FileStorageException.class)
    public ResponseEntity<ErrorResponseDTO> handleFileStorage(
            com.manjith.portfolio.exception.FileStorageException ex, HttpServletRequest request) {
        log.error("File storage failure at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(ex.getErrorCode(), ex.getMessage(), request);
    }

    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaxUploadSizeExceeded(
            org.springframework.web.multipart.MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.warn("Upload rejected at [{}]: exceeds container-level max size", request.getRequestURI());
        return buildResponse(ErrorCode.FILE_TOO_LARGE, ErrorCode.FILE_TOO_LARGE.getDefaultMessage(), request);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidRefreshToken(
            InvalidRefreshTokenException ex, HttpServletRequest request) {
        log.warn("Invalid refresh token at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(ex.getErrorCode(), ex.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Bad credentials at [{}]", request.getRequestURI());
        return buildResponse(ErrorCode.INVALID_CREDENTIALS, ErrorCode.INVALID_CREDENTIALS.getDefaultMessage(), request);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponseDTO> handleDisabledAccount(
            DisabledException ex, HttpServletRequest request) {
        log.warn("Disabled account login attempt at [{}]", request.getRequestURI());
        return buildResponse(ErrorCode.USER_DISABLED, ErrorCode.USER_DISABLED.getDefaultMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String combinedMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("Validation failed at [{}]: {}", request.getRequestURI(), combinedMessage);
        return buildResponse(ErrorCode.VALIDATION_FAILED, combinedMessage, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        log.warn("Constraint violation at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(ErrorCode.VALIDATION_FAILED, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at [{}]", request.getRequestURI(), ex);
        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage(), request);
    }

    private ResponseEntity<ErrorResponseDTO> buildResponse(
            ErrorCode errorCode, String message, HttpServletRequest request) {
        HttpStatus status = errorCode.getHttpStatus();
        ErrorResponseDTO body = ErrorResponseDTO.builder()
                .apiPath(request.getRequestURI())
                .errorCode(errorCode.getCode())
                .errorMessage(message)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
