package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Message> findAllByIsReadOrderByCreatedAtDesc(boolean isRead, Pageable pageable);

    long countByIsReadFalse();

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.id = :id")
    int markAsReadById(@Param("id") Long id);
}
