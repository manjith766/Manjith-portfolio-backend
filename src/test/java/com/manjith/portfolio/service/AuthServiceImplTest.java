package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.RefreshTokenRequestDTO;
import com.manjith.portfolio.entity.RefreshToken;
import com.manjith.portfolio.entity.Role;
import com.manjith.portfolio.entity.User;
import com.manjith.portfolio.exception.InvalidRefreshTokenException;
import com.manjith.portfolio.repository.RefreshTokenRepository;
import com.manjith.portfolio.repository.UserRepository;
import com.manjith.portfolio.security.JwtService;
import com.manjith.portfolio.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtService jwtService;

    private AuthServiceImpl authService;

    private AuthServiceImpl newService() {
        AuthServiceImpl service = new AuthServiceImpl(authenticationManager, userRepository, refreshTokenRepository, jwtService);
        ReflectionTestUtils.setField(service, "refreshTokenExpirationMs", 604800000L);
        return service;
    }

    @Test
    void refresh_throwsInvalidRefreshTokenException_whenTokenNotFound() {
        authService = newService();
        when(refreshTokenRepository.findByToken("unknown-token")).thenReturn(Optional.empty());

        RefreshTokenRequestDTO request = RefreshTokenRequestDTO.builder().refreshToken("unknown-token").build();

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void refresh_throwsInvalidRefreshTokenException_whenTokenRevoked() {
        authService = newService();
        Role role = Role.builder().id(1L).name("ADMIN").build();
        User user = User.builder().id(1L).username("admin").email("admin@test.com").role(role).enabled(true).build();
        RefreshToken revokedToken = RefreshToken.builder()
                .id(1L).user(user).token("revoked-token")
                .expiryDate(OffsetDateTime.now().plusDays(1))
                .revoked(true)
                .build();

        when(refreshTokenRepository.findByToken("revoked-token")).thenReturn(Optional.of(revokedToken));

        RefreshTokenRequestDTO request = RefreshTokenRequestDTO.builder().refreshToken("revoked-token").build();

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void refresh_throwsInvalidRefreshTokenException_whenTokenExpired() {
        authService = newService();
        Role role = Role.builder().id(1L).name("ADMIN").build();
        User user = User.builder().id(1L).username("admin").email("admin@test.com").role(role).enabled(true).build();
        RefreshToken expiredToken = RefreshToken.builder()
                .id(1L).user(user).token("expired-token")
                .expiryDate(OffsetDateTime.now().minusDays(1))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));

        RefreshTokenRequestDTO request = RefreshTokenRequestDTO.builder().refreshToken("expired-token").build();

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }
}
