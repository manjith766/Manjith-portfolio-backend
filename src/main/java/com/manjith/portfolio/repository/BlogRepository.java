package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * Same query-strategy pattern as ProjectRepository: @EntityGraph on the
 * ManyToOne joins (category, author) to avoid N+1 on list/detail, tags
 * left to lazy + Hibernate batch-fetching (hibernate.default_batch_fetch_size),
 * and atomic single-column UPDATEs for views/likes instead of
 * read-modify-write.
 */
public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {

    @Override
    @EntityGraph(attributePaths = {"category", "author"})
    @NonNull
    Page<Blog> findAll(@NonNull Specification<Blog> spec, @NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"category", "author"})
    Optional<Blog> findBySlug(String slug);

    @Override
    @EntityGraph(attributePaths = {"category", "author"})
    @NonNull
    Optional<Blog> findById(@NonNull Long id);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    @Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Blog b SET b.views = b.views + 1 WHERE b.slug = :slug")
    int incrementViewsBySlug(@Param("slug") String slug);

    @Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Blog b SET b.likes = b.likes + 1 WHERE b.slug = :slug")
    int incrementLikesBySlug(@Param("slug") String slug);
}
