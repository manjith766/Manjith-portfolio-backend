package com.manjith.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDailyResponseDTO {
    private Long id;
    private LocalDate metricDate;
    private Long totalVisits;
    private Long uniqueVisitors;
    private Map<String, Long> pageViews;
    private OffsetDateTime createdAt;
}
