package io.dattq.loanplatform.notification.api;

import io.dattq.loanplatform.notification.domain.Notification;
import io.dattq.loanplatform.notification.repository.NotificationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationRepository repository;

    public NotificationController(NotificationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<NotificationView> list(@RequestParam String customerId) {
        return repository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(notification -> new NotificationView(notification.getNotificationId(), notification.getApplicationId(),
                        notification.getCustomerId(), notification.getNotificationType(), notification.getMessage(),
                        notification.getCreatedAt()))
                .toList();
    }

    public record NotificationView(UUID notificationId, UUID applicationId, String customerId,
                                   String type, String message, Instant createdAt) { }
}
