package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.SocialLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SocialLinkRepository extends JpaRepository<SocialLink, Long> {

    List<SocialLink> findAllByOrderByDisplayOrderAsc();

    boolean existsByPlatform(String platform);

    boolean existsByPlatformAndIdNot(String platform, Long id);
}
