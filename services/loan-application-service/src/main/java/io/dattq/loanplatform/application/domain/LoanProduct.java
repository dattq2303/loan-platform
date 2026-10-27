package io.dattq.loanplatform.application.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "loan_product")
public class LoanProduct {
    @Id
    @Column(name = "product_id")
    private UUID productId;
    @Column(name = "product_code")
    private String productCode;
    private String name;
    @Column(name = "min_amount")
    private BigDecimal minAmount;
    @Column(name = "max_amount")
    private BigDecimal maxAmount;
    @Column(name = "min_tenor_months")
    private int minTenorMonths;
    @Column(name = "max_tenor_months")
    private int maxTenorMonths;
    private boolean active;

    protected LoanProduct() {
    }

    public UUID getProductId() { return productId; }
    public String getProductCode() { return productCode; }
    public String getName() { return name; }
    public BigDecimal getMinAmount() { return minAmount; }
    public BigDecimal getMaxAmount() { return maxAmount; }
    public int getMinTenorMonths() { return minTenorMonths; }
    public int getMaxTenorMonths() { return maxTenorMonths; }
    public boolean isActive() { return active; }
}
