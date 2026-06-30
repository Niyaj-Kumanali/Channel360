package com.channel360.workflow.application.engine.resolver;

import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ApproverResolutionCache {

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final Duration ttl = Duration.ofSeconds(30);

    public List<Long> get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) return null;
        if (Instant.now().isAfter(entry.expiresAt())) {
            cache.remove(key);
            return null;
        }
        return entry.userIds();
    }

    public void put(String key, List<Long> userIds) {
        cache.put(key, new CacheEntry(userIds, Instant.now().plus(ttl)));
    }

    public void invalidate(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    private record CacheEntry(List<Long> userIds, Instant expiresAt) {}
}
