package com.manjith.portfolio.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Maps 1:1 to table: public.blogs
 * Columns: id, title, slug, content_markdown, excerpt, cover_image_url,
 *          category_id, author_id, status, views, likes,
 *          reading_time_minutes, published_at, created_at, updated_at
 * Constraints: uq_blogs_slug UNIQUE (slug)
 *              chk_blogs_status CHECK (status IN ('DRAFT','PUBLISHED','ARCHIVED'))
 *              chk_blogs_views CHECK (views >= 0)
 *              chk_blogs_likes CHECK (likes >= 0)
 */
@Entity
@Table(
        schema = "public",
        name = "blogs",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_blogs_slug", columnNames = "slug")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"tags", "comments"})
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "slug", nullable = false, length = 280)
    private String slug;

    @Column(name = "content_markdown", nullable = false, columnDefinition = "TEXT")
    private String contentMarkdown;

    @Column(name = "excerpt", length = 500)
    private String excerpt;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_blogs_category"))
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_blogs_author"))
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private BlogStatus status = BlogStatus.DRAFT;

    @Column(name = "views", nullable = false)
    @Builder.Default
    private Long views = 0L;

    @Column(name = "likes", nullable = false)
    @Builder.Default
    private Long likes = 0L;

    @Column(name = "reading_time_minutes", nullable = false)
    @Builder.Default
    private Integer readingTimeMinutes = 0;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            schema = "public",
            name = "blog_tag_map",
            joinColumns = @JoinColumn(name = "blog_id", foreignKey = @ForeignKey(name = "fk_blog_tag_map_blog")),
            inverseJoinColumns = @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "fk_blog_tag_map_tag"))
    )
    @Builder.Default
    private Set<BlogTag> tags = new LinkedHashSet<>();

    // Comments are appended one at a time via the public submission
    // endpoint, never bulk-replaced like Project features/images — so
    // NO orphanRemoval here. Deleting a single comment goes through
    // BlogCommentRepository directly, not through this collection.
    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BlogComment> comments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) this.status = BlogStatus.DRAFT;
        if (this.views == null) this.views = 0L;
        if (this.likes == null) this.likes = 0L;
        if (this.readingTimeMinutes == null) this.readingTimeMinutes = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public void replaceTags(Set<BlogTag> newTags) {
        this.tags.clear();
        this.tags.addAll(newTags);
    }
}
