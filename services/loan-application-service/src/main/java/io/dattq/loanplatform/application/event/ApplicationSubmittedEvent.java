package io.dattq.loanplatform.application.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

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
