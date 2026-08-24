package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    List<Certification> findAllByOrderByDisplayOrderAsc();
}
