package com.ai.usersentiments.infrastructure.aop;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class LlmMetricsAspect {
    private final MeterRegistry registry;

    @Around("execution(* com.ai.modules.providers.*Provider.generate(..))")
    public Object measureLLMCallLatency(ProceedingJoinPoint pjp) throws Throwable{
        long start = System.nanoTime();
        String providerName = pjp.getTarget()
                .getClass()
                .getSimpleName();
        try{
            return pjp.proceed();
        } catch (Exception e) {
            registry.counter("llm_errors_total", "provider", providerName).increment();
            log.error("Error in LLM provider {}: {}", providerName, e.getMessage());
            throw new RuntimeException(e);
        }finally {
            long duration = System.nanoTime() - start;

            registry.timer("llm_response_latency", "provider", providerName)
                    .record(duration, TimeUnit.NANOSECONDS);

            log.info("LLM call [{}] took {} ms", providerName, duration / 1_000_000);
        }
    }
}
