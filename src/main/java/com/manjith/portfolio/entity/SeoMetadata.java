package com.manjith.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Maps 1:1 to table: public.seo
 * Constraint: uq_seo_page_path UNIQUE (page_path)
 * Named SeoMetadata (not Seo) to avoid a two-letter class name that reads
 * ambiguously next to the DTO/service/controller of the same concept.
 */
@Entity
@Table(
        schema = "public",
        name = "seo",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_seo_page_path", columnNames = "page_path")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class SeoMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "page_path", nullable = false, length = 255)
    private String pagePath;

    @Column(name = "meta_title", nullable = false, length = 255)
    private String metaTitle;

    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Column(name = "og_image_url", length = 500)
    private String ogImageUrl;

    @Column(name = "canonical_url", length = 500)
    private String canonicalUrl;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.updatedAt = OffsetDateTime.now();
    }
}
