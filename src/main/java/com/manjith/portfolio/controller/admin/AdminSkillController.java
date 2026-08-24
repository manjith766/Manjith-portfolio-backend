package com.manjith.portfolio.controller.admin;

import com.manjith.portfolio.dto.request.SkillRequestDTO;
import com.manjith.portfolio.dto.response.SkillResponseDTO;
import com.manjith.portfolio.service.SkillService;
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
@Tag(name = "Admin - Skills", description = "Admin-only skill management endpoints")
public class AdminSkillController {

    private final SkillService skillService;

    @PostMapping("/api/admin/skills")
    @Operation(summary = "Create a new skill")
    public ResponseEntity<SkillResponseDTO> createSkill(
            @Valid @RequestBody SkillRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder) {
        log.info("POST /api/admin/skills name={}", requestDTO.getName());
        SkillResponseDTO created = skillService.createSkill(requestDTO);
        URI location = uriBuilder.path("/api/skills/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/api/admin/skills/{id}")
    @Operation(summary = "Full update of an existing skill")
    public ResponseEntity<SkillResponseDTO> updateSkill(
            @PathVariable Long id,
            @Valid @RequestBody SkillRequestDTO requestDTO) {
        log.info("PUT /api/admin/skills/{}", id);
        SkillResponseDTO updated = skillService.updateSkill(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api/admin/skills/{id}")
    @Operation(summary = "Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        log.info("DELETE /api/admin/skills/{}", id);
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}
