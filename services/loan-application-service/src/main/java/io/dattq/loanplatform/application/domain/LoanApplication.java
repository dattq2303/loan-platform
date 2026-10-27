package io.dattq.loanplatform.application.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "loan_application")
public class LoanApplication {
    @Id
    @Column(name = "application_id")
    private UUID applicationId;
    @Column(name = "application_no")
    private String applicationNo;
    @Column(name = "owner_customer_id")
    private String ownerCustomerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private LoanProduct product;
    @Column(name = "requested_amount")
    private BigDecimal requestedAmount;
    @Column(name = "tenor_months")
    private int tenorMonths;
    @Enumerated(EnumType.STRING)
    @Column(name = "application_status")
    private ApplicationStatus applicationStatus;
    @Column(name = "submission_channel")
    private String submissionChannel;
    @Column(name = "submitted_at")
    private Instant submittedAt;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;
    @Version
    private long version;
    @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private ApplicantProfile applicantProfile;

    protected LoanApplication() {
    }

    public LoanApplication(String applicationNo, String ownerCustomerId, LoanProduct product,
                           BigDecimal requestedAmount, int tenorMonths, String applicantCustomerId,
                           String fullName, LocalDate dateOfBirth, BigDecimal monthlyIncome,
                           String employmentType) {
        this.applicationId = UUID.randomUUID();
        this.applicationNo = applicationNo;
        this.ownerCustomerId = ownerCustomerId;
        this.applicationStatus = ApplicationStatus.DRAFT;
        this.createdAt = Instant.now();
        applyDraftChanges(product, requestedAmount, tenorMonths, applicantCustomerId, fullName,
                dateOfBirth, monthlyIncome, employmentType);
    }

    public void applyDraftChanges(LoanProduct product, BigDecimal requestedAmount, int tenorMonths,
                                  String applicantCustomerId, String fullName, LocalDate dateOfBirth,
                                  BigDecimal monthlyIncome, String employmentType) {
        ensureDraft();
        this.product = product;
        this.requestedAmount = requestedAmount;
        this.tenorMonths = tenorMonths;
        if (applicantProfile == null) {
            applicantProfile = new ApplicantProfile(this, applicantCustomerId, fullName, dateOfBirth,
                    monthlyIncome, employmentType);
        } else {
            applicantProfile.update(applicantCustomerId, fullName, dateOfBirth, monthlyIncome, employmentType);
        }
        this.updatedAt = Instant.now();
    }

    public void submit(String channel) {
        ensureDraft();
        this.applicationStatus = ApplicationStatus.SUBMITTED;
        this.submissionChannel = channel;
        this.submittedAt = Instant.now();
        this.updatedAt = submittedAt;
    }

    private void ensureDraft() {
        if (applicationStatus != null && applicationStatus != ApplicationStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT applications can be changed or submitted");
        }
    }

    public UUID getApplicationId() { return applicationId; }
    public String getApplicationNo() { return applicationNo; }
    public String getOwnerCustomerId() { return ownerCustomerId; }
    public LoanProduct getProduct() { return product; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public int getTenorMonths() { return tenorMonths; }
    public ApplicationStatus getApplicationStatus() { return applicationStatus; }
    public String getSubmissionChannel() { return submissionChannel; }
    public Instant getSubmittedAt() { return submittedAt; }
    public long getVersion() { return version; }
    public ApplicantProfile getApplicantProfile() { return applicantProfile; }
}
