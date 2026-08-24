package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findAllByOrderByDisplayOrderAsc();
}
