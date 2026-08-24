package com.manjith.portfolio.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction over the file storage provider. ResumeServiceImpl depends
 * on this interface, not on Cloudinary's SDK directly — swapping
 * providers later (S3, local disk for tests, etc.) means writing a new
 * implementation of this interface, not touching business logic.
 */
public interface FileStorageService {

    CloudinaryUploadResult upload(MultipartFile file, String folder);

    void delete(String publicId);
}
