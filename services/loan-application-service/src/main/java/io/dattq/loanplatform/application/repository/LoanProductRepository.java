package io.dattq.loanplatform.application.repository;

import io.dattq.loanplatform.application.domain.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoanProductRepository extends JpaRepository<LoanProduct, UUID> {
    Optional<LoanProduct> findByProductCode(String productCode);
}
