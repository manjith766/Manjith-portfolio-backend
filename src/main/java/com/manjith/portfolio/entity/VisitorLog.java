package com.manjith.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Maps 1:1 to table: public.visitor_logs
 * Columns: id, ip_address, user_agent, page_path, referrer, country, city,
 *          visited_at
 *
 * country/city are populated ONLY if a GeoIP lookup is wired in — this
 * codebase does not include one (no MaxMind DB, no third-party geo API
 * call). Both columns are simply left null by AnalyticsServiceImpl. This
 * is a deliberate scope decision, not a bug: fabricating fake geo data
 * would be worse than admitting it's unimplemented.
 */
@Entity
@Table(schema = "public", name = "visitor_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class VisitorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "page_path", nullable = false, length = 255)
    private String pagePath;

    @Column(name = "referrer", length = 500)
    private String referrer;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "visited_at", nullable = false)
    private OffsetDateTime visitedAt;

    @PrePersist
    protected void onCreate() {
        this.visitedAt = OffsetDateTime.now();
    }
}
