package io.dattq.loanplatform.application.api;

import io.dattq.loanplatform.application.domain.LoanProduct;
import io.dattq.loanplatform.application.repository.LoanProductRepository;
import io.dattq.loanplatform.application.service.LoanApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class LoanApplicationController {
    private final LoanApplicationService applicationService;
    private final LoanProductRepository productRepository;

    public LoanApplicationController(LoanApplicationService applicationService, LoanProductRepository productRepository) {
        this.applicationService = applicationService;
        this.productRepository = productRepository;
    }

    @GetMapping("/loan-products")
    public List<LoanProductView> products() {
        return productRepository.findAll().stream().filter(LoanProduct::isActive).map(this::toProductView).toList();
    }

    @GetMapping("/loan-products/{productCode}")
    public LoanProductView product(@PathVariable String productCode) {
        LoanProduct product = productRepository.findByProductCode(productCode)
                .filter(LoanProduct::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan product not found"));
        return toProductView(product);
    }

    @PostMapping("/loan-applications")
    public ResponseEntity<LoanApplicationView> create(@RequestHeader(value = "X-Actor-Id", required = false) String actorId,
                                                       @Valid @RequestBody CreateLoanApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.createDraft(actor(actorId), request));
    }

    @PutMapping("/loan-applications/{applicationId}")
    public LoanApplicationView update(@PathVariable UUID applicationId,
                                      @RequestHeader(value = "X-Actor-Id", required = false) String actorId,
                                      @Valid @RequestBody UpdateLoanApplicationRequest request) {
        return applicationService.updateDraft(applicationId, actor(actorId), request);
    }

    @GetMapping("/loan-applications/{applicationId}")
    public LoanApplicationView get(@PathVariable UUID applicationId,
                                   @RequestHeader(value = "X-Actor-Id", required = false) String actorId) {
        return applicationService.get(applicationId, actor(actorId));
    }

    @PostMapping("/loan-applications/{applicationId}/submit")
    public LoanApplicationView submit(@PathVariable UUID applicationId,
                                      @RequestHeader(value = "X-Actor-Id", required = false) String actorId,
                                      @Valid @RequestBody SubmitLoanApplicationRequest request) {
        return applicationService.submit(applicationId, actor(actorId), request);
    }

    private String actor(String actorId) {
        if (actorId == null || actorId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "X-Actor-Id is required in local development");
        }
        return actorId;
    }

    private LoanProductView toProductView(LoanProduct product) {
        return new LoanProductView(product.getProductCode(), product.getName(), product.getMinAmount(),
                product.getMaxAmount(), product.getMinTenorMonths(), product.getMaxTenorMonths());
    }
}
