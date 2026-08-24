package com.manjith.portfolio.controller.admin;

import com.manjith.portfolio.dto.request.ExperienceRequestDTO;
import com.manjith.portfolio.dto.response.ExperienceResponseDTO;
import com.manjith.portfolio.service.ExperienceService;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Experience", description = "Admin-only work-experience management endpoints")
public class AdminExperienceController {

    private final ExperienceService experienceService;

    @PostMapping("/api/admin/experience")
    @Operation(summary = "Create a new experience entry")
    public ResponseEntity<ExperienceResponseDTO> createExperience(
            @Valid @RequestBody ExperienceRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder) {
        log.info("POST /api/admin/experience company={}", requestDTO.getCompanyName());
        ExperienceResponseDTO created = experienceService.createExperience(requestDTO);
        URI location = uriBuilder.path("/api/experience/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/api/admin/experience/{id}")
    @Operation(summary = "Full update of an existing experience entry")
    public ResponseEntity<ExperienceResponseDTO> updateExperience(
            @PathVariable Long id,
            @Valid @RequestBody ExperienceRequestDTO requestDTO) {
        log.info("PUT /api/admin/experience/{}", id);
        ExperienceResponseDTO updated = experienceService.updateExperience(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api/admin/experience/{id}")
    @Operation(summary = "Delete an experience entry")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long id) {
        log.info("DELETE /api/admin/experience/{}", id);
        experienceService.deleteExperience(id);
        return ResponseEntity.noContent().build();
    }
}
