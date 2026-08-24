package com.manjith.portfolio.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.manjith.portfolio.exception.ErrorCode;
import com.manjith.portfolio.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CloudinaryFileStorageService implements FileStorageService {

    private final Cloudinary cloudinary;

    public CloudinaryFileStorageService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        config.put("secure", true);
        this.cloudinary = new Cloudinary(config);
    }

    @Override
    public CloudinaryUploadResult upload(MultipartFile file, String folder) {
        log.info("Uploading file={} ({} bytes) to Cloudinary folder={}", file.getOriginalFilename(), file.getSize(), folder);

        try {
            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "auto", // PDFs are not images — "auto" lets Cloudinary route correctly
                    "use_filename", true,
                    "unique_filename", true
            );
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), options);

            String secureUrl = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");
            log.debug("Cloudinary upload succeeded, publicId={}", publicId);
            return new CloudinaryUploadResult(secureUrl, publicId);
        } catch (IOException e) {
            log.error("Cloudinary upload failed for file={}", file.getOriginalFilename(), e);
            throw new FileStorageException(ErrorCode.STORAGE_UPLOAD_FAILED,
                    "Failed to upload file to storage provider", e);
        }
    }

    @Override
    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            log.debug("Skipping Cloudinary delete — no publicId on record");
            return;
        }
        log.info("Deleting Cloudinary asset publicId={}", publicId);
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "auto"));
        } catch (IOException e) {
            // Deliberately does not fail the surrounding operation — an
            // orphaned file in Cloudinary storage is a cleanup nuisance,
            // not a correctness problem, and should not block the DB
            // transaction that's already committed the new active resume.
            log.error("Cloudinary delete failed for publicId={} — orphaned asset, needs manual cleanup", publicId, e);
        }
    }
}
