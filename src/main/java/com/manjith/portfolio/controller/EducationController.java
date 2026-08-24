package com.manjith.portfolio.controller;

import com.manjith.portfolio.dto.response.EducationResponseDTO;
import com.manjith.portfolio.service.EducationService;
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
@Tag(name = "Education", description = "Public education listing and detail endpoints")
public class EducationController {

    private final EducationService educationService;

    @GetMapping("/api/education")
    @Operation(summary = "List all education entries, ordered by display order")
    public ResponseEntity<List<EducationResponseDTO>> getAllEducation() {
        log.info("GET /api/education");
        List<EducationResponseDTO> result = educationService.getAllEducation();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/education/{id}")
    @Operation(summary = "Get a single education entry by id")
    public ResponseEntity<EducationResponseDTO> getEducationById(@PathVariable Long id) {
        log.info("GET /api/education/{}", id);
        EducationResponseDTO result = educationService.getEducationById(id);
        return ResponseEntity.ok(result);
    }
}
