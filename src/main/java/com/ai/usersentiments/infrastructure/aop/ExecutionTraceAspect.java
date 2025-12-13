package com.ai.usersentiments.infrastructure.aop;

import com.ai.usersentiments.infrastructure.metrics.WorkFlowMetricsRecorder;
import com.ai.usersentiments.infrastructure.metrics.WorkflowAuditService;
import com.ai.usersentiments.langgraph.model.WorkFlowState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ExecutionTraceAspect {
    private static final String TRACE_ID = "traceId";

    private final WorkflowAuditService auditService;
    private final WorkFlowMetricsRecorder metricsRecorder;

    @Around("execution(* com.ai.usersentiments.langgraph.WorkflowGraph.run(..))")
    public Object traceWorkflow(ProceedingJoinPoint pjp) throws Throwable {
        String traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ID, traceId);

        String method = pjp.getSignature().toShortString();
        Object[] args = pjp.getArgs();
        String userMessage = (args != null && args.length > 0) ? String.valueOf(args[0]) : "<no-arg>";

        log.info("[WF-START] {} traceId={} message={}", method, traceId, userMessage);
        auditService.startExecution(traceId, userMessage);

        long startMs = System.currentTimeMillis();

        try {
            Object result = pjp.proceed();

            if (result instanceof WorkFlowState state) {
                long latency = System.currentTimeMillis() - startMs;

                log.info("[WF-END] traceId={} sentiment={} response={}",
                        traceId, state.sentiment(), state.agentResponse());

                auditService.finishExecution(state, true);


                metricsRecorder.recordExecution(
                        true,
                        state.sentiment() != null ? state.sentiment().name() : null,
                        latency
                );
            } else {
                long latency = System.currentTimeMillis() - startMs;
                log.info("[WF-END] traceId={} result={}", traceId, result);


                metricsRecorder.recordExecution(true, null, latency);
            }
            return result;

        } catch (Throwable ex) {
            long latency = System.currentTimeMillis() - startMs;

            log.error("[WF-ERROR] traceId={} error={}", traceId, ex.getMessage(), ex);
            auditService.finishExecution(new WorkFlowState(userMessage), false);


            metricsRecorder.recordExecution(false, null, latency);

            throw ex;
        } finally {
            MDC.remove(TRACE_ID);
        }
    }

    @Around("execution(* com.ai.usersentiments.langgraph.nodes.*Node.execute(..))")
    public Object traceNode(ProceedingJoinPoint pjp) throws Throwable {
        String traceId = MDC.get(TRACE_ID); // may be null if not invoked from WorkflowGraph
        String nodeName = pjp.getTarget().getClass().getSimpleName();
        boolean success = true;

        log.info("[NODE-ENTER] node={} traceId={} args={}", nodeName, traceId, pjp.getArgs());
        long start = System.currentTimeMillis();

        Object result = null;
        try {
            result = pjp.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("[NODE-EXIT] node={} traceId={} elapsedMs={} result={}",
                    nodeName, traceId, elapsed, result);
            return result;
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[NODE-ERROR] node={} traceId={} elapsedMs={} error={}",
                    nodeName, traceId, elapsed, ex.getMessage(), ex);
            success = false;
            throw ex;
        } finally {
            if(result instanceof WorkFlowState state) {
                auditService.recordStep(nodeName, state, success);
            }
        }
    }
}
