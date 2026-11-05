package io.dattq.loanplatform.workflow.api;

import io.dattq.loanplatform.workflow.service.WorkflowCaseQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workflow-cases")
public class WorkflowCaseController {
    private final WorkflowCaseQueryService workflowCaseQueryService;

    public WorkflowCaseController(WorkflowCaseQueryService workflowCaseQueryService) {
        this.workflowCaseQueryService = workflowCaseQueryService;
    }

    @GetMapping("/{applicationId}")
    public WorkflowCaseView get(@PathVariable("applicationId") UUID applicationId) {
        return workflowCaseQueryService.getByApplicationId(applicationId);
    }
}
