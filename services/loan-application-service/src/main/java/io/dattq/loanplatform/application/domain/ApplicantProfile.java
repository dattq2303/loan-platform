package io.dattq.loanplatform.application.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "applicant_profile")
public class ApplicantProfile {
    @Id
    @Column(name = "applicant_profile_id")
    private UUID applicantProfileId;
    @OneToOne
    @JoinColumn(name = "loan_application_id", nullable = false, unique = true)
    private LoanApplication loanApplication;
    @Column(name = "customer_id")
    private String customerId;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(name = "monthly_income")
    private BigDecimal monthlyIncome;
    @Column(name = "employment_type")
    private String employmentType;

    protected ApplicantProfile() {
    }

    ApplicantProfile(LoanApplication loanApplication, String customerId, String fullName,
                     LocalDate dateOfBirth, BigDecimal monthlyIncome, String employmentType) {
        this.applicantProfileId = UUID.randomUUID();
        this.loanApplication = loanApplication;
        update(customerId, fullName, dateOfBirth, monthlyIncome, employmentType);
    }

    void update(String customerId, String fullName, LocalDate dateOfBirth,
                BigDecimal monthlyIncome, String employmentType) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.monthlyIncome = monthlyIncome;
        this.employmentType = employmentType;
    }

    public String getCustomerId() { return customerId; }
    public String getFullName() { return fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public String getEmploymentType() { return employmentType; }
}
