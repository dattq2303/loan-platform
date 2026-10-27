package io.dattq.loanplatform.workflow.repository;

import io.dattq.loanplatform.workflow.domain.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {
    @Modifying
    @Query(value = "INSERT INTO processed_event(event_id) VALUES (:eventId) ON CONFLICT DO NOTHING", nativeQuery = true)
    int recordIfAbsent(@Param("eventId") UUID eventId);
}
