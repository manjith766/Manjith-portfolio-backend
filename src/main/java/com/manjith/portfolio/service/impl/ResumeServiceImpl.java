package com.manjith.portfolio.service.impl;

import com.manjith.portfolio.dto.response.ResumeResponseDTO;
import com.manjith.portfolio.entity.Resume;
import com.manjith.portfolio.exception.BusinessValidationException;
import com.manjith.portfolio.exception.ErrorCode;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.ResumeMapper;
import com.manjith.portfolio.repository.ResumeRepository;
import com.manjith.portfolio.service.ResumeService;
import com.manjith.portfolio.storage.CloudinaryUploadResult;
import com.manjith.portfolio.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * VERSIONING STRATEGY: every upload creates a NEW row (never overwrites
 * an existing one) with version = previousActiveVersion + 1, and the
 * previous active row is deactivated in the same transaction via a bulk
 * UPDATE (ResumeRepository.deactivateAllActive) rather than
 * load-then-save, so the DB's partial unique index
 * (uq_resume_single_active) is never briefly violated by two active rows
 * existing at once mid-transaction.
 *
 * The superseded row's Cloudinary file is NOT deleted automatically —
 * old versions stay downloadable via getAllVersions() until an admin
 * explicitly calls deleteVersion(). This is a deliberate "keep history"
 * choice, not an oversight; flip it if you'd rather old files disappear
 * automatically on every new upload.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private static final String CLOUDINARY_FOLDER = "portfolio/resume";
    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024; // 5MB
    private static final String ALLOWED_CONTENT_TYPE = "application/pdf";

    private final ResumeRepository resumeRepository;
    private final ResumeMapper resumeMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public ResumeResponseDTO getActiveResume() {
        log.info("API entry: getActiveResume");

        Resume resume = resumeRepository.findByIsActiveTrue()
                .orElseThrow(() -> {
                    log.warn("No active resume found");
                    return new ResourceNotFoundException(ErrorCode.RESUME_NOT_FOUND, "No active resume has been uploaded yet");
                });

        log.info("API exit: getActiveResume returning id={}, version={}", resume.getId(), resume.getVersion());
        return resumeMapper.toResponseDTO(resume);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeResponseDTO> getAllVersions() {
        log.info("API entry: getAllVersions");

        List<ResumeResponseDTO> response = resumeRepository.findAllByOrderByVersionDesc().stream()
                .map(resumeMapper::toResponseDTO)
                .collect(Collectors.toList());

        log.info("API exit: getAllVersions returning {} versions", response.size());
        return response;
    }

    @Override
    @Transactional
    public ResumeResponseDTO uploadResume(MultipartFile file) {
        log.info("API entry: uploadResume filename={}", file.getOriginalFilename());

        validateFile(file);

        int nextVersion = resumeRepository.findByIsActiveTrue()
                .map(active -> active.getVersion() + 1)
                .orElse(1);

        CloudinaryUploadResult uploadResult = fileStorageService.upload(file, CLOUDINARY_FOLDER);

        int deactivated = resumeRepository.deactivateAllActive();
        log.debug("Deactivated {} previously-active resume row(s)", deactivated);

        Resume resume = Resume.builder()
                .fileUrl(uploadResult.secureUrl())
                .fileName(file.getOriginalFilename())
                .version(nextVersion)
                .isActive(true)
                .cloudinaryPublicId(uploadResult.publicId())
                .build();

        Resume saved = resumeRepository.save(resume);
        log.debug("Persisted resume id={} version={}", saved.getId(), saved.getVersion());

        log.info("API exit: uploadResume created id={} version={}", saved.getId(), saved.getVersion());
        return resumeMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteVersion(Long id) {
        log.info("API entry: deleteVersion id={}", id);

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Resume version not found id={}", id);
                    return new ResourceNotFoundException(ErrorCode.RESUME_VERSION_NOT_FOUND, "No resume version found with id: " + id);
                });

        if (Boolean.TRUE.equals(resume.getIsActive())) {
            log.warn("Refused to delete active resume id={}", id);
            throw new BusinessValidationException(ErrorCode.CANNOT_DELETE_ACTIVE_RESUME,
                    ErrorCode.CANNOT_DELETE_ACTIVE_RESUME.getDefaultMessage());
        }

        fileStorageService.delete(resume.getCloudinaryPublicId());
        resumeRepository.deleteById(id);

        log.info("API exit: deleteVersion deleted id={}", id);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("Validation failed: empty file upload");
            throw new BusinessValidationException(ErrorCode.EMPTY_FILE, ErrorCode.EMPTY_FILE.getDefaultMessage());
        }
        if (!ALLOWED_CONTENT_TYPE.equals(file.getContentType())) {
            log.warn("Validation failed: contentType={} is not {}", file.getContentType(), ALLOWED_CONTENT_TYPE);
            throw new BusinessValidationException(ErrorCode.INVALID_FILE_TYPE, ErrorCode.INVALID_FILE_TYPE.getDefaultMessage());
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            log.warn("Validation failed: file size {} exceeds max {}", file.getSize(), MAX_FILE_SIZE_BYTES);
            throw new BusinessValidationException(ErrorCode.FILE_TOO_LARGE, ErrorCode.FILE_TOO_LARGE.getDefaultMessage());
        }
    }
}
