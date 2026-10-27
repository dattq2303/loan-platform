package io.dattq.loanplatform.workflow.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dattq.loanplatform.workflow.domain.WorkflowCase;
import io.dattq.loanplatform.workflow.repository.ProcessedEventRepository;
import io.dattq.loanplatform.workflow.repository.WorkflowCaseRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ApplicationSubmittedListener {
    private final ObjectMapper objectMapper;
    private final ProcessedEventRepository processedEventRepository;
    private final WorkflowCaseRepository workflowCaseRepository;

    public ApplicationSubmittedListener(ObjectMapper objectMapper, ProcessedEventRepository processedEventRepository,
                                        WorkflowCaseRepository workflowCaseRepository) {
        this.objectMapper = objectMapper;
        this.processedEventRepository = processedEventRepository;
        this.workflowCaseRepository = workflowCaseRepository;
    }

    @KafkaListener(topics = "${platform.kafka.topics.loan-application-events}")
    @Transactional
    public void consume(String payload) throws Exception {
        ApplicationSubmittedEvent event = objectMapper.readValue(payload, ApplicationSubmittedEvent.class);
        if (!"ApplicationSubmitted".equals(event.eventType())) {
            return;
        }
        if (processedEventRepository.recordIfAbsent(event.eventId()) == 0) {
            return;
        }
        workflowCaseRepository.save(new WorkflowCase(event.applicationId(), event.applicationNo(), event.customerId()));
    }

    private record ApplicationSubmittedEvent(UUID eventId, String eventType, UUID applicationId,
                                             String applicationNo, String customerId) {
    }
}
