package io.dattq.loanplatform.application.service;

import io.dattq.loanplatform.application.api.LoanProductView;
import io.dattq.loanplatform.application.domain.LoanProduct;
import io.dattq.loanplatform.application.repository.LoanProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LoanProductService {
    private final LoanProductRepository productRepository;

    public LoanProductService(LoanProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<LoanProductView> getActiveProducts() {
        return productRepository.findAll().stream()
                .filter(LoanProduct::isActive)
                .map(this::toView)
                .toList();
    }

    @Transactional(readOnly = true)
    public LoanProductView getActiveProduct(String productCode) {
        LoanProduct product = productRepository.findByProductCode(productCode)
                .filter(LoanProduct::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan product not found"));
        return toView(product);
    }

    private LoanProductView toView(LoanProduct product) {
        return new LoanProductView(
                product.getProductCode(),
                product.getName(),
                product.getMinAmount(),
                product.getMaxAmount(),
                product.getMinTenorMonths(),
                product.getMaxTenorMonths()
        );
    }
}
