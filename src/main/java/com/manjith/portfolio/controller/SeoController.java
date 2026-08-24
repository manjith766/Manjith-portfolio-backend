package com.manjith.portfolio.controller;

import com.manjith.portfolio.dto.response.SeoResponseDTO;
import com.manjith.portfolio.service.SeoMetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "SEO", description = "Public per-page SEO metadata, for React Helmet on the frontend")
public class SeoController {

    private final SeoMetadataService seoMetadataService;

    // pagePath is a query param, not a path variable — it contains
    // slashes ("/about", "/projects") which don't sit cleanly in a REST
    // path segment without ambiguity against this controller's own route.
    @GetMapping("/api/seo")
    @Operation(summary = "Get SEO metadata for a given frontend page path")
    public ResponseEntity<SeoResponseDTO> getSeoForPage(@RequestParam String pagePath) {
        log.info("GET /api/seo pagePath={}", pagePath);
        SeoResponseDTO result = seoMetadataService.getSeoForPage(pagePath);
        return ResponseEntity.ok(result);
    }
}
