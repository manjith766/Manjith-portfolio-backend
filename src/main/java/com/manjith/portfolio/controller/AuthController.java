package com.manjith.portfolio.controller;

import com.manjith.portfolio.dto.request.LoginRequestDTO;
import com.manjith.portfolio.dto.request.RefreshTokenRequestDTO;
import com.manjith.portfolio.dto.response.AuthResponseDTO;
import com.manjith.portfolio.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public authentication endpoints. Contains NO business logic — every
 * method is a direct pass-through to AuthService.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Login, token refresh, and logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/auth/login")
    @Operation(summary = "Authenticate and receive an access + refresh token pair")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        log.info("POST /api/auth/login usernameOrEmail={}", requestDTO.getUsernameOrEmail());
        AuthResponseDTO response = authService.login(requestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/auth/refresh")
    @Operation(summary = "Exchange a valid refresh token for a new access + refresh token pair")
    public ResponseEntity<AuthResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO requestDTO) {
        log.info("POST /api/auth/refresh");
        AuthResponseDTO response = authService.refresh(requestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/auth/logout")
    @Operation(summary = "Revoke a refresh token")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDTO requestDTO) {
        log.info("POST /api/auth/logout");
        authService.logout(requestDTO);
        return ResponseEntity.noContent().build();
    }
}
