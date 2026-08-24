package com.manjith.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Maps 1:1 to table: public.social_links
 * Constraint: uq_social_links_platform UNIQUE (platform)
 */
@Entity
@Table(
        schema = "public",
        name = "social_links",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_social_links_platform", columnNames = "platform")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class SocialLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "platform", nullable = false, length = 50)
    private String platform;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "icon", length = 100)
    private String icon;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
}
