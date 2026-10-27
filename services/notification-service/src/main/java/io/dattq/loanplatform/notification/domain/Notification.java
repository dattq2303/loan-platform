package io.dattq.loanplatform.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @Column(name = "notification_id")
    private UUID notificationId;
    @Column(name = "application_id")
    private UUID applicationId;
    @Column(name = "customer_id")
    private String customerId;
    @Column(name = "notification_type")
    private String notificationType;
    private String message;
    @Column(name = "created_at")
    private Instant createdAt;

    protected Notification() {
    }

    public Notification(UUID applicationId, String customerId, String notificationType, String message) {
        this.notificationId = UUID.randomUUID();
        this.applicationId = applicationId;
        this.customerId = customerId;
        this.notificationType = notificationType;
        this.message = message;
        this.createdAt = Instant.now();
    }

    public UUID getNotificationId() { return notificationId; }
    public UUID getApplicationId() { return applicationId; }
    public String getCustomerId() { return customerId; }
    public String getNotificationType() { return notificationType; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
}
