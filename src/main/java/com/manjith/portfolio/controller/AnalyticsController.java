package com.manjith.portfolio.controller;

import com.manjith.portfolio.dto.request.TrackVisitRequestDTO;
import com.manjith.portfolio.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Public page-visit tracking")
public class AnalyticsController {

    private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";

    private final AnalyticsService analyticsService;

    @PostMapping("/api/analytics/track")
    @Operation(summary = "Record a page visit (called by the frontend on route change)")
    public ResponseEntity<Void> trackVisit(
            @Valid @RequestBody TrackVisitRequestDTO requestDTO,
            HttpServletRequest request) {

        String ipAddress = extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        log.info("POST /api/analytics/track pagePath={}", requestDTO.getPagePath());
        analyticsService.trackVisit(requestDTO.getPagePath(), requestDTO.getReferrer(), ipAddress, userAgent);
        return ResponseEntity.noContent().build();
    }

    /**
     * Behind a load balancer / reverse proxy (Render, Vercel, etc.),
     * request.getRemoteAddr() returns the proxy's IP, not the visitor's —
     * X-Forwarded-For (set by the proxy, not the client) is required to
     * get the real client IP. Takes the first entry, since intermediate
     * proxies append their own IPs to the end of the chain.
     */
    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader(FORWARDED_FOR_HEADER);
        if (StringUtils.hasText(forwardedFor)) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
