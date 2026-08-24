package com.manjith.portfolio.controller.admin;

import com.manjith.portfolio.dto.request.EducationRequestDTO;
import com.manjith.portfolio.dto.response.EducationResponseDTO;
import com.manjith.portfolio.service.EducationService;
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
@Tag(name = "Admin - Education", description = "Admin-only education management endpoints")
public class AdminEducationController {

    private final EducationService educationService;

    @PostMapping("/api/admin/education")
    @Operation(summary = "Create a new education entry")
    public ResponseEntity<EducationResponseDTO> createEducation(@Valid @RequestBody EducationRequestDTO requestDTO) {
        log.info("POST /api/admin/education institution={}", requestDTO.getInstitution());
        EducationResponseDTO created = educationService.createEducation(requestDTO);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/api/admin/education/{id}")
    @Operation(summary = "Full update of an existing education entry")
    public ResponseEntity<EducationResponseDTO> updateEducation(
            @PathVariable Long id, @Valid @RequestBody EducationRequestDTO requestDTO) {
        log.info("PUT /api/admin/education/{}", id);
        EducationResponseDTO updated = educationService.updateEducation(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api/admin/education/{id}")
    @Operation(summary = "Delete an education entry")
    public ResponseEntity<Void> deleteEducation(@PathVariable Long id) {
        log.info("DELETE /api/admin/education/{}", id);
        educationService.deleteEducation(id);
        return ResponseEntity.noContent().build();
    }
}
