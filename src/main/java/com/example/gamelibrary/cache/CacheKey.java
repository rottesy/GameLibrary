package com.example.gamelibrary.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record CacheKey(Class<?> entityClass, String methodName, List<Object> args) {
    public CacheKey {
        Objects.requireNonNull(entityClass, "entityClass must not be null");
        Objects.requireNonNull(methodName, "methodName must not be null");
        args = args == null
                ? List.of()
                : Collections.unmodifiableList(new ArrayList<>(args));
    }

    public CacheKey(Class<?> entityClass, String methodName, Object... args) {
        this(entityClass, methodName, args == null ? List.of() : Arrays.asList(args.clone()));
    }
}
