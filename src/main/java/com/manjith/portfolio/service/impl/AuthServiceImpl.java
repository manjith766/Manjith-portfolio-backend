package com.manjith.portfolio.service.impl;

import com.manjith.portfolio.dto.request.LoginRequestDTO;
import com.manjith.portfolio.dto.request.RefreshTokenRequestDTO;
import com.manjith.portfolio.dto.response.AuthResponseDTO;
import com.manjith.portfolio.entity.RefreshToken;
import com.manjith.portfolio.entity.User;
import com.manjith.portfolio.exception.ErrorCode;
import com.manjith.portfolio.exception.InvalidRefreshTokenException;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.repository.RefreshTokenRepository;
import com.manjith.portfolio.repository.UserRepository;
import com.manjith.portfolio.security.JwtService;
import com.manjith.portfolio.security.UserPrincipal;
import com.manjith.portfolio.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;

/**
 * QUERY / TOKEN STRATEGY:
 *  - login: 1 authentication lookup (via CustomUserDetailsService, with
 *    @EntityGraph(role) so no second query for the role) + 1 SELECT to
 *    reload the full User entity for the RefreshToken FK + 1 INSERT for
 *    the new refresh token row. Access token itself is stateless — no DB
 *    write per access-token issuance.
 *  - refresh: 1 SELECT by token + 1 UPDATE (revoke old) + 1 INSERT (new
 *    token) — rotation on every refresh so a stolen refresh token has a
 *    single-use window before its owner's next legitimate refresh call
 *    invalidates it implicitly (both requests target the same row).
 *  - logout: 1 UPDATE (revoke), idempotent no-op if the token is already
 *    gone — logout should never leak whether a token existed.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Override
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        log.info("API entry: login usernameOrEmail={}", requestDTO.getUsernameOrEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getUsernameOrEmail(), requestDTO.getPassword()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        log.debug("Authenticated userId={} role={}", principal.getId(), principal.getRoleName());

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND,
                        "Authenticated user id " + principal.getId() + " no longer exists"));

        String accessToken = jwtService.generateAccessToken(principal);
        String refreshTokenValue = issueRefreshToken(user);

        log.info("API exit: login succeeded for userId={}", user.getId());
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .expiresInSeconds(jwtService.getAccessTokenExpirationSeconds())
                .username(user.getUsername())
                .role(user.getRole().getName())
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDTO refresh(RefreshTokenRequestDTO requestDTO) {
        log.info("API entry: refresh token rotation requested");

        RefreshToken existing = refreshTokenRepository.findByToken(requestDTO.getRefreshToken())
                .orElseThrow(() -> {
                    log.warn("Refresh token not found");
                    return new InvalidRefreshTokenException(ErrorCode.REFRESH_TOKEN_NOT_FOUND,
                            "Refresh token not found");
                });

        if (Boolean.TRUE.equals(existing.getRevoked())) {
            log.warn("Refresh token already revoked, userId={}", existing.getUser().getId());
            throw new InvalidRefreshTokenException(ErrorCode.REFRESH_TOKEN_REVOKED, "Refresh token has been revoked");
        }

        if (existing.isExpired()) {
            log.warn("Refresh token expired, userId={}", existing.getUser().getId());
            throw new InvalidRefreshTokenException(ErrorCode.REFRESH_TOKEN_EXPIRED, "Refresh token has expired");
        }

        User user = existing.getUser();
        existing.setRevoked(true); // rotation: old token is single-use

        UserPrincipal principal = new UserPrincipal(user);
        String newAccessToken = jwtService.generateAccessToken(principal);
        String newRefreshToken = issueRefreshToken(user);

        log.info("API exit: refresh rotated token for userId={}", user.getId());
        return AuthResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresInSeconds(jwtService.getAccessTokenExpirationSeconds())
                .username(user.getUsername())
                .role(user.getRole().getName())
                .build();
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequestDTO requestDTO) {
        log.info("API entry: logout");
        int revokedRows = refreshTokenRepository.revokeByToken(requestDTO.getRefreshToken());
        log.debug("Logout revoked {} token row(s)", revokedRows);
        log.info("API exit: logout complete");
    }

    private String issueRefreshToken(User user) {
        byte[] randomBytes = new byte[64];
        SECURE_RANDOM.nextBytes(randomBytes);
        String tokenValue = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenValue)
                .expiryDate(OffsetDateTime.now().plus(java.time.Duration.ofMillis(refreshTokenExpirationMs)))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }
}
