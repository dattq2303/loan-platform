package io.dattq.loanplatform.workflow.repository;

import io.dattq.loanplatform.workflow.domain.WorkflowCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkflowCaseRepository extends JpaRepository<WorkflowCase, UUID> {
    Optional<WorkflowCase> findByApplicationId(UUID applicationId);
}
