package io.dattq.loanplatform.notification.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dattq.loanplatform.notification.domain.Notification;
import io.dattq.loanplatform.notification.repository.NotificationRepository;
import io.dattq.loanplatform.notification.repository.ProcessedEventRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ApplicationSubmittedListener {
    private final ObjectMapper objectMapper;
    private final ProcessedEventRepository processedEventRepository;
    private final NotificationRepository notificationRepository;

    public ApplicationSubmittedListener(ObjectMapper objectMapper, ProcessedEventRepository processedEventRepository,
                                        NotificationRepository notificationRepository) {
        this.objectMapper = objectMapper;
        this.processedEventRepository = processedEventRepository;
        this.notificationRepository = notificationRepository;
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
        notificationRepository.save(new Notification(event.applicationId(), event.customerId(), APPLICATION_SUBMITTED,
                "Your loan application " + event.applicationNo() + " was submitted."));
    }

    private static final String APPLICATION_SUBMITTED = "APPLICATION_SUBMITTED";

    private record ApplicationSubmittedEvent(UUID eventId, String eventType, UUID applicationId,
                                             String applicationNo, String customerId) {
    }
}
