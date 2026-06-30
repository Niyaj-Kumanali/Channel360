package com.channel360.workflow.application.runtime;

import com.channel360.workflow.infrastructure.persistence.repository.WorkflowRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final ConcurrentHashMap<String, Boolean> processedKeys = new ConcurrentHashMap<>();
    private final WorkflowRequestRepository requestRepository;

    public boolean isProcessed(String idempotencyKey) {
        if (idempotencyKey == null) return false;
        if (processedKeys.containsKey(idempotencyKey)) return true;
        return requestRepository.findByIdempotencyKey(idempotencyKey).isPresent();
    }

    public void markProcessed(String idempotencyKey) {
        if (idempotencyKey != null) {
            processedKeys.put(idempotencyKey, true);
        }
    }
}
