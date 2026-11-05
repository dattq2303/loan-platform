package io.dattq.loanplatform.notification.service;

import io.dattq.loanplatform.notification.api.NotificationView;
import io.dattq.loanplatform.notification.domain.Notification;
import io.dattq.loanplatform.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationQueryService {
    private final NotificationRepository notificationRepository;

    public NotificationQueryService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationView> getByCustomerId(String customerId) {
        return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(this::toView)
                .toList();
    }

    private NotificationView toView(Notification notification) {
        return new NotificationView(
                notification.getNotificationId(),
                notification.getApplicationId(),
                notification.getCustomerId(),
                notification.getNotificationType(),
                notification.getMessage(),
                notification.getCreatedAt()
        );
    }
}
