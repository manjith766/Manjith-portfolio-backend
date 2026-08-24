package com.manjith.portfolio.controller.admin;

import com.manjith.portfolio.dto.request.CertificationRequestDTO;
import com.manjith.portfolio.dto.response.CertificationResponseDTO;
import com.manjith.portfolio.service.CertificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Certifications", description = "Admin-only certification management endpoints")
public class AdminCertificationController {

    private final CertificationService certificationService;

    @PostMapping("/api/admin/certifications")
    @Operation(summary = "Create a new certification")
    public ResponseEntity<CertificationResponseDTO> createCertification(
            @Valid @RequestBody CertificationRequestDTO requestDTO) {
        log.info("POST /api/admin/certifications title={}", requestDTO.getTitle());
        CertificationResponseDTO created = certificationService.createCertification(requestDTO);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/api/admin/certifications/{id}")
    @Operation(summary = "Full update of an existing certification")
    public ResponseEntity<CertificationResponseDTO> updateCertification(
            @PathVariable Long id, @Valid @RequestBody CertificationRequestDTO requestDTO) {
        log.info("PUT /api/admin/certifications/{}", id);
        CertificationResponseDTO updated = certificationService.updateCertification(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api/admin/certifications/{id}")
    @Operation(summary = "Delete a certification")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long id) {
        log.info("DELETE /api/admin/certifications/{}", id);
        certificationService.deleteCertification(id);
        return ResponseEntity.noContent().build();
    }
}
