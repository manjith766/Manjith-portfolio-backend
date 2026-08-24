package com.manjith.portfolio.controller;

import com.manjith.portfolio.dto.response.CertificationResponseDTO;
import com.manjith.portfolio.service.CertificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Certifications", description = "Public certification listing and detail endpoints")
public class CertificationController {

    private final CertificationService certificationService;

    @GetMapping("/api/certifications")
    @Operation(summary = "List all certifications, ordered by display order")
    public ResponseEntity<List<CertificationResponseDTO>> getAllCertifications() {
        log.info("GET /api/certifications");
        List<CertificationResponseDTO> result = certificationService.getAllCertifications();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/certifications/{id}")
    @Operation(summary = "Get a single certification by id")
    public ResponseEntity<CertificationResponseDTO> getCertificationById(@PathVariable Long id) {
        log.info("GET /api/certifications/{}", id);
        CertificationResponseDTO result = certificationService.getCertificationById(id);
        return ResponseEntity.ok(result);
    }
}
