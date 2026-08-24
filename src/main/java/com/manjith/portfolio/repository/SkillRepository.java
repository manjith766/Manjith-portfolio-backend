package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.Skill;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findAllByIdIn(Collection<Long> ids);

    @EntityGraph(attributePaths = "category")
    List<Skill> findAllByOrderByDisplayOrderAsc();

    @EntityGraph(attributePaths = "category")
    List<Skill> findAllByCategory_SlugOrderByDisplayOrderAsc(String categorySlug);

    @EntityGraph(attributePaths = "category")
    Optional<Skill> findById(Long id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
