package com.manjith.portfolio.controller.admin;

import com.manjith.portfolio.dto.request.SettingRequestDTO;
import com.manjith.portfolio.dto.response.SettingResponseDTO;
import com.manjith.portfolio.service.SettingService;
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
@Tag(name = "Admin - Settings", description = "Site settings management")
public class AdminSettingController {

    private final SettingService settingService;

    @GetMapping("/api/admin/settings")
    @Operation(summary = "List all settings with metadata (for the admin editing UI)")
    public ResponseEntity<List<SettingResponseDTO>> getAllSettings() {
        log.info("GET /api/admin/settings");
        List<SettingResponseDTO> result = settingService.getAllSettingsForAdmin();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/api/admin/settings/{key}")
    @Operation(summary = "Create or update a setting by key")
    public ResponseEntity<SettingResponseDTO> upsertSetting(
            @PathVariable String key, @Valid @RequestBody SettingRequestDTO requestDTO) {
        log.info("PUT /api/admin/settings/{}", key);
        SettingResponseDTO result = settingService.upsertSetting(key, requestDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/api/admin/settings/{key}")
    @Operation(summary = "Delete a setting by key")
    public ResponseEntity<Void> deleteSetting(@PathVariable String key) {
        log.info("DELETE /api/admin/settings/{}", key);
        settingService.deleteSetting(key);
        return ResponseEntity.noContent().build();
    }
}
