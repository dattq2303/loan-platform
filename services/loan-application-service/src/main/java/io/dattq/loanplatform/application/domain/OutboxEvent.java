package io.dattq.loanplatform.application.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * Sự kiện cần phát ra Kafka, được lưu cùng transaction nghiệp vụ để tránh mất sự kiện.
 */
@Entity
@Table(name = "outbox_event")
public class OutboxEvent {
    @Id
    @Column(name = "event_id")

    // ID này phải trùng eventId trong payload để consumer có thể khử trùng lặp.
    private UUID eventId;

    @Column(name = "aggregate_id")

    private UUID aggregateId;

    @Column(name = "event_type")

    private String eventType;


    private String topic;

    @Column(name = "event_key")

    private String eventKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")

    private String payload;

    @Enumerated(EnumType.STRING)

    // PENDING được publisher nhận xử lý; PUBLISHED chỉ được ghi sau khi Kafka xác nhận.
    private OutboxStatus status;

    @Column(name = "attempt_count")

    private int attemptCount;

    @Column(name = "occurred_at")

    private Instant occurredAt;

    @Column(name = "published_at")

    private Instant publishedAt;

    protected OutboxEvent() {
    }

    public OutboxEvent(UUID eventId, UUID aggregateId, String eventType, String topic, String eventKey, String payload) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.topic = topic;
        this.eventKey = eventKey;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
        this.occurredAt = Instant.now();
    }

    public void markPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = Instant.now();
        this.attemptCount++;
    }

    public UUID getEventId() { return eventId; }
    public String getTopic() { return topic; }
    public String getEventKey() { return eventKey; }
    public String getPayload() { return payload; }
}
