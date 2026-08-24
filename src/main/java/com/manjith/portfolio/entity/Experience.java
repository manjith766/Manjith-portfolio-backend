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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Maps 1:1 to table: public.experience
 * Columns: id, company_name, role, location, start_date, end_date,
 *          is_current, description, display_order, created_at, updated_at
 * Constraint: chk_experience_dates CHECK (end_date IS NULL OR end_date >= start_date)
 */
@Entity
@Table(schema = "public", name = "experience")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"responsibilities", "achievements", "skills"})
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "role", nullable = false, length = 200)
    private String role;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current", nullable = false)
    @Builder.Default
    private Boolean isCurrent = false;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "experience", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ExperienceResponsibility> responsibilities = new ArrayList<>();

    @OneToMany(mappedBy = "experience", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ExperienceAchievement> achievements = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            schema = "public",
            name = "experience_skills",
            joinColumns = @JoinColumn(name = "experience_id", foreignKey = @ForeignKey(name = "fk_experience_skills_experience")),
            inverseJoinColumns = @JoinColumn(name = "skill_id", foreignKey = @ForeignKey(name = "fk_experience_skills_skill"))
    )
    @Builder.Default
    private Set<Skill> skills = new LinkedHashSet<>();

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.isCurrent == null) this.isCurrent = false;
        if (this.displayOrder == null) this.displayOrder = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public void addResponsibility(ExperienceResponsibility responsibility) {
        responsibilities.add(responsibility);
        responsibility.setExperience(this);
    }

    public void clearResponsibilities() {
        responsibilities.forEach(r -> r.setExperience(null));
        responsibilities.clear();
    }

    public void addAchievement(ExperienceAchievement achievement) {
        achievements.add(achievement);
        achievement.setExperience(this);
    }

    public void clearAchievements() {
        achievements.forEach(a -> a.setExperience(null));
        achievements.clear();
    }

    public void replaceSkills(Set<Skill> newSkills) {
        this.skills.clear();
        this.skills.addAll(newSkills);
    }
}
