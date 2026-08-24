package com.manjith.portfolio.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Maps 1:1 to table: public.projects
 * Columns: id, title, slug, description, short_description, category_id,
 *          github_url, live_demo_url, cover_image_url, is_featured,
 *          display_order, view_count, created_at, updated_at
 * Constraints: uq_projects_slug UNIQUE (slug)
 *              chk_projects_view_count CHECK (view_count >= 0)
 * Relations:
 *   - category_id -> categories.id (ManyToOne, ON DELETE SET NULL)
 *   - project_images.project_id -> projects.id (OneToMany, ON DELETE CASCADE)
 *   - project_features.project_id -> projects.id (OneToMany, ON DELETE CASCADE)
 *   - project_skills (project_id, skill_id) join table (ManyToMany)
 */
@Entity
@Table(
        schema = "public",
        name = "projects",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_projects_slug", columnNames = "slug")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"images", "features", "skills"})
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "slug", nullable = false, length = 220)
    private String slug;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_projects_category"))
    private Category category;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "live_demo_url", length = 500)
    private String liveDemoUrl;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ProjectImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ProjectFeature> features = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            schema = "public",
            name = "project_skills",
            joinColumns = @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "fk_project_skills_project")),
            inverseJoinColumns = @JoinColumn(name = "skill_id", foreignKey = @ForeignKey(name = "fk_project_skills_skill"))
    )
    @Builder.Default
    private Set<Skill> skills = new LinkedHashSet<>();

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.isFeatured == null) this.isFeatured = false;
        if (this.displayOrder == null) this.displayOrder = 0;
        if (this.viewCount == null) this.viewCount = 0L;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // ---- Bidirectional relationship helpers (keep both sides in sync) ----

    public void addImage(ProjectImage image) {
        images.add(image);
        image.setProject(this);
    }

    public void clearImages() {
        images.forEach(img -> img.setProject(null));
        images.clear();
    }

    public void addFeature(ProjectFeature feature) {
        features.add(feature);
        feature.setProject(this);
    }

    public void clearFeatures() {
        features.forEach(f -> f.setProject(null));
        features.clear();
    }

    public void replaceSkills(Set<Skill> newSkills) {
        this.skills.clear();
        this.skills.addAll(newSkills);
    }
}
