package io.dattq.loanplatform.application.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ApplicantRequest(
        @NotBlank String customerId,
        @NotBlank String fullName,
        @NotNull LocalDate dateOfBirth,
        @NotNull @DecimalMin("0.01") BigDecimal monthlyIncome,
        @NotBlank String employmentType
) {
}
