package com.manjith.portfolio.controller.admin;

import com.manjith.portfolio.dto.request.ProjectRequestDTO;
import com.manjith.portfolio.dto.response.ProjectResponseDTO;
import com.manjith.portfolio.service.ProjectService;
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

/**
 * Admin-only CRUD. Secured via @PreAuthorize — assumes a Spring Security
 * config (JWT filter + role-based authorization) is wired at the
 * application level; that config is a separate module and is not
 * redefined here. Contains NO business logic — every method is a direct
 * pass-through to ProjectService. Does not touch ProjectRepository or any
 * entity type.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Projects", description = "Admin-only project management endpoints")
public class AdminProjectController {

    private final ProjectService projectService;

    @PostMapping("/api/admin/projects")
    @Operation(summary = "Create a new project")
    public ResponseEntity<ProjectResponseDTO> createProject(
            @Valid @RequestBody ProjectRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder) {
        log.info("POST /api/admin/projects title={}", requestDTO.getTitle());
        ProjectResponseDTO created = projectService.createProject(requestDTO);
        URI location = uriBuilder.path("/api/projects/{slug}").buildAndExpand(created.getSlug()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/api/admin/projects/{id}")
    @Operation(summary = "Full update of an existing project")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDTO requestDTO) {
        log.info("PUT /api/admin/projects/{}", id);
        ProjectResponseDTO updated = projectService.updateProject(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api/admin/projects/{id}")
    @Operation(summary = "Delete a project")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.info("DELETE /api/admin/projects/{}", id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
