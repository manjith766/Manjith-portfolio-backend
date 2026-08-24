package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findAllByOrderByDisplayOrderAsc();
}
