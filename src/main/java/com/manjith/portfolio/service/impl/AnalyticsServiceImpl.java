package com.manjith.portfolio.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manjith.portfolio.constants.AppConstants;
import com.manjith.portfolio.dto.response.AnalyticsDailyResponseDTO;
import com.manjith.portfolio.dto.response.AnalyticsSummaryResponseDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;
import com.manjith.portfolio.dto.response.VisitorLogResponseDTO;
import com.manjith.portfolio.entity.AnalyticsDaily;
import com.manjith.portfolio.entity.VisitorLog;
import com.manjith.portfolio.mapper.VisitorLogMapper;
import com.manjith.portfolio.repository.AnalyticsDailyRepository;
import com.manjith.portfolio.repository.VisitorLogRepository;
import com.manjith.portfolio.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * QUERY COUNT PER ENDPOINT:
 *   trackVisit           -> 1 INSERT. No read-before-write.
 *   getVisitorLogs        -> 1 query (flat entity, no relations).
 *   getDailyAnalytics      -> 1 query (flat entity, no relations; JSON
 *                             parsing happens in-memory, not another query).
 *   getSummary            -> 3 queries: all-time count, today's count,
 *                             today's distinct-IP count. Fixed regardless
 *                             of how much data exists.
 *   runDailyRollup        -> 4 queries: total count, distinct-IP count,
 *                             per-page GROUP BY, then 1 SELECT (find
 *                             existing row for upsert) + 1 INSERT/UPDATE.
 *                             The per-page breakdown is ONE grouped query,
 *                             never a loop of per-page COUNT calls.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final VisitorLogRepository visitorLogRepository;
    private final AnalyticsDailyRepository analyticsDailyRepository;
    private final VisitorLogMapper visitorLogMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void trackVisit(String pagePath, String referrer, String ipAddress, String userAgent) {
        log.info("API entry: trackVisit pagePath={}", pagePath);

        VisitorLog visitorLog = VisitorLog.builder()
                .pagePath(pagePath)
                .referrer(referrer)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        visitorLogRepository.save(visitorLog);
        log.debug("Recorded visit pagePath={}, ipAddress={}", pagePath, ipAddress);

        log.info("API exit: trackVisit recorded pagePath={}", pagePath);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<VisitorLogResponseDTO> getVisitorLogs(int page, int size) {
        log.info("API entry: getVisitorLogs page={}, size={}", page, size);

        Pageable pageable = buildPageable(page, size);
        Page<VisitorLog> logPage = visitorLogRepository.findAllByOrderByVisitedAtDesc(pageable);
        Page<VisitorLogResponseDTO> dtoPage = logPage.map(visitorLogMapper::toResponseDTO);

        log.info("API exit: getVisitorLogs returning {} of {} total", dtoPage.getNumberOfElements(), dtoPage.getTotalElements());
        return PageResponseDTO.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<AnalyticsDailyResponseDTO> getDailyAnalytics(int page, int size) {
        log.info("API entry: getDailyAnalytics page={}, size={}", page, size);

        Pageable pageable = buildPageable(page, size);
        Page<AnalyticsDaily> dailyPage = analyticsDailyRepository.findAllByOrderByMetricDateDesc(pageable);
        Page<AnalyticsDailyResponseDTO> dtoPage = dailyPage.map(this::toDailyResponseDTO);

        log.info("API exit: getDailyAnalytics returning {} of {} total", dtoPage.getNumberOfElements(), dtoPage.getTotalElements());
        return PageResponseDTO.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsSummaryResponseDTO getSummary() {
        log.info("API entry: getSummary");

        long totalVisitsAllTime = visitorLogRepository.count();

        LocalDate today = LocalDate.now();
        OffsetDateTime startOfToday = OffsetDateTime.of(today, LocalTime.MIDNIGHT, ZoneOffset.UTC);
        OffsetDateTime startOfTomorrow = startOfToday.plusDays(1);

        long todayVisits = visitorLogRepository.countByVisitedAtBetween(startOfToday, startOfTomorrow);
        long todayUniqueVisitors = visitorLogRepository.countDistinctIpAddressBetween(startOfToday, startOfTomorrow);

        log.info("API exit: getSummary totalVisitsAllTime={}, todayVisits={}", totalVisitsAllTime, todayVisits);
        return AnalyticsSummaryResponseDTO.builder()
                .totalVisitsAllTime(totalVisitsAllTime)
                .todayVisits(todayVisits)
                .todayUniqueVisitors(todayUniqueVisitors)
                .build();
    }

    @Override
    @Transactional
    public void runDailyRollup(LocalDate date) {
        log.info("Rollup started for date={}", date);

        OffsetDateTime start = OffsetDateTime.of(date, LocalTime.MIDNIGHT, ZoneOffset.UTC);
        OffsetDateTime end = start.plusDays(1);

        long totalVisits = visitorLogRepository.countByVisitedAtBetween(start, end);
        long uniqueVisitors = visitorLogRepository.countDistinctIpAddressBetween(start, end);

        Map<String, Long> pageViews = new LinkedHashMap<>();
        for (Object[] row : visitorLogRepository.countByPagePathBetween(start, end)) {
            pageViews.put((String) row[0], (Long) row[1]);
        }

        String pageViewsJson = serializePageViews(pageViews, date);

        AnalyticsDaily entry = analyticsDailyRepository.findByMetricDate(date)
                .orElseGet(() -> AnalyticsDaily.builder().metricDate(date).build());
        entry.setTotalVisits(totalVisits);
        entry.setUniqueVisitors(uniqueVisitors);
        entry.setPageViewsJson(pageViewsJson);

        analyticsDailyRepository.save(entry);
        log.info("Rollup finished for date={}: totalVisits={}, uniqueVisitors={}, distinctPages={}",
                date, totalVisits, uniqueVisitors, pageViews.size());
    }

    private Pageable buildPageable(int page, int size) {
        int safePage = Math.max(page, AppConstants.DEFAULT_PAGE_NUMBER);
        int safeSize = size <= 0 ? AppConstants.DEFAULT_PAGE_SIZE : Math.min(size, AppConstants.MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));
    }

    private AnalyticsDailyResponseDTO toDailyResponseDTO(AnalyticsDaily entity) {
        return AnalyticsDailyResponseDTO.builder()
                .id(entity.getId())
                .metricDate(entity.getMetricDate())
                .totalVisits(entity.getTotalVisits())
                .uniqueVisitors(entity.getUniqueVisitors())
                .pageViews(deserializePageViews(entity.getPageViewsJson(), entity.getMetricDate()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private String serializePageViews(Map<String, Long> pageViews, LocalDate date) {
        try {
            return objectMapper.writeValueAsString(pageViews);
        } catch (Exception e) {
            // Should be unreachable for a Map<String,Long> — Jackson can
            // always serialize this shape. Logged rather than swallowed
            // silently in case something genuinely unexpected occurs.
            log.error("Failed to serialize pageViews for date={} — storing empty map instead", date, e);
            return "{}";
        }
    }

    private Map<String, Long> deserializePageViews(String json, LocalDate date) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Long>>() {
            });
        } catch (Exception e) {
            // A malformed row should not break the whole list response —
            // one bad row degrades to an empty map for that row only.
            log.warn("Failed to parse pageViews JSON for date={} — returning empty map for this row", date, e);
            return Map.of();
        }
    }
}
