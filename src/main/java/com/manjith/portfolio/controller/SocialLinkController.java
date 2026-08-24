package com.manjith.portfolio.controller;

import com.manjith.portfolio.dto.response.SocialLinkResponseDTO;
import com.manjith.portfolio.service.SocialLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Social Links", description = "Public social link list")
public class SocialLinkController {

    private final SocialLinkService socialLinkService;

    @GetMapping("/api/social-links")
    @Operation(summary = "List all social links, ordered by display order")
    public ResponseEntity<List<SocialLinkResponseDTO>> getAllSocialLinks() {
        log.info("GET /api/social-links");
        List<SocialLinkResponseDTO> result = socialLinkService.getAllSocialLinks();
        return ResponseEntity.ok(result);
    }
}
