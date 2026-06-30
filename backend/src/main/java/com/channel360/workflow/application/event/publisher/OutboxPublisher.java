package com.channel360.workflow.application.event.publisher;

import com.channel360.workflow.application.outbox.OutboxService;
import com.channel360.workflow.domain.entity.OutboxEvent;
import com.channel360.workflow.domain.enums.OutboxStatus;
import com.channel360.workflow.infrastructure.persistence.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxRepository;
    private final OutboxService outboxService;

    public void publishAfterCommit(OutboxEvent event) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    log.debug("Publishing outbox event: {}", event.getId());
                    outboxService.markPublished(event.getId());
                } catch (Exception e) {
                    log.error("Failed to publish outbox event {}: {}", event.getId(), e.getMessage());
                    outboxService.markFailed(event.getId(), e.getMessage());
                }
            }
        });
    }

    public void retryFailedEvents(int maxRetries) {
        var failedEvents = outboxRepository.findPendingEvents(LocalDateTime.now(), maxRetries, 50);
        for (var event : failedEvents) {
            try {
                log.info("Retrying outbox event: {}", event.getId());
                outboxService.markPublished(event.getId());
            } catch (Exception e) {
                log.error("Retry failed for outbox event {}: {}", event.getId(), e.getMessage());
                outboxService.markFailed(event.getId(), e.getMessage());
                if (event.getRetryCount() >= maxRetries) {
                    log.warn("Outbox event {} exceeded max retries, moving to DLQ", event.getId());
                }
            }
        }
    }
}
