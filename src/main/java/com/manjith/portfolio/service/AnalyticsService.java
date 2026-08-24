package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.response.AnalyticsDailyResponseDTO;
import com.manjith.portfolio.dto.response.AnalyticsSummaryResponseDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;
import com.manjith.portfolio.dto.response.VisitorLogResponseDTO;

import java.time.LocalDate;

public interface AnalyticsService {

    void trackVisit(String pagePath, String referrer, String ipAddress, String userAgent);

    PageResponseDTO<VisitorLogResponseDTO> getVisitorLogs(int page, int size);

    PageResponseDTO<AnalyticsDailyResponseDTO> getDailyAnalytics(int page, int size);

    AnalyticsSummaryResponseDTO getSummary();

    /**
     * Computes totals for the given calendar date from visitor_logs and
     * upserts the corresponding analytics_daily row. Called by
     * AnalyticsRollupScheduler for "yesterday" on its daily schedule, but
     * exposed as a public method (rather than private scheduler logic) so
     * it can also be triggered manually for backfill or testing.
     */
    void runDailyRollup(LocalDate date);
}
