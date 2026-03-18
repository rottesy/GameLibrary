package com.example.gamelibrary.cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheManager {

    private final Map<CacheKey, Object> storage = new HashMap<>();

    @SuppressWarnings("unchecked")
    public synchronized <T> T computeIfAbsent(CacheKey key, Supplier<T> supplier) {
        if (storage.containsKey(key)) {
            log.info("[CACHE HIT] key={}, size={}", formatKey(key), storage.size());
            return (T) storage.get(key);
        }
        log.info("[CACHE MISS] key={}, size={}", formatKey(key), storage.size());
        T result = supplier.get();
        storage.put(key, result);
        log.info("[CACHE PUT] key={}, size={}", formatKey(key), storage.size());
        return result;
    }

    public synchronized void invalidate(Class<?>... entityClasses) {
        var classesList = Arrays.asList(entityClasses);
        int sizeBefore = storage.size();
        storage.keySet().removeIf(key -> classesList.contains(key.entityClass()));
        int removedKeys = sizeBefore - storage.size();
        log.info(
                "[CACHE INVALIDATE] entities={}, removedKeys={}, size={}",
                classesList.stream().map(Class::getSimpleName).toList(),
                removedKeys,
                storage.size()
        );
    }

    private String formatKey(CacheKey key) {
        return "%s.%s%s".formatted(
                key.entityClass().getSimpleName(),
                key.methodName(),
                key.args()
        );
    }
}
