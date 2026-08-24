package com.manjith.portfolio.controller;

import com.manjith.portfolio.dto.response.ExperienceResponseDTO;
import com.manjith.portfolio.service.ExperienceService;
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
@Tag(name = "Experience", description = "Public work-experience listing and detail endpoints")
public class ExperienceController {

    private final ExperienceService experienceService;

    @GetMapping("/api/experience")
    @Operation(summary = "List all work experience entries, ordered by display order")
    public ResponseEntity<List<ExperienceResponseDTO>> getAllExperience() {
        log.info("GET /api/experience");
        List<ExperienceResponseDTO> result = experienceService.getAllExperience();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/experience/{id}")
    @Operation(summary = "Get a single experience entry by id")
    public ResponseEntity<ExperienceResponseDTO> getExperienceById(@PathVariable Long id) {
        log.info("GET /api/experience/{}", id);
        ExperienceResponseDTO result = experienceService.getExperienceById(id);
        return ResponseEntity.ok(result);
    }
}
