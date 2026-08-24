package com.manjith.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Maps 1:1 to table: public.analytics_daily
 * Constraint: uq_analytics_daily_metric_date UNIQUE (metric_date)
 *
 * page_views is a jsonb column, mapped as a raw JSON String
 * (pageViewsJson) rather than a native Map<String,Long> — the Map<->JSON
 * conversion happens explicitly via Jackson in AnalyticsServiceImpl,
 * where it's visible and testable, rather than relying on Hibernate to
 * marshal a Map automatically. @JdbcTypeCode(SqlTypes.JSON) is still
 * required even with a String target: without it, Hibernate binds the
 * value as JDBC VARCHAR and PostgreSQL rejects the write with "column is
 * of type jsonb but expression is of type character varying" — the type
 * code is what tells the driver to send it as JSON, not a stylistic
 * choice.
 */
@Entity
@Table(
        schema = "public",
        name = "analytics_daily",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_analytics_daily_metric_date", columnNames = "metric_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AnalyticsDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "total_visits", nullable = false)
    @Builder.Default
    private Long totalVisits = 0L;

    @Column(name = "unique_visitors", nullable = false)
    @Builder.Default
    private Long uniqueVisitors = 0L;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "page_views", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private String pageViewsJson = "{}";

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        if (this.totalVisits == null) this.totalVisits = 0L;
        if (this.uniqueVisitors == null) this.uniqueVisitors = 0L;
        if (this.pageViewsJson == null) this.pageViewsJson = "{}";
    }
}
