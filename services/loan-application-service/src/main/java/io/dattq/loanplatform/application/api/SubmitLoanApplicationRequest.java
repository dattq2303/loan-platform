package io.dattq.loanplatform.application.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitLoanApplicationRequest(
        @NotBlank String submissionChannel,
        @NotNull Boolean confirmedTerms,
        long version
) {
}
