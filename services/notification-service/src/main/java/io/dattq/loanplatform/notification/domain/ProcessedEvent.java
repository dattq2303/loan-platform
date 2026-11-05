package io.dattq.loanplatform.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Dấu vết idempotency: một eventId chỉ được tạo tác động nghiệp vụ một lần.
 */
@Entity
@Table(name = "processed_event")
public class ProcessedEvent {
    @Id
    @Column(name = "event_id")

    // Khóa duy nhất giúp chặn Kafka gửi lại cùng một sự kiện.
    private UUID eventId;

    protected ProcessedEvent() {
    }
}
