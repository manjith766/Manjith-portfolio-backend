package com.manjith.portfolio.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manjith.portfolio.entity.AnalyticsDaily;
import com.manjith.portfolio.mapper.VisitorLogMapper;
import com.manjith.portfolio.repository.AnalyticsDailyRepository;
import com.manjith.portfolio.repository.VisitorLogRepository;
import com.manjith.portfolio.service.impl.AnalyticsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private VisitorLogRepository visitorLogRepository;
    @Mock
    private AnalyticsDailyRepository analyticsDailyRepository;
    @Mock
    private VisitorLogMapper visitorLogMapper;

    private AnalyticsServiceImpl analyticsService;

    private AnalyticsServiceImpl newService() {
        return new AnalyticsServiceImpl(visitorLogRepository, analyticsDailyRepository, visitorLogMapper, new ObjectMapper());
    }

    @Test
    void runDailyRollup_aggregatesPageViewsIntoJson_forNewDate() {
        analyticsService = newService();
        LocalDate targetDate = LocalDate.of(2026, 8, 15);

        when(visitorLogRepository.countByVisitedAtBetween(any(), any())).thenReturn(42L);
        when(visitorLogRepository.countDistinctIpAddressBetween(any(), any())).thenReturn(30L);
        when(visitorLogRepository.countByPagePathBetween(any(), any()))
                .thenReturn(List.of(
                        new Object[]{"/projects", 20L},
                        new Object[]{"/about", 15L},
                        new Object[]{"/", 7L}
                ));
        when(analyticsDailyRepository.findByMetricDate(targetDate)).thenReturn(Optional.empty());
        when(analyticsDailyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        analyticsService.runDailyRollup(targetDate);

        ArgumentCaptor<AnalyticsDaily> captor = ArgumentCaptor.forClass(AnalyticsDaily.class);
        verify(analyticsDailyRepository).save(captor.capture());

        AnalyticsDaily saved = captor.getValue();
        assertThat(saved.getMetricDate()).isEqualTo(targetDate);
        assertThat(saved.getTotalVisits()).isEqualTo(42L);
        assertThat(saved.getUniqueVisitors()).isEqualTo(30L);
        assertThat(saved.getPageViewsJson()).contains("\"/projects\":20").contains("\"/about\":15").contains("\"/\":7");
    }

    @Test
    void runDailyRollup_updatesExistingRow_whenDateAlreadyRolledUp() {
        analyticsService = newService();
        LocalDate targetDate = LocalDate.of(2026, 8, 14);
        AnalyticsDaily existing = AnalyticsDaily.builder()
                .id(5L).metricDate(targetDate).totalVisits(10L).uniqueVisitors(8L)
                .createdAt(OffsetDateTime.now().minusDays(1))
                .build();

        when(visitorLogRepository.countByVisitedAtBetween(any(), any())).thenReturn(99L);
        when(visitorLogRepository.countDistinctIpAddressBetween(any(), any())).thenReturn(70L);
        when(visitorLogRepository.countByPagePathBetween(any(), any())).thenReturn(List.of());
        when(analyticsDailyRepository.findByMetricDate(targetDate)).thenReturn(Optional.of(existing));
        when(analyticsDailyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        analyticsService.runDailyRollup(targetDate);

        ArgumentCaptor<AnalyticsDaily> captor = ArgumentCaptor.forClass(AnalyticsDaily.class);
        verify(analyticsDailyRepository).save(captor.capture());

        AnalyticsDaily saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(5L); // same row updated, not a new one
        assertThat(saved.getTotalVisits()).isEqualTo(99L);
        assertThat(saved.getUniqueVisitors()).isEqualTo(70L);
    }

    @Test
    void getSummary_returnsZeroCounts_whenNoVisitorLogsExist() {
        analyticsService = newService();

        when(visitorLogRepository.count()).thenReturn(0L);
        when(visitorLogRepository.countByVisitedAtBetween(any(), any())).thenReturn(0L);
        when(visitorLogRepository.countDistinctIpAddressBetween(any(), any())).thenReturn(0L);

        var summary = analyticsService.getSummary();

        assertThat(summary.getTotalVisitsAllTime()).isZero();
        assertThat(summary.getTodayVisits()).isZero();
        assertThat(summary.getTodayUniqueVisitors()).isZero();
    }
}
