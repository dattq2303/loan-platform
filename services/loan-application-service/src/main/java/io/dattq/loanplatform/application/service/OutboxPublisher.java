package io.dattq.loanplatform.application.service;

import io.dattq.loanplatform.application.domain.OutboxEvent;
import io.dattq.loanplatform.application.repository.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
public class OutboxPublisher {
    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(OutboxEventRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${platform.outbox.publish-delay-ms}")
    @Transactional
    public void publishPendingEvents() {
        for (OutboxEvent event : outboxRepository.lockPendingBatch(20)) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getEventKey(), event.getPayload()).get(10, TimeUnit.SECONDS);
                event.markPublished();
            } catch (Exception exception) {
                throw new IllegalStateException("Kafka publication failed; outbox row will remain pending", exception);
            }
        }
    }
}
