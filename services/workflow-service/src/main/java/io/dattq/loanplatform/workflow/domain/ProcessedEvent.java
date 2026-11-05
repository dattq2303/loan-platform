package io.dattq.loanplatform.workflow.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Bản ghi chống xử lý trùng lặp khi Kafka giao lại sự kiện cho workflow.
 */
@Entity
@Table(name = "processed_event")
public class ProcessedEvent {
    @Id
    @Column(name = "event_id")

    private UUID eventId;

    protected ProcessedEvent() {
    }
}
