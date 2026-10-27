package io.dattq.loanplatform.application.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LoanApplicationView(
        UUID applicationId,
        String applicationNo,
        String status,
        String productCode,
        BigDecimal requestedAmount,
        int tenorMonths,
        String customerId,
        String submissionChannel,
        Instant submittedAt,
        long version
) {
}
