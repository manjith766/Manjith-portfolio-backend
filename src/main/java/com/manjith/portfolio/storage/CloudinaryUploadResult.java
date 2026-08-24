package com.manjith.portfolio.storage;

/**
 * Internal-only. Never returned from a controller — ResumeServiceImpl
 * consumes this and maps the relevant fields into Resume/ResumeResponseDTO.
 */
public record CloudinaryUploadResult(String secureUrl, String publicId) {
}
