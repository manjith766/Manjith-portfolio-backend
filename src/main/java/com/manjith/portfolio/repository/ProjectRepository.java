package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * QUERY STRATEGY (see ProjectServiceImpl class-level Javadoc for the full
 * per-endpoint query count breakdown):
 *
 *  - findAll(spec, pageable): re-declared with @EntityGraph(category) so the
 *    ManyToOne join is fetched in the SAME query as the page — a raw
 *    JpaSpecificationExecutor.findAll would otherwise issue one extra
 *    SELECT per row for category (classic N+1). Skills are intentionally
 *    NOT fetch-joined here — see hibernate.default_batch_fetch_size in
 *    application.yml, which turns lazy-collection access into ONE batched
 *    IN-query per page instead of one query per row.
 *
 *  - findBySlug: also fetch-joins category for the same reason. images/
 *    features/skills are loaded lazily inside the same @Transactional
 *    service method — for a SINGLE project this is 3 fixed extra queries
 *    total, not N+1 (N+1 requires the query count to scale with row count;
 *    here it's constant regardless of how many images/features/skills
 *    exist).
 *
 *  - incrementViewCountBySlug: a single atomic UPDATE ... SET view_count =
 *    view_count + 1, not a read-modify-write, so concurrent increments
 *    never lose an update under concurrent requests.
 */
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    @Override
    @EntityGraph(attributePaths = {"category"})
    @NonNull
    Page<Project> findAll(@NonNull org.springframework.data.jpa.domain.Specification<Project> spec, @NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Optional<Project> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    @Modifying
    @Query("UPDATE Project p SET p.viewCount = p.viewCount + 1 WHERE p.slug = :slug")
    int incrementViewCountBySlug(@Param("slug") String slug);
}
