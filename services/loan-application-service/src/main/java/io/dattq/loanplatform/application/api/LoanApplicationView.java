package io.dattq.loanplatform.application.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Thông tin hồ sơ vay an toàn để trả về cho chủ sở hữu hiện tại.
 */
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
