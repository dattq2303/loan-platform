package io.dattq.loanplatform.workflow.service;

import io.dattq.loanplatform.workflow.api.WorkflowCaseView;
import io.dattq.loanplatform.workflow.domain.WorkflowCase;
import io.dattq.loanplatform.workflow.repository.WorkflowCaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class WorkflowCaseQueryService {
    private final WorkflowCaseRepository workflowCaseRepository;

    public WorkflowCaseQueryService(WorkflowCaseRepository workflowCaseRepository) {
        this.workflowCaseRepository = workflowCaseRepository;
    }

    @Transactional(readOnly = true)
    public WorkflowCaseView getByApplicationId(UUID applicationId) {
        WorkflowCase workflowCase = workflowCaseRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow case not found"));
        return new WorkflowCaseView(
                workflowCase.getApplicationId(),
                workflowCase.getApplicationNo(),
                workflowCase.getCustomerId(),
                workflowCase.getCaseStatus(),
                workflowCase.getCreatedAt()
        );
    }
}
