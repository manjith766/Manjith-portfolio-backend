package com.manjith.portfolio.controller;

import com.manjith.portfolio.constants.AppConstants;
import com.manjith.portfolio.dto.response.PageResponseDTO;
import com.manjith.portfolio.dto.response.ProjectResponseDTO;
import com.manjith.portfolio.dto.response.ProjectSummaryResponseDTO;
import com.manjith.portfolio.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public, unauthenticated read endpoints. Contains NO business logic —
 * every method is a direct pass-through to ProjectService. Does not touch
 * ProjectRepository or any entity type.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Public project listing and detail endpoints")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/api/projects")
    @Operation(summary = "List projects with pagination, filtering, and search")
    public ResponseEntity<PageResponseDTO<ProjectSummaryResponseDTO>> getProjects(
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDirection) {

        log.info("GET /api/projects categorySlug={}, featured={}, search={}, page={}, size={}",
                categorySlug, featured, search, page, size);
        PageResponseDTO<ProjectSummaryResponseDTO> result =
                projectService.getProjects(categorySlug, featured, search, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/projects/{slug}")
    @Operation(summary = "Get full project detail by slug")
    public ResponseEntity<ProjectResponseDTO> getProjectBySlug(@PathVariable String slug) {
        log.info("GET /api/projects/{}", slug);
        ProjectResponseDTO result = projectService.getProjectBySlug(slug);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/api/projects/{slug}/view")
    @Operation(summary = "Atomically increment the view count for a project")
    public ResponseEntity<Void> incrementViewCount(@PathVariable String slug) {
        log.info("PATCH /api/projects/{}/view", slug);
        projectService.incrementViewCount(slug);
        return ResponseEntity.noContent().build();
    }
}
