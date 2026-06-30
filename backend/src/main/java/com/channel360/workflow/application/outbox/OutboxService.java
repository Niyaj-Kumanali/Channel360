package com.channel360.workflow.application.outbox;

import com.channel360.workflow.application.serialization.JsonSerializer;
import com.channel360.workflow.domain.entity.OutboxEvent;
import com.channel360.workflow.domain.enums.OutboxStatus;
import com.channel360.workflow.infrastructure.persistence.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxRepository;
    private final JsonSerializer jsonSerializer;

    @Transactional
    public void saveEvent(String aggregateType, Long aggregateId, String eventType, Object payload) {
        OutboxEvent event = OutboxEvent.builder()
            .aggregateType(aggregateType)
            .aggregateId(aggregateId)
            .eventType(eventType)
            .payload(jsonSerializer.toJson(payload))
            .status(OutboxStatus.PENDING)
            .retryCount(0)
            .build();
        outboxRepository.save(event);
    }

    @Transactional
    public void markPublished(Long eventId) {
        outboxRepository.findById(eventId).ifPresent(event -> {
            event.setStatus(OutboxStatus.PUBLISHED);
            event.setPublishedAt(LocalDateTime.now());
        });
    }

    @Transactional
    public void markFailed(Long eventId, String error) {
        outboxRepository.findById(eventId).ifPresent(event -> {
            event.setStatus(OutboxStatus.FAILED);
            event.setRetryCount(event.getRetryCount() + 1);
            event.setLastError(error);
            event.setNextRetryAt(LocalDateTime.now().plusMinutes(5));
        });
    }
}
