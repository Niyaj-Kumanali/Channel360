package com.channel360.workflow.infrastructure.scheduler;

import com.channel360.workflow.application.outbox.OutboxService;
import com.channel360.workflow.infrastructure.persistence.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPoller {

    private final OutboxEventRepository outboxRepository;
    private final OutboxService outboxService;

    @Scheduled(fixedRate = 5000)
    public void poll() {
        var events = outboxRepository.findPendingEvents(LocalDateTime.now(), 5, 50);
        for (var event : events) {
            try {
                log.debug("Publishing outbox event: {}", event.getId());
                outboxService.markPublished(event.getId());
            } catch (Exception e) {
                log.error("Failed to publish outbox event {}", event.getId(), e);
                outboxService.markFailed(event.getId(), e.getMessage());
            }
        }
    }
}
