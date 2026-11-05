package io.dattq.loanplatform.application.api;

import java.math.BigDecimal;

/**
 * Cấu hình sản phẩm công khai cần thiết để khách hàng lập hồ sơ vay.
 */
public record LoanProductView(
        String productCode,
        String name,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        int minTenorMonths,
        int maxTenorMonths
) {
}
