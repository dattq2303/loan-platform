package io.dattq.loanplatform.application.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Lệnh gửi bản nháp vào quy trình xử lý chính thức.
 */
public record SubmitLoanApplicationRequest(
        @NotBlank String submissionChannel,
        @NotNull Boolean confirmedTerms,
        long version
) {
}
