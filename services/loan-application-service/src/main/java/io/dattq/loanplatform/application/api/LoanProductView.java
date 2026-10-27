package io.dattq.loanplatform.application.api;

import java.math.BigDecimal;

public record LoanProductView(
        String productCode,
        String name,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        int minTenorMonths,
        int maxTenorMonths
) {
}
