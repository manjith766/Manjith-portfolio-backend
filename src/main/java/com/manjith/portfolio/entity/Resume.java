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
 * Maps 1:1 to table: public.resume
 * Columns: id, file_url, file_name, version, is_active, uploaded_at,
 *          cloudinary_public_id (added in V3, nullable — see that
 *          migration's header comment for why)
 * Constraint: uq_resume_single_active — a partial unique index ensures at
 * most one row has is_active = TRUE at any time (enforced at the DB
 * level, not just in application code).
 */
@Entity
@Table(schema = "public", name = "resume")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "version", nullable = false)
    @Builder.Default
    private Integer version = 1;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "uploaded_at", nullable = false)
    private OffsetDateTime uploadedAt;

    @Column(name = "cloudinary_public_id", length = 255)
    private String cloudinaryPublicId;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = OffsetDateTime.now();
        if (this.version == null) this.version = 1;
        if (this.isActive == null) this.isActive = true;
    }
}
