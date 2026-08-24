package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.BlogTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogTagRepository extends JpaRepository<BlogTag, Long> {

    Optional<BlogTag> findBySlug(String slug);
}
