package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.VisitorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface VisitorLogRepository extends JpaRepository<VisitorLog, Long> {

    Page<VisitorLog> findAllByOrderByVisitedAtDesc(Pageable pageable);

    long countByVisitedAtBetween(OffsetDateTime start, OffsetDateTime end);

    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM VisitorLog v WHERE v.visitedAt BETWEEN :start AND :end")
    long countDistinctIpAddressBetween(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    /**
     * Returns one row per distinct page_path with its visit count for the
     * window — used by the rollup scheduler to build the page_views JSON
     * map. A single GROUP BY query, not a loop of per-page COUNT calls.
     */
    @Query("SELECT v.pagePath, COUNT(v) FROM VisitorLog v WHERE v.visitedAt BETWEEN :start AND :end GROUP BY v.pagePath")
    List<Object[]> countByPagePathBetween(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);
}
