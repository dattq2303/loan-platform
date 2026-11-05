package io.dattq.loanplatform.application.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Sự kiện phát sau khi hồ sơ chuyển từ DRAFT sang SUBMITTED.
 */
public record ApplicationSubmittedEvent(
        UUID eventId,
        String eventType,
        UUID applicationId,
        String applicationNo,
        String customerId,
        String productCode,
        BigDecimal requestedAmount,
        int tenorMonths,
        Instant occurredAt
) {
}
