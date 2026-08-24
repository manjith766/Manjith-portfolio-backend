package com.manjith.portfolio.controller.admin;

import com.manjith.portfolio.dto.request.SeoRequestDTO;
import com.manjith.portfolio.dto.response.SeoResponseDTO;
import com.manjith.portfolio.service.SeoMetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - SEO", description = "Per-page SEO metadata management")
public class AdminSeoController {

    private final SeoMetadataService seoMetadataService;

    @GetMapping("/api/admin/seo")
    @Operation(summary = "List SEO metadata for all pages")
    public ResponseEntity<List<SeoResponseDTO>> getAllSeoEntries() {
        log.info("GET /api/admin/seo");
        List<SeoResponseDTO> result = seoMetadataService.getAllSeoEntries();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/api/admin/seo")
    @Operation(summary = "Create or update SEO metadata for a page (upserted by pagePath in the body)")
    public ResponseEntity<SeoResponseDTO> upsertSeoEntry(@Valid @RequestBody SeoRequestDTO requestDTO) {
        log.info("PUT /api/admin/seo pagePath={}", requestDTO.getPagePath());
        SeoResponseDTO result = seoMetadataService.upsertSeoEntry(requestDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/api/admin/seo/{id}")
    @Operation(summary = "Delete a page's SEO entry")
    public ResponseEntity<Void> deleteSeoEntry(@PathVariable Long id) {
        log.info("DELETE /api/admin/seo/{}", id);
        seoMetadataService.deleteSeoEntry(id);
        return ResponseEntity.noContent().build();
    }
}
