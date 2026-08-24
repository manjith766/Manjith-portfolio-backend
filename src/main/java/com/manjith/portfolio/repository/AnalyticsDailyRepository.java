package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.AnalyticsDaily;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AnalyticsDailyRepository extends JpaRepository<AnalyticsDaily, Long> {

    Page<AnalyticsDaily> findAllByOrderByMetricDateDesc(Pageable pageable);

    Optional<AnalyticsDaily> findByMetricDate(LocalDate metricDate);
}
