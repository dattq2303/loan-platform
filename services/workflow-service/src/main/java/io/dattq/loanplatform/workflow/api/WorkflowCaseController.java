package io.dattq.loanplatform.workflow.api;

import io.dattq.loanplatform.workflow.domain.WorkflowCase;
import io.dattq.loanplatform.workflow.repository.WorkflowCaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workflow-cases")
public class WorkflowCaseController {
    private final WorkflowCaseRepository repository;

    public WorkflowCaseController(WorkflowCaseRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{applicationId}")
    public WorkflowCaseView get(@PathVariable UUID applicationId) {
        WorkflowCase workflowCase = repository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow case not found"));
        return new WorkflowCaseView(workflowCase.getApplicationId(), workflowCase.getApplicationNo(),
                workflowCase.getCustomerId(), workflowCase.getCaseStatus(), workflowCase.getCreatedAt());
    }

    public record WorkflowCaseView(UUID applicationId, String applicationNo, String customerId,
                                   String status, Instant createdAt) { }
}
