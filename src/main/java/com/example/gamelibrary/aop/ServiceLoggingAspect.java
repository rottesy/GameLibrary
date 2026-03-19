package com.example.gamelibrary.aop;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {
    private static final long SLOW_THRESHOLD_MS = 500L;
    private static final long VERY_SLOW_THRESHOLD_MS = 1_000L;

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {
    }

    @Around("serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethodName = className + "." + methodName;
        StopWatch stopWatch = new StopWatch(fullMethodName);

        try {
            stopWatch.start();
            log.debug("Executing {} with args {}", fullMethodName, Arrays.deepToString(joinPoint.getArgs()));

            Object result = joinPoint.proceed();

            stopWatch.stop();
            logExecutionDuration(fullMethodName, stopWatch.getTotalTimeMillis());
            return result;
        } catch (Throwable ex) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            log.error("Method {} failed after {} ms", fullMethodName, stopWatch.getTotalTimeMillis(), ex);
            throw ex;
        }
    }

    private void logExecutionDuration(String methodName, long executionTimeMs) {
        if (executionTimeMs > VERY_SLOW_THRESHOLD_MS) {
            log.warn("Method {} completed in {} ms", methodName, executionTimeMs);
        } else if (executionTimeMs > SLOW_THRESHOLD_MS) {
            log.info("Method {} completed in {} ms", methodName, executionTimeMs);
        } else {
            log.debug("Method {} completed in {} ms", methodName, executionTimeMs);
        }
    }
}
