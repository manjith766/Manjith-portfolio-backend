package com.manjith.portfolio.controller.admin;

import com.manjith.portfolio.dto.request.SocialLinkRequestDTO;
import com.manjith.portfolio.dto.response.SocialLinkResponseDTO;
import com.manjith.portfolio.service.SocialLinkService;
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
@Tag(name = "Admin - Social Links", description = "Social link management")
public class AdminSocialLinkController {

    private final SocialLinkService socialLinkService;

    @PostMapping("/api/admin/social-links")
    @Operation(summary = "Create a new social link")
    public ResponseEntity<SocialLinkResponseDTO> createSocialLink(@Valid @RequestBody SocialLinkRequestDTO requestDTO) {
        log.info("POST /api/admin/social-links platform={}", requestDTO.getPlatform());
        SocialLinkResponseDTO created = socialLinkService.createSocialLink(requestDTO);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/api/admin/social-links/{id}")
    @Operation(summary = "Update an existing social link")
    public ResponseEntity<SocialLinkResponseDTO> updateSocialLink(
            @PathVariable Long id, @Valid @RequestBody SocialLinkRequestDTO requestDTO) {
        log.info("PUT /api/admin/social-links/{}", id);
        SocialLinkResponseDTO updated = socialLinkService.updateSocialLink(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api/admin/social-links/{id}")
    @Operation(summary = "Delete a social link")
    public ResponseEntity<Void> deleteSocialLink(@PathVariable Long id) {
        log.info("DELETE /api/admin/social-links/{}", id);
        socialLinkService.deleteSocialLink(id);
        return ResponseEntity.noContent().build();
    }
}
