package io.dattq.loanplatform.workflow.api;

import java.time.Instant;
import java.util.UUID;

/**
 * Dữ liệu chỉ đọc của hồ sơ workflow để quan sát trạng thái xử lý.
 */
public record WorkflowCaseView(
        UUID applicationId,
        String applicationNo,
        String customerId,
        String status,
        Instant createdAt
) {
}
