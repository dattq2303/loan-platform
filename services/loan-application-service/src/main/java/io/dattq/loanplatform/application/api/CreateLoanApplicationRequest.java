package io.dattq.loanplatform.application.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateLoanApplicationRequest(
        @NotBlank String productCode,
        @NotNull @DecimalMin("0.01") BigDecimal requestedAmount,
        @Min(1) int tenorMonths,
        @NotNull @Valid ApplicantRequest applicant
) {
}
