package com.manjith.portfolio.scheduler;

import com.manjith.portfolio.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Runs once daily, shortly after midnight UTC, and rolls up the PREVIOUS
 * calendar day's visitor_logs into a single analytics_daily row. Uses a
 * cron expression (not fixedRate) because this needs to fire at a
 * specific wall-clock time, not on an interval from application startup.
 *
 * Rolling up "yesterday" rather than "today" avoids ever summarizing a
 * partial day — by the time this runs, yesterday's data is complete.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsRollupScheduler {

    private final AnalyticsService analyticsService;

    @Scheduled(cron = "0 5 0 * * *", zone = "UTC")
    public void rollUpYesterday() {
        LocalDate yesterday = LocalDate.now(java.time.ZoneOffset.UTC).minusDays(1);
        log.info("Scheduled job started: analytics rollup for date={}", yesterday);
        try {
            analyticsService.runDailyRollup(yesterday);
            log.info("Scheduled job finished: analytics rollup for date={}", yesterday);
        } catch (Exception e) {
            // A failed rollup should not crash the scheduler thread or
            // prevent tomorrow's run — logged at ERROR for follow-up;
            // runDailyRollup(date) can be re-triggered manually (it's a
            // public service method, not scheduler-only logic) once the
            // underlying issue is fixed.
            log.error("Scheduled analytics rollup failed for date={}", yesterday, e);
        }
    }
}
