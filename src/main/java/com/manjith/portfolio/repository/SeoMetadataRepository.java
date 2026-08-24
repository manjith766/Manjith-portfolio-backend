package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.SeoMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeoMetadataRepository extends JpaRepository<SeoMetadata, Long> {

    Optional<SeoMetadata> findByPagePath(String pagePath);
}
