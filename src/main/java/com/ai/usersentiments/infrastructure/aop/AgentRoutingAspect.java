package com.ai.usersentiments.infrastructure.aop;

import com.ai.usersentiments.infrastructure.metrics.RoutingMetricsRecorder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AgentRoutingAspect {
    private final RoutingMetricsRecorder routingMetrics;

    @Around("execution(* com.ai.langgraph.nodes.*Node.execute(..))")
    public Object trackAgentRouting(ProceedingJoinPoint pjp) throws Throwable {
        String agent = pjp.getTarget().getClass().getSimpleName();
        long start = System.currentTimeMillis();

        try {
            Object result = pjp.proceed();
            routingMetrics.recordSuccess(agent);
            return result;
        } catch (Exception e) {
            routingMetrics.recordFailure(agent);
            log.error("Agent {} failed: {}", agent, e.getMessage());
            throw e;
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            routingMetrics.recordLatency(agent, elapsed);

            log.info("Agent [{}] executed in {} ms", agent, elapsed);
        }
    }
}
