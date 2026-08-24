package com.manjith.portfolio.controller.admin;

import com.manjith.portfolio.dto.response.ResumeResponseDTO;
import com.manjith.portfolio.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Admin-only. Contains NO business logic — validation (file type/size),
 * versioning, and the Cloudinary call all live in ResumeServiceImpl; this
 * controller only unwraps the multipart request.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Resume", description = "Resume upload and version management")
public class AdminResumeController {

    private final ResumeService resumeService;

    @PostMapping(value = "/api/admin/resume", consumes = "multipart/form-data")
    @Operation(summary = "Upload a new resume PDF, making it the active version")
    public ResponseEntity<ResumeResponseDTO> uploadResume(@RequestParam("file") MultipartFile file) {
        log.info("POST /api/admin/resume filename={}, size={}", file.getOriginalFilename(), file.getSize());
        ResumeResponseDTO result = resumeService.uploadResume(file);
        return ResponseEntity.status(201).body(result);
    }

    @GetMapping("/api/admin/resume/versions")
    @Operation(summary = "List all resume versions, most recent first")
    public ResponseEntity<List<ResumeResponseDTO>> getAllVersions() {
        log.info("GET /api/admin/resume/versions");
        List<ResumeResponseDTO> result = resumeService.getAllVersions();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/api/admin/resume/{id}")
    @Operation(summary = "Delete a non-active resume version")
    public ResponseEntity<Void> deleteVersion(@PathVariable Long id) {
        log.info("DELETE /api/admin/resume/{}", id);
        resumeService.deleteVersion(id);
        return ResponseEntity.noContent().build();
    }
}
