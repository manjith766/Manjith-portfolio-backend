package com.manjith.portfolio.scheduler;

import com.manjith.portfolio.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

/**
 * Keeps refresh_tokens from growing unbounded. Runs once every 24h; a
 * fixed-rate schedule (rather than fixed-delay) is fine here since the
 * job is a single bulk DELETE, not something that risks overlapping runs
 * under normal load.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000L)
    @Transactional
    public void purgeExpiredOrRevokedTokens() {
        log.info("Scheduled job started: purge expired/revoked refresh tokens");
        int deleted = refreshTokenRepository.deleteAllExpiredOrRevokedBefore(OffsetDateTime.now());
        log.info("Scheduled job finished: purged {} refresh token row(s)", deleted);
    }
}
