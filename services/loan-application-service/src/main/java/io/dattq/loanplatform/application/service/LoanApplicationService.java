package io.dattq.loanplatform.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dattq.loanplatform.application.api.CreateLoanApplicationRequest;
import io.dattq.loanplatform.application.api.LoanApplicationView;
import io.dattq.loanplatform.application.api.SubmitLoanApplicationRequest;
import io.dattq.loanplatform.application.api.UpdateLoanApplicationRequest;
import io.dattq.loanplatform.application.domain.LoanApplication;
import io.dattq.loanplatform.application.domain.LoanProduct;
import io.dattq.loanplatform.application.domain.OutboxEvent;
import io.dattq.loanplatform.application.event.ApplicationSubmittedEvent;
import io.dattq.loanplatform.application.repository.LoanApplicationRepository;
import io.dattq.loanplatform.application.repository.LoanProductRepository;
import io.dattq.loanplatform.application.repository.OutboxEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class LoanApplicationService {
    private static final String APPLICATION_SUBMITTED = "ApplicationSubmitted";

    private final LoanApplicationRepository applicationRepository;
    private final LoanProductRepository productRepository;
    private final OutboxEventRepository outboxRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final String applicationEventsTopic;

    public LoanApplicationService(LoanApplicationRepository applicationRepository,
                                  LoanProductRepository productRepository,
                                  OutboxEventRepository outboxRepository,
                                  JdbcTemplate jdbcTemplate,
                                  ObjectMapper objectMapper,
                                  @Value("${platform.kafka.topics.loan-application-events}") String applicationEventsTopic) {
        this.applicationRepository = applicationRepository;
        this.productRepository = productRepository;
        this.outboxRepository = outboxRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.applicationEventsTopic = applicationEventsTopic;
    }

    @Transactional
    public LoanApplicationView createDraft(String actorId, CreateLoanApplicationRequest request) {
        requireOwner(actorId, request.applicant().customerId());
        LoanProduct product = requiredProduct(request.productCode());
        validateAgainstProduct(product, request.requestedAmount(), request.tenorMonths());
        String applicationNo = nextApplicationNo();
        LoanApplication application = new LoanApplication(applicationNo, actorId, product,
                request.requestedAmount(), request.tenorMonths(), request.applicant().customerId(),
                request.applicant().fullName(), request.applicant().dateOfBirth(),
                request.applicant().monthlyIncome(), request.applicant().employmentType());
        return toView(applicationRepository.save(application));
    }

    @Transactional
    public LoanApplicationView updateDraft(UUID applicationId, String actorId, UpdateLoanApplicationRequest request) {
        LoanApplication application = ownedApplication(applicationId, actorId);
        assertVersion(application, request.version());
        LoanProduct product = requiredProduct(request.productCode());
        validateAgainstProduct(product, request.requestedAmount(), request.tenorMonths());
        requireOwner(actorId, request.applicant().customerId());
        application.applyDraftChanges(product, request.requestedAmount(), request.tenorMonths(),
                request.applicant().customerId(), request.applicant().fullName(), request.applicant().dateOfBirth(),
                request.applicant().monthlyIncome(), request.applicant().employmentType());
        return flushAndView(application);
    }

    @Transactional(readOnly = true)
    public LoanApplicationView get(UUID applicationId, String actorId) {
        return toView(ownedApplication(applicationId, actorId));
    }

    @Transactional
    public LoanApplicationView submit(UUID applicationId, String actorId, SubmitLoanApplicationRequest request) {
        LoanApplication application = ownedApplication(applicationId, actorId);
        assertVersion(application, request.version());
        validateAgainstProduct(application.getProduct(), application.getRequestedAmount(), application.getTenorMonths());
        if (!Boolean.TRUE.equals(request.confirmedTerms())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Terms must be confirmed before submission");
        }
        application.submit(request.submissionChannel());
        LoanApplicationView view = flushAndView(application);
        ApplicationSubmittedEvent event = new ApplicationSubmittedEvent(UUID.randomUUID(), APPLICATION_SUBMITTED,
                application.getApplicationId(), application.getApplicationNo(), application.getOwnerCustomerId(),
                application.getProduct().getProductCode(), application.getRequestedAmount(), application.getTenorMonths(),
                application.getSubmittedAt());
        outboxRepository.save(new OutboxEvent(event.eventId(), application.getApplicationId(), APPLICATION_SUBMITTED,
                applicationEventsTopic, application.getApplicationId().toString(), serialize(event)));
        return view;
    }

    public LoanApplicationView toView(LoanApplication application) {
        return new LoanApplicationView(application.getApplicationId(), application.getApplicationNo(),
                application.getApplicationStatus().name(), application.getProduct().getProductCode(),
                application.getRequestedAmount(), application.getTenorMonths(), application.getOwnerCustomerId(),
                application.getSubmissionChannel(), application.getSubmittedAt(), application.getVersion());
    }

    private LoanApplicationView flushAndView(LoanApplication application) {
        try {
            applicationRepository.flush();
            return toView(application);
        } catch (OptimisticLockingFailureException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Application was changed by another request", exception);
        }
    }

    private LoanApplication ownedApplication(UUID applicationId, String actorId) {
        LoanApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        requireOwner(actorId, application.getOwnerCustomerId());
        return application;
    }

    private LoanProduct requiredProduct(String productCode) {
        return productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan product not found"));
    }

    private void validateAgainstProduct(LoanProduct product, java.math.BigDecimal amount, int tenorMonths) {
        if (!product.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Loan product is inactive");
        }
        if (amount.compareTo(product.getMinAmount()) < 0 || amount.compareTo(product.getMaxAmount()) > 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Requested amount is outside product range");
        }
        if (tenorMonths < product.getMinTenorMonths() || tenorMonths > product.getMaxTenorMonths()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Requested tenor is outside product range");
        }
    }

    private void assertVersion(LoanApplication application, long expectedVersion) {
        if (application.getVersion() != expectedVersion) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Application version is stale");
        }
    }

    private void requireOwner(String actorId, String ownerCustomerId) {
        if (!actorId.equals(ownerCustomerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Application belongs to another customer");
        }
    }

    private String nextApplicationNo() {
        Long sequence = jdbcTemplate.queryForObject("SELECT nextval('application_no_seq')", Long.class);
        return "LAP-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-%08d".formatted(sequence);
    }

    private String serialize(ApplicationSubmittedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize application event", exception);
        }
    }
}
