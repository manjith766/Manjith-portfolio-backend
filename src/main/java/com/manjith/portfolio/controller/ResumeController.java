package com.manjith.portfolio.controller;

import com.manjith.portfolio.dto.response.ResumeResponseDTO;
import com.manjith.portfolio.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Resume", description = "Public resume metadata and download")
public class ResumeController {

    private final ResumeService resumeService;

    @GetMapping("/api/resume")
    @Operation(summary = "Get metadata for the currently active resume (filename, version, upload date)")
    public ResponseEntity<ResumeResponseDTO> getActiveResume() {
        log.info("GET /api/resume");
        ResumeResponseDTO result = resumeService.getActiveResume();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/resume/download")
    @Operation(summary = "Redirect to the active resume file — usable directly as an <a href> target")
    public ResponseEntity<Void> downloadActiveResume() {
        log.info("GET /api/resume/download");
        ResumeResponseDTO active = resumeService.getActiveResume();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(active.getFileUrl()))
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .build();
    }
}
