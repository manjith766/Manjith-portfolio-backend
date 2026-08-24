package com.manjith.portfolio.controller.admin;

import com.manjith.portfolio.constants.AppConstants;
import com.manjith.portfolio.dto.response.AnalyticsDailyResponseDTO;
import com.manjith.portfolio.dto.response.AnalyticsSummaryResponseDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;
import com.manjith.portfolio.dto.response.VisitorLogResponseDTO;
import com.manjith.portfolio.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Analytics", description = "Visitor logs and traffic analytics for the admin dashboard")
public class AdminAnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/api/admin/analytics/visitor-logs")
    @Operation(summary = "List raw visitor log entries, most recent first")
    public ResponseEntity<PageResponseDTO<VisitorLogResponseDTO>> getVisitorLogs(
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_SIZE) int size) {
        log.info("GET /api/admin/analytics/visitor-logs page={}, size={}", page, size);
        PageResponseDTO<VisitorLogResponseDTO> result = analyticsService.getVisitorLogs(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/admin/analytics/daily")
    @Operation(summary = "List daily rolled-up analytics, most recent first")
    public ResponseEntity<PageResponseDTO<AnalyticsDailyResponseDTO>> getDailyAnalytics(
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_SIZE) int size) {
        log.info("GET /api/admin/analytics/daily page={}, size={}", page, size);
        PageResponseDTO<AnalyticsDailyResponseDTO> result = analyticsService.getDailyAnalytics(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/admin/analytics/summary")
    @Operation(summary = "Get quick dashboard totals: all-time visits, today's visits, today's unique visitors")
    public ResponseEntity<AnalyticsSummaryResponseDTO> getSummary() {
        log.info("GET /api/admin/analytics/summary");
        AnalyticsSummaryResponseDTO result = analyticsService.getSummary();
        return ResponseEntity.ok(result);
    }
}
