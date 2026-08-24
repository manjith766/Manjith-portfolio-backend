package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findByIsActiveTrue();

    List<Resume> findAllByOrderByVersionDesc();

    /**
     * Deliberately a bulk UPDATE rather than load-all-then-save-each: the
     * partial unique index (uq_resume_single_active) requires the old
     * active row to be deactivated in the SAME transaction as the new
     * row's insert, and doing it as one statement removes any window
     * where two rows could momentarily both be active under concurrent
     * requests.
     */
    @Modifying
    @Query("UPDATE Resume r SET r.isActive = false WHERE r.isActive = true")
    int deactivateAllActive();
}
