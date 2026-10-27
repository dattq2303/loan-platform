package io.dattq.loanplatform.workflow.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workflow_case")
public class WorkflowCase {
    @Id
    @Column(name = "workflow_case_id")
    private UUID workflowCaseId;
    @Column(name = "application_id")
    private UUID applicationId;
    @Column(name = "application_no")
    private String applicationNo;
    @Column(name = "customer_id")
    private String customerId;
    @Column(name = "case_status")
    private String caseStatus;
    @Column(name = "created_at")
    private Instant createdAt;

    protected WorkflowCase() {
    }

    public WorkflowCase(UUID applicationId, String applicationNo, String customerId) {
        this.workflowCaseId = UUID.randomUUID();
        this.applicationId = applicationId;
        this.applicationNo = applicationNo;
        this.customerId = customerId;
        this.caseStatus = "PENDING_INTAKE";
        this.createdAt = Instant.now();
    }

    public UUID getApplicationId() { return applicationId; }
    public String getApplicationNo() { return applicationNo; }
    public String getCustomerId() { return customerId; }
    public String getCaseStatus() { return caseStatus; }
    public Instant getCreatedAt() { return createdAt; }
}
