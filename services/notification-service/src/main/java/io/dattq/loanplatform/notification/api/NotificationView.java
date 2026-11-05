package io.dattq.loanplatform.notification.api;

import java.time.Instant;
import java.util.UUID;

/**
 * Bản ghi thông báo được trả về cho client để theo dõi lịch sử gửi.
 */
public record NotificationView(
        UUID notificationId,
        UUID applicationId,
        String customerId,
        String type,
        String message,
        Instant createdAt
) {
}
