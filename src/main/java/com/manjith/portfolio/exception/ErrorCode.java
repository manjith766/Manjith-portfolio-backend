package com.manjith.portfolio.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    PROJECT_NOT_FOUND("PRJ-404", HttpStatus.NOT_FOUND, "Project not found"),
    CATEGORY_NOT_FOUND("CAT-404", HttpStatus.NOT_FOUND, "Category not found"),
    CATEGORY_INVALID_TYPE("CAT-400", HttpStatus.BAD_REQUEST, "Category is not of the expected type"),
    SKILL_NOT_FOUND("SKL-404", HttpStatus.NOT_FOUND, "One or more skills not found"),
    DUPLICATE_SLUG("PRJ-409", HttpStatus.CONFLICT, "A project with this title already exists"),
    DUPLICATE_SKILL_NAME("SKL-409", HttpStatus.CONFLICT, "A skill with this name already exists"),
    EXPERIENCE_NOT_FOUND("EXP-404", HttpStatus.NOT_FOUND, "Experience entry not found"),
    INVALID_DATE_RANGE("EXP-400", HttpStatus.BAD_REQUEST, "endDate must not be before startDate"),
    CURRENT_ROLE_WITH_END_DATE("EXP-400-2", HttpStatus.BAD_REQUEST, "A role marked as current must not have an endDate"),
    PAST_ROLE_MISSING_END_DATE("EXP-400-3", HttpStatus.BAD_REQUEST, "A role not marked as current must have an endDate"),
    EDUCATION_NOT_FOUND("EDU-404", HttpStatus.NOT_FOUND, "Education entry not found"),
    EDUCATION_INVALID_DATE_RANGE("EDU-400", HttpStatus.BAD_REQUEST, "endDate must not be before startDate"),
    MESSAGE_NOT_FOUND("MSG-404", HttpStatus.NOT_FOUND, "Message not found"),
    RESUME_NOT_FOUND("RSM-404", HttpStatus.NOT_FOUND, "No active resume found"),
    RESUME_VERSION_NOT_FOUND("RSM-404-2", HttpStatus.NOT_FOUND, "Resume version not found"),
    INVALID_FILE_TYPE("RSM-400-1", HttpStatus.BAD_REQUEST, "Only PDF files are accepted"),
    FILE_TOO_LARGE("RSM-400-2", HttpStatus.BAD_REQUEST, "File exceeds the maximum allowed size"),
    EMPTY_FILE("RSM-400-3", HttpStatus.BAD_REQUEST, "Uploaded file is empty"),
    CANNOT_DELETE_ACTIVE_RESUME("RSM-409", HttpStatus.CONFLICT, "Cannot delete the active resume — upload a replacement first"),
    STORAGE_UPLOAD_FAILED("STG-502-1", HttpStatus.BAD_GATEWAY, "File upload to storage provider failed"),
    STORAGE_DELETE_FAILED("STG-502-2", HttpStatus.BAD_GATEWAY, "File deletion from storage provider failed"),
    SETTING_NOT_FOUND("SET-404", HttpStatus.NOT_FOUND, "Setting not found"),
    SETTING_INVALID_KEY("SET-400", HttpStatus.BAD_REQUEST, "Setting key must contain only letters, numbers, dots, underscores, or hyphens"),
    SEO_ENTRY_NOT_FOUND("SEO-404", HttpStatus.NOT_FOUND, "SEO entry not found"),
    SOCIAL_LINK_NOT_FOUND("SOC-404", HttpStatus.NOT_FOUND, "Social link not found"),
    DUPLICATE_SOCIAL_LINK_PLATFORM("SOC-409", HttpStatus.CONFLICT, "A social link for this platform already exists"),
    CERTIFICATION_NOT_FOUND("CRT-404", HttpStatus.NOT_FOUND, "Certification not found"),
    BLOG_NOT_FOUND("BLG-404", HttpStatus.NOT_FOUND, "Blog post not found"),
    BLOG_DUPLICATE_SLUG("BLG-409", HttpStatus.CONFLICT, "A blog post with this title already exists"),
    COMMENT_NOT_FOUND("CMT-404", HttpStatus.NOT_FOUND, "Comment not found"),
    VALIDATION_FAILED("VAL-400", HttpStatus.BAD_REQUEST, "Request validation failed"),
    USER_NOT_FOUND("USR-404", HttpStatus.NOT_FOUND, "User not found"),
    INVALID_CREDENTIALS("AUTH-401-1", HttpStatus.UNAUTHORIZED, "Invalid username/email or password"),
    USER_DISABLED("AUTH-403-1", HttpStatus.FORBIDDEN, "This account has been disabled"),
    REFRESH_TOKEN_NOT_FOUND("AUTH-404-1", HttpStatus.NOT_FOUND, "Refresh token not found"),
    REFRESH_TOKEN_EXPIRED("AUTH-401-2", HttpStatus.UNAUTHORIZED, "Refresh token has expired"),
    REFRESH_TOKEN_REVOKED("AUTH-401-3", HttpStatus.UNAUTHORIZED, "Refresh token has been revoked"),
    INTERNAL_SERVER_ERROR("SRV-500", HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(String code, HttpStatus httpStatus, String defaultMessage) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
