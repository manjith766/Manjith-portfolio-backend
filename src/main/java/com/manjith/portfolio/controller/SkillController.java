package com.manjith.portfolio.controller;

import com.manjith.portfolio.dto.response.SkillResponseDTO;
import com.manjith.portfolio.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Skills", description = "Public skills listing and detail endpoints")
public class SkillController {

    private final SkillService skillService;

    @GetMapping("/api/skills")
    @Operation(summary = "List all skills, optionally filtered by category slug")
    public ResponseEntity<List<SkillResponseDTO>> getSkills(
            @RequestParam(required = false) String categorySlug) {
        log.info("GET /api/skills categorySlug={}", categorySlug);
        List<SkillResponseDTO> result = skillService.getSkills(categorySlug);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/skills/{id}")
    @Operation(summary = "Get a single skill by id")
    public ResponseEntity<SkillResponseDTO> getSkillById(@PathVariable Long id) {
        log.info("GET /api/skills/{}", id);
        SkillResponseDTO result = skillService.getSkillById(id);
        return ResponseEntity.ok(result);
    }
}
