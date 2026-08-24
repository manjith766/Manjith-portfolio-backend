package com.manjith.portfolio.controller;

import com.manjith.portfolio.service.SettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Settings", description = "Public site-wide settings")
public class SettingController {

    private final SettingService settingService;

    @GetMapping("/api/settings")
    @Operation(summary = "Get all site settings as a flat key-value map, for frontend bootstrap on load")
    public ResponseEntity<Map<String, String>> getPublicSettings() {
        log.info("GET /api/settings");
        Map<String, String> result = settingService.getPublicSettingsMap();
        return ResponseEntity.ok(result);
    }
}
