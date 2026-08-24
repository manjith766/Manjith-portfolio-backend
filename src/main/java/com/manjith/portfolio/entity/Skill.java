package com.manjith.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Maps 1:1 to table: public.skills
 * Columns: id, name, category_id, proficiency_pct, years_experience, icon_url,
 *          display_order, created_at, updated_at
 * Constraints: uq_skills_name UNIQUE (name)
 *              chk_skills_proficiency CHECK (proficiency_pct BETWEEN 0 AND 100)
 *              chk_skills_years CHECK (years_experience >= 0)
 *
 * NOTE: The full Skills CRUD module (service/controller/DTOs) is built on
 * top of this same entity — see SkillService, SkillController,
 * AdminSkillController. The inverse `projects` collection below exists so
 * the "Projects used" display requirement per skill can be served without
 * a separate query per skill (see SkillServiceImpl query-strategy Javadoc).
 */
@Entity
@Table(
        schema = "public",
        name = "skills",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_skills_name", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_skills_category"))
    private Category category;

    @Column(name = "proficiency_pct", nullable = false)
    private Short proficiencyPct;

    @Column(name = "years_experience", nullable = false, precision = 4, scale = 1)
    private BigDecimal yearsExperience;

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Project> projects = new LinkedHashSet<>();

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
